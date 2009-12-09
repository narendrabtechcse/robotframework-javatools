import os
import re
import tempfile
import time

from robot.utils import NormalizedDict, timestr_to_secs
from robot.running import NAMESPACES
from robot.running.namespace import IMPORTER
from robot.libraries.BuiltIn import BuiltIn

from org.springframework.beans.factory import BeanCreationException
from org.springframework.remoting import RemoteAccessException
from org.springframework.remoting.rmi import RmiProxyFactoryBean

from org.robotframework.jvmconnector.client import RobotRemoteLibrary
from org.robotframework.jvmconnector.server import RmiInfoStorage, LibraryImporter


class InvalidURLException(Exception):
    pass


DATABASE = os.path.join(tempfile.gettempdir(), 'launcher.txt')


class RemoteLibrary:

    #TODO: timeout?
    def __init__(self, uri):
        self._uri = uri
        self._open_connection()
        self._keywords = None

    def _open_connection(self):
        self._remote_lib = RobotRemoteLibrary(self._uri)

    def get_keyword_names(self):
        if self._keywords is None:
            #FIXME: Normalize
            self._keywords = list(self._remote_lib.getKeywordNames())
        return self._keywords

    def has_keyword(self, name):
        #FIXME: Normalize
        return name in self._keywords

    def run_keyword(self, name, args):
        try:
            return self._remote_lib.runKeyword(name, args)
        except RemoteAccessException:
            print '*DEBUG* Reconnecting to remote library.'
            self._open_connection()
            return self._remote_lib.runKeyword(name, args)


class RemoteApplication:
    ROBOT_LIBRARY_SCOPE = 'GLOBAL'

    def __init__(self):
        self._libs = []
        self._rmi_client = None
        self._name = None

    def application_started(self, name=None, timeout='60 seconds', rmi_url=None):
        if self._rmi_client:
            raise RuntimeError("Application already connected")
        self._name = name
        timeout = timestr_to_secs(timeout or '60 seconds')
        self._rmi_client = self._connect_to_base_rmi_service(name, timeout, rmi_url)

    def _connect_to_base_rmi_service(self, name, timeout, rmi_url): 
        start_time = time.time()
        while time.time() - start_time < timeout:
            url = self._retrieve_base_rmi_url(rmi_url)
            try:
                print "*TRACE* Trying to connect to rmi url '%s'" % url
                return self._create_rmi_client(url)
            except (BeanCreationException, RemoteAccessException,
                    InvalidURLException):
                time.sleep(2)
        self._could_not_connect(name)

    def _retrieve_base_rmi_url(self, url):
        #TODO: Get from started apps DB
        return url or RmiInfoStorage(DATABASE).retrieve()

    def _create_rmi_client(self, url):
        if not re.match('rmi://[^:]+:\d{1,5}/.*', url):
            raise InvalidURLException()
        rmi_client = RmiProxyFactoryBean(serviceUrl=url,
                                         serviceInterface=LibraryImporter)
        rmi_client.prepare()
        rmi_client.afterPropertiesSet()
        self._save_base_url_and_clean_db(url)
        return rmi_client

    def _save_base_url_and_clean_db(self, url):
        self._rmi_url = url
        if os.path.exists(DATABASE):
            os.remove(DATABASE)

    def _could_not_connect(self, name):
        name = name or ''
        raise RuntimeError("Could not connect to application %s" % name)

    def take_libraries_into_use(self, *library_names):
        for name in library_names:
            self.take_library_into_use(name)

    def take_library_into_use(self, library_name):
        #TODO: Add support for arguments
        self._check_connection()
        library_url = self._import_remote_library(library_name)
        library = RemoteLibrary(library_url)
        self._libs.append(library)
        return library.get_keyword_names()

    def _check_connection(self):
        if self._rmi_client is None:
            raise RuntimeError("No connection established. Use keyword " +
                               "'Application Started' before this keyword.")

    def _import_remote_library(self, library_name): 
        try:
            return self._rmi_client.getObject().importLibrary(library_name)
        except (BeanCreationException, RemoteAccessException):
            self._could_not_connect(self._name)

    def close_application(self):
        self._check_connection()
        try:
            self._rmi_client.getObject().closeService()
        except RemoteAccessException:
            self._rmi_client = None
            return
        raise RuntimeError('Could not close application.')

    def get_keyword_names(self):
        kws = []
        for lib in self._libs:
            kws.extend(lib.get_keyword_names())
        return kws

    def run_keyword(self, name, args):
        for lib in self._libs:
            if lib.has_keyword(name):
                return lib.run_keyword(name, args)


