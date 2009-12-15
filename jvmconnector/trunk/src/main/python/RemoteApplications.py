import glob
import os
import re
import tempfile
import time

from java.util.jar import JarFile
from java.util.zip import ZipException
from java.io import IOException

from robot.utils import normalize, NormalizedDict, timestr_to_secs
from robot.running import NAMESPACES
from robot.running.namespace import IMPORTER
from robot.libraries.BuiltIn import BuiltIn
from robot.libraries.OperatingSystem import OperatingSystem

from org.springframework.beans.factory import BeanCreationException
from org.springframework.remoting import RemoteAccessException
from org.springframework.remoting.rmi import RmiProxyFactoryBean

from org.robotframework.jvmconnector.client import RobotRemoteLibrary
from org.robotframework.jvmconnector.server import RmiInfoStorage, LibraryImporter


class InvalidURLException(Exception):
    pass


DATABASE = os.path.join(tempfile.gettempdir(), 'launcher.txt')


def get_arg_spec(method):
    """Returns info about args in a tuple (args, defaults, varargs)

    args     - tuple of all accepted arguments
    defaults - tuple of default values
    varargs  - name of the argument accepting varargs or None
    """
    # Code below is based on inspect module's getargs and getargspec 
    # methods. See their documentation and/or source for more details. 
    func = method.im_func
    co = func.func_code
    number_of_args = co.co_argcount
    args = co.co_varnames[1:number_of_args] #drops 'self' from methods' args
    defaults = func.func_defaults or ()
    if co.co_flags & 4:                      # 4 == CO_VARARGS
        varargs =  co.co_varnames[number_of_args]
    else:
        varargs = None
    return args, defaults, varargs


class RemoteLibrary:

    def __init__(self, uri):
        self._uri = uri
        self._open_connection()
        self._keywords = None

    def _open_connection(self):
        self._remote_lib = RobotRemoteLibrary(self._uri)

    def get_keyword_names(self):
        if self._keywords is None:
            self._keywords = list(self._remote_lib.getKeywordNames())
        return self._keywords

    def has_keyword(self, name):
        return name in self._keywords

    def run_keyword(self, name, args):
        #TODO: Add polling to RemoteLibrary to make it easier to see whether 
        #there is connection or not. If not,  reconnect.
        return self._remote_lib.runKeyword(name, args)


class RemoteApplication:
    ROBOT_LIBRARY_SCOPE = 'GLOBAL'

    def __init__(self):
        self._libs = []
        self._rmi_client = None
        self._alias = None

    def application_started(self, alias=None, timeout='60 seconds', rmi_url=None):
        if self._rmi_client:
            raise RuntimeError("Application already connected")
        self._alias = alias
        timeout = timestr_to_secs(timeout or '60 seconds')
        self._rmi_client = self._connect_to_base_rmi_service(alias, timeout, rmi_url)

    def _connect_to_base_rmi_service(self, alias, timeout, rmi_url): 
        start_time = time.time()
        while time.time() - start_time < timeout:
            url = self._retrieve_base_rmi_url(rmi_url)
            try:
                print "*TRACE* Trying to connect to rmi url '%s'" % url
                return self._create_rmi_client(url)
            except (BeanCreationException, RemoteAccessException,
                    InvalidURLException):
                time.sleep(2)
        self._could_not_connect(alias)

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

    def _could_not_connect(self, alias):
        alias = alias or ''
        raise RuntimeError("Could not connect to application %s" % alias)

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
            self._could_not_connect(self._alias)

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
    """

    RemoteApplications library is used for launching Java applications in a
    separate process and taking Robot Framework (RF) libraries into use to
    operate on them. This is useful when application does something that cannot
    be tested with RF when running inside the same JVM. Such cases are when
    System.exit is called in the SUT, when multiple applications running in
    separate JVMs need to be tested in parallel or when application is started
    using Java Web Start.

    Using RemoteApplications requires that jvm_connector jar file is in
    _CLASSPATH_ environment variable before starting RF test execution. 
    RemoteApplications works with Java 1.5 and newer. Following paragraphs 
    contain generic information about RemoteApplications library. See also 
    keywords' documentation for more detailed information.

    Application can be started using RemoteApplications or any other way e.g.
    using SeleniumLibrary to start Java Web Start application. When the
    application is started using RemoteApplications, command used to start the
    application JVM is given to `Start Application` keyword. In case application
    is started otherwise, RemoteApplications needs to be informed about started
    application so that it can establish connection to it. This is achieved
    using `Application Started` keyword. `Application Started` keyword can be
    also used to connect application started on remote machine.

    After the application is started, there is need to take the needed test 
    libraries into use. That is done using `Take Library Into Use` and `Take 
    Libraries Into Use` keywords. After that, keywords are ready to be used.
    Note that you need to take the libraries into use for every application.

    In case multiple applications are started with RemoteApplications library,
    `Switch To Application` keyword can be used to define which application is
    currently active. Keywords handle always the currently active application.

    `Close Application` is used to close the application. In case application is
    closed using some other keyword, RemoteApplications library needs to be
    informed by using `Close Application` keyword. In case you want to close all
    applications, `Close All Applications` keyword can be used.

    *NOTE:* RemoteApplications cannot be taken into use with _WITH NAME_
    functionality. However, there should not be need for that as the
    RemoteApplications library can handle multiple applications. 


    *ROBOT AGENT*

    Sometimes you cannot start the application locally from command line. In
    that case you need to use Robot Agent which will start testing capabilities
    to the started JVM. Robot Agent works both with java webstart applications
    and standalone java applications. It is taken into use by setting
    environment variable _JAVA_TOOL_OPTIONS_ with value
    _-javaagent:${jvmconnector.jar}=${testing_dependencies_dir}[:PORT=${port}]_
    where _${jvmconnector.jar}_ is the path to the jvmconnector package and
    _${testing_dependencies_dir}_ is the path to the directory containing the
    test library jars. Optionally you can give the _:PORT=${port}_ where the
    _:PORT=_ is separator, and _${port}_ defines the port number where the
    service providing the testing capabilities is started.

    When Robot Agent is used (RemoteApplications uses it internally) and the
    port parameter is not given, rmi_url from where the testing capabilities
    can be accessed is written to file
    _%HOME/.robotframework/jvmconnector/launched.txt_ or to file
    _%APPDATA%\\RobotFramework\\jvmconnector\\launched.txt_ on Windows. In case
    application is started on remote machine, this rmi_url needs to be given to
    `Application Started` keyword.

    *NOTE:* Under java 1.5 you have to package the jvmconnector.jar and all your
    testing dependencies into one big jar, you cannot provide the testing jars
    as arguments to the agent due to a limitation in Java 1.5's API.
    """

    def __init__(self):
        self._initialize()
        rf_api_keywords = ['run_keyword', 'get_keyword_documentation',
                           'get_keyword_arguments', 'get_keyword_names']
        self._kws = [ attr for attr in dir(self) if not attr.startswith('_') \
                     and attr not in rf_api_keywords ]

    def _initialize(self):
        self._apps = NormalizedDict()
        self._active_app = None

    def start_application(self, alias, command, timeout='60 seconds', 
                          lib_dir=None, port=None):
        """Starts the application, connects to it and makes it active application.

        `command` is the command used to start the application from the command
        line. It can be any command that finally starts JVM e.g. 'java -jar
        my_application.jar', javaws http://my.domain.fi/my_application.jnlp or 
        'start_my_app.bat'.

        `lib_dir` is path to the directory containing all the test library jar
        files which are required for running the tests. `lib_dir` is needed in
        case libraries are not in the CLASSPATH. When application is started
        using Java Web Start and Java version is 1.6 or higher, `lib_dir` is 
        mandatory. In case you are using 1.5 Java, you should package all these
        libraries to the `jvm_connector_jar` which is set to CLASSPATH before
        starting the test execution.

        When Java Web Start is used to start the application, there is need to
        allow permissions for the testing capabilities. Easiest way to do that
        is to add file .java.policy with following content to $HOME directory or
        %USERPROFILE% directory on Windows:
        | _grant {_
        |     _permission java.security.AllPermission;_
        | _};_
        
        `port` defines the port in which the testing capabilities are started
        on the application. By default port is selected randomly from available
        ports.
        
        *NOTE:* If the application is used to start other applications
        and those applications should be controlled with RemoteApplications, 
        port should NOT be given.
        """
        self._alias_in_use(alias)
        orig_java_tool_options = self._get_java_tool_options()
        os.environ['JAVA_TOOL_OPTIONS'] =  self._get_java_agent(lib_dir, port)
        OperatingSystem().start_process(command)
        os.environ['JAVA_TOOL_OPTIONS'] = orig_java_tool_options
        rmi_url = port and 'rmi://localhost:%s/robotrmiservice' % port or None
        self.application_started(alias, timeout, rmi_url)

    def _alias_in_use(self, alias):
        if self._apps.has_key(alias):
            raise RuntimeError("Application with alias '%s' already in use" % alias)

    def _get_java_tool_options(self):
        if os.environ.has_key('JAVA_TOOL_OPTIONS'):
            return os.environ['JAVA_TOOL_OPTIONS']
        return ''

    def _get_java_agent(self, lib_dir, port):
        lib_dir = lib_dir or ''
        jvm_connector_jar = self._get_jvm_connector_jar()
        port = port and ['PORT=%s' % port] or []
        return '-javaagent:%s=%s' % (jvm_connector_jar, 
                                     os.path.pathsep.join(port + [lib_dir]))

    def _get_jvm_connector_jar(self):
        for jar_file in self._get_jars_from_classpath():
            try:
                premain_class = JarFile(jar_file).getManifest().getMainAttributes().getValue('Premain-Class')
            except ZipException, IOExcetion:
                continue
            if premain_class == 'org.robotframework.jvmconnector.agent.RmiServiceAgent':
                print "*TRACE* Found jvm_connector jar '%s'" % jar_file
                return jar_file
        raise RuntimeError("Could not find jvmconnector jarfile from CLASSPATH")

    def _get_jars_from_classpath(self):
        jars = []
        if os.environ.has_key('CLASSPATH'):
            jars = jars + os.environ['CLASSPATH'].split(os.path.pathsep)
        if os.environ.has_key('classpath'):
            jars = jars + os.environ['classpath'].split(os.path.pathsep)
        return jars

    def application_started(self, alias, timeout='60 seconds', rmi_url=None):
        """Connects to started application and switches to it.

        `alias` is the alias name for the application. When using multiple 
        applications alias is used to switch between them with keyword `Switch 
        To Application`.

        `timeout` is the time to wait the application to be started to the point
        where the testing capabilities are initialized and the connection to
        application can be established.

        `rmi_url` is url that can be used to connect to the application. When
        used locally there is usually no need to give the `rmi_url`. However,
        when the application is running on remote machine, the file based
        mechanism used to find the application is not enough and you need to
        provide the `rmi_url`. Format of the `rmi_url` is 
        'rmi://host:port/robotrmiservice'. See from `Introduction` about
        Robot Agent.
        """
        self._alias_in_use(alias)
        app = RemoteApplication()
        app.application_started(alias, timeout, rmi_url)
        self._apps[alias] = app
        self._active_app = app

    def switch_to_application(self, alias):
        """Changes the application where the keywords are executed.
        
        `alias` is the name of the application and it have been given with the
        `Application Started` keyword."""
        self._check_application_in_use(alias)
        self._active_app = self._apps[alias]

    def _check_application_in_use(self, alias):
        if not self._apps.has_key(alias):
            raise RuntimeError("No Application with alias '%s' in use" % alias)

    def take_libraries_into_use(self, *library_names):
        """Takes the libraries into use at the remote application.
        
        `library_names` contains all the libraries that you want to take into
        use on the remote side. *NOTE:* See 'Start Application' for information
        how to provide library jar files."""
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
        """Takes given library into use.
        
        See `Take Libraries Into Use` keyword for more details.
        """
        self._check_active_app()
        self._active_app.take_library_into_use(library_name)
        self._update_keywords_to_robot()

    def close_all_applications(self):
        """Closes all the applications"""
        for alias in self._apps.keys():
            try:
                self._apps[alias].close_application()
            except RuntimeError:
                print "*WARN* Could not close application '%s'" % (alias)
        self._initialize()

    def close_application(self, alias=None):
        """Closes application.
        
        If `alias` is given, closes application related to the alias.
        Otherwise closes the active application."""
        alias = alias or self._get_active_app_alias()
        print "*TRACE* Closing application '%s'" % alias
        self._check_application_in_use(alias)
        if self._apps[alias] == self._active_app:
            self._active_app = None
        try:
            self._apps[alias].close_application()
            print "Closed application '%s'" % (alias)
        finally:
            del(self._apps[normalize(alias)])

    def _get_active_app_alias(self):
        self._check_active_app()
        for alias, app in self._apps.items():
            if app == self._active_app:
                return alias

    def get_keyword_names(self):
        #FIXME: Handle dublicate kw names
        kws = []
        for app in self._apps.values():
            kws.extend(app.get_keyword_names())
        return kws + self._kws

    def run_keyword(self, name, args):
        method = self._get_method(name)
        if method:
            return method(*args)
        return self._active_app.run_keyword(name, args)

    def _get_method(self, name):
        if name in self._kws:
            return getattr(self, name)
        return None

    def get_keyword_arguments(self, name):
        method = self._get_method(name)
        if method:
            args, defaults, varargs = get_arg_spec(method)
            arguments = list(args[:len(args)-len(defaults)])
            defaults = [ '%s=%s' % (arg, default) for arg, default in zip(list(args[len(arguments):]), list(defaults)) ]
            varargs = varargs and ['*%s' % varargs] or []
            return arguments + defaults + varargs
        return ['*args']

    def get_keyword_documentation(self, name):
        method = self._get_method(name)
        if method:
            return method.__doc__
        return ''


class RemoteApplications:
    ROBOT_LIBRARY_SCOPE = 'GLOBAL'
    _connector = RemoteApplicationsConnector()
    __doc__ = _connector.__doc__

    def get_keyword_names(self):
        names = self._connector.get_keyword_names()
        return names

    def run_keyword(self, name, args):
        return self._connector.run_keyword(name, args)

    def get_keyword_arguments(self, name):
        return self._connector.get_keyword_arguments(name)

    def get_keyword_documentation(self, name):
        return self._connector.get_keyword_documentation(name)