class RemoteApplicationsConnector:
    _kws = ['applicationstarted', 'switchtoapplication', 'closeallapplications',
            'closeapplication', 'takelibrariesintouse', 'takelibraryintouse']

    def __init__(self):
        self._initialize()

    def _initialize(self):
        self._apps = NormalizedDict()
        self._active_app = None

    def application_started(self, name, timeout='60 seconds', rmi_url=None):
        """Makes active"""
        if self._apps.has_key(name):
            raise RuntimeError("Application with name '%s' already in use" % name)
        app = RemoteApplication()
        app.application_started(name, timeout, rmi_url)
        self._apps[name] = app
        self._active_app = app

    def switch_to_application(self, name):
        self._check_application_in_use(name)
        self._active_app = self._apps[name]

    def _check_application_in_use(self, name):
        if not self._apps.has_key(name):
            raise RuntimeError("No Application with name '%s' in use" % name)

    def take_libraries_into_use(self, *library_names):
        self._check_active_app()
        self._active_app.take_libraries_into_use(*library_names)
        self._update_keywords_to_robot()

    def _check_active_app(self):
        if self._active_app is None:
            raise RuntimeError("No application selected")

    def _update_keywords_to_robot(self):
        # TODO: When Robot has better support for reimporting libraries,
        # update following code to use that approach. See RF issue 293.
        lib_name = os.path.splitext(os.path.basename(__file__))[0]
        self._remove_lib_from_current_namespace(lib_name)
        self._remove_lib_from_importer(lib_name, [])
        BuiltIn().import_library(lib_name)

    def _remove_lib_from_current_namespace(self, name):
        testlibs = NAMESPACES.current._testlibs
        if testlibs.has_key(name):
            del(testlibs[name])

    def _remove_lib_from_importer(self, name, args):
        if IMPORTER._libraries.has_key((name, tuple(args))):
            del(IMPORTER._libraries[(name, tuple(args))])
        elif IMPORTER._libraries.has_key((name, args)):
                index = IMPORTER._libraries._keys.index((name, args))
                IMPORTER._libraries._keys.pop(index)
                IMPORTER._libraries._libs.pop(index)

    def take_library_into_use(self, library_name):
        self._check_active_app()
        self._active_app.take_library_into_use(library_name)
        self._update_keywords_to_robot()

    def close_all_applications(self):
        for name in self._apps.keys():
            try:
                self._apps[name].close_application()
            except RuntimeError:
                print "*WARN* Could not close application '%s'" % (name)
        self._initialize()

    def close_application(self, name=None):
        name = name or self._get_active_app_name()
        print "*TRACE* Closing application '%s'" % name
        self._check_application_in_use(name)
        if self._apps[name] == self._active_app:
            self._active_app = None
        try:
            self._apps[name].close_application()
            print "Closed application '%s'" % (name)
        finally:
            print self._apps
            #TODO: Robot normalize
            del(self._apps[name.lower().replace(' ', '').replace('_', '')])

    def _get_active_app_name(self):
        self._check_active_app()
        for name, app in self._apps.items():
            if app == self._active_app:
                return name

    def get_keyword_names(self):
        #FIXME: Handle dublicate kw names
        kws = []
        for app in self._apps.values():
            kws.extend(app.get_keyword_names())
        return kws + self._kws

    def run_keyword(self, name, args):
        print "running keyword: ", name
        if name in self._kws:
            for method in dir(self):
                if method.lower().replace('_', '') == name:
                    return getattr(self, method)(*args)
        return self._active_app.run_keyword(name, args)


class RemoteApplications:
    ROBOT_LIBRARY_SCOPE = 'GLOBAL'
    _connector = RemoteApplicationsConnector()

    def get_keyword_names(self):
        names = self._connector.get_keyword_names()
        print "This library provides keywords: ", ', '.join(names)
        return names

    def run_keyword(self, name, args):
        return self._connector.run_keyword(name, args)
