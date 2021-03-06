import re
import time
import sys
import __builtin__

from os import pathsep, path, remove
from tempfile import gettempdir

from java.lang import Class
from java.lang import System
from java.net import ServerSocket

from robot.utils import timestr_to_secs
from robot.running import NAMESPACES
from robot.running.importer import Importer

from org.springframework.remoting import RemoteAccessException
from org.springframework.beans.factory import BeanCreationException
from org.springframework.remoting.rmi import RmiServiceExporter

from org.robotframework.jvmconnector.client import RobotRemoteLibrary
from org.robotframework.jvmconnector.server import LibraryImporter
from org.robotframework.jvmconnector.server import CloseableRobotRmiService
from org.robotframework.jvmconnector.server import SimpleRobotRmiService
from org.springframework.remoting.rmi import RmiProxyFactoryBean

from robot.libraries.OperatingSystem import OperatingSystem
from robot.libraries.BuiltIn import BuiltIn

class RemoteLibrary:

    def __init__(self, uri):
        self.uri = uri
        self._open_connection()

    def _open_connection(self):
        self.remote_lib = RobotRemoteLibrary(self.uri)
        
    def get_keyword_names(self):
        return list(self.remote_lib.getKeywordNames())

    def run_keyword(self, name, args):
        try:
            return self.remote_lib.runKeyword(name, args)
        except RemoteAccessException:
            print '*DEBUG* Reconnecting to remote library.' 
            self._open_connection()
            return self.remote_lib.runKeyword(name, args)


class FreePortFinder:

    def find_free_port(self, socket=ServerSocket(0)):
        try:
            return socket.getLocalPort()
        finally:
            socket.close()


class LibraryDb:

    def __init__(self, path, fileutil=__builtin__):
        self.path = path
        self.fileutil = fileutil

    def store(self, rmi_info):
        file = self.fileutil.open(self.path, 'w')
        file.write(rmi_info + '\n')
        file.close()

    def retrieve_base_rmi_url(self):
        if self._is_new():
            return ''

        file = self.fileutil.open(self.path, 'r')

        try:
            return file.read().rstrip()
        finally:
            file.close()

    def _is_new(self):
        return not path.exists(self.path)


class MyRmiServicePublisher:

    def __init__(self, class_loader=Class, exporter=RmiServiceExporter(),
                 port_finder=FreePortFinder()):
        self.class_loader = class_loader
        self.exporter = exporter
        self.port_finder = port_finder

    def publish(self, service_name, service, service_interface_name):
        port = self.port_finder.find_free_port()
        service_class = self.class_loader.forName(service_interface_name)

        self.exporter.setServiceName(service_name)
        self.exporter.setRegistryPort(port)
        self.exporter.setService(service)
        self.exporter.setServiceInterface(service_class)
        self.exporter.prepare()
        self.rmi_info = 'rmi://localhost:%s/%s' % (port, service_name)


class RemoteLibraryImporter(LibraryImporter):

    def __init__(self, rmi_publisher=MyRmiServicePublisher(),
                 class_loader=Class):
        self.rmi_publisher = rmi_publisher
        self.class_loader = class_loader

    def importLibrary(self, library_name):
        service_name = re.sub('\.', '', library_name)
        lib = self.class_loader.forName(library_name)()
        service = CloseableRobotRmiService(SimpleRobotRmiService(library=lib))
        interface_name = 'org.robotframework.jvmconnector.server.RobotRmiService'
        self.rmi_publisher.publish(service_name, service, interface_name)
        return self.rmi_publisher.rmi_info

    def closeService(self):
        System.exit(0);


class LibraryImporterPublisher:

    def __init__(self, library_db,
                 rmi_publisher=MyRmiServicePublisher()):
        self.library_db = library_db
        self.rmi_publisher = rmi_publisher

    def publish(self):
        interface_name = 'org.robotframework.jvmconnector.server.LibraryImporter'
        self.rmi_publisher.publish('robotrmiservice', RemoteLibraryImporter(),
                                   interface_name)
        self.library_db.store(self.rmi_publisher.rmi_info)


class RmiWrapper:

    def __init__(self, library_importer_publisher):
        self.library_importer_publisher = library_importer_publisher
        self.class_loader = Class

    def export_rmi_service_and_launch_application(self, application, args):
        self.library_importer_publisher.publish()
        self.class_loader.forName(application).main(args)


class InvalidURLException(Exception):
    pass

DATABASE = path.join(gettempdir(), 'launcher.txt')

class ApplicationLauncher:
    """A library for starting java application in separate JVM and importing
    remote libraries for operating it.

    
    """

    def __init__(self, application, timeout='60 seconds'):
        """ApplicationLauncher takes one mandatory and one optional argument.

        `application` is a required argument, it is the name of the main
        class or the class that has the main method.

        `timeout` is the timeout used to wait for importing a remote library.
        """
        self.application = application
        self.timeout = timestr_to_secs(timeout)
        self.builtin = BuiltIn()
        self.operating_system = OperatingSystem()
        self.rmi_url = None

    def start_application(self, args='', jvm_args=''):
        """Starts the application with given arguments.

        `args` optional application arguments..
        `jvm_args` optional jvm arguments.

        Example:
        | Start Application | one two three | -Dproperty=value |
        """
        pythonpath = self._get_python_path()
        command = 'jython -Dpython.path=%s %s %s %s %s' % (pythonpath,
                  jvm_args, __file__, self.application, args)
        self.operating_system.start_process(command)
        self.application_started()
    
    def import_remote_library(self, library_name, *args):
        """Imports a library with given arguments for the application.
        
        Application needs to be started before using this keyword. In case the
        application is started externally, `Application Started` keyword has
        to be used beforehand. In case there is multiple applications, there
        is need to have one ApplicationLauncher per application. In that case,
        starting application and library imports needs to be in sequence. It is 
        not possible to start multiple applications and then import libraries
        to those.

        Examples:

        | Start Application | arg1 |  
        | Import Remote Library | SwingLibrary |
        
        or

        | Application Started | 
        | Import Remote Library | SwingLibrary |
        """
        library_url = self._run_remote_import(library_name)
        newargs = self._add_name_to_args_if_necessary(library_name, args)
        self._prepare_for_reimport_if_necessary(library_url, *newargs) 
        self.builtin.import_library('ApplicationLauncher.RemoteLibrary',
                                    library_url,
                                    *newargs)

    def close_application(self):
        """Closes the active application.
        
        If same application is opened multiple times, only the latest one is
        closed. Therefore you should close the application before starting it
        again."""
        rmi_client = self._connect_to_base_rmi_service()
        self.rmi_url = None
        try:
            rmi_client.getObject().closeService()
        except RemoteAccessException:
            return
        raise RuntimeError('Could not close application.')
            

    def application_started(self):
        """Notifies ApplicationLauncher that application is launched
        externally.
        
        Required before taking libraries into use with `Import Remote Library` 
        when application is started with ApplicationLauncher.py script.
        """
        self.rmi_url = None
        self._connect_to_base_rmi_service()
        
    def _get_python_path(self):
        for path_entry in sys.path:
            if path.exists(path.join(path_entry, 'robot')):
                return path_entry

    def _add_name_to_args_if_necessary(self, library_name, args):
        if len(args) >= 2 and args[-2].upper() == 'WITH NAME':
            return args

        return sum((args,), ('WITH NAME', library_name))

    def _prepare_for_reimport_if_necessary(self, library_url, *args):
        lib = Importer().import_library('ApplicationLauncher.RemoteLibrary',
                                        sum((args,), (library_url,)))
        testlibs = NAMESPACES.current._testlibs
        if testlibs.has_key(lib.name):
            testlibs.pop(lib.name)

    def _connect_to_base_rmi_service(self): 
        start_time = time.time()
        while time.time() - start_time < self.timeout:
            url = self._retrieve_base_rmi_url()
            try:
                return self._create_rmi_client(url)
            except (BeanCreationException, RemoteAccessException,
                    InvalidURLException):
                time.sleep(2)
        raise RuntimeError('Could not connect to application %s' % self.application)

    def _run_remote_import(self, library_name): 
        try:
            rmi_client = self._connect_to_base_rmi_service()
            return rmi_client.getObject().importLibrary(library_name)
        except (BeanCreationException, RemoteAccessException):
            raise RuntimeError('Could not connect to application %s' % self.application)

    def _retrieve_base_rmi_url(self):
        if self.rmi_url:
            return self.rmi_url

        return LibraryDb(DATABASE).retrieve_base_rmi_url()

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
        self.rmi_url = url
        if path.exists(DATABASE):
            remove(DATABASE)

if __name__ == '__main__':
    if len(sys.argv[1:]) >= 1:
        db = LibraryDb(DATABASE)
        wrapper = RmiWrapper(LibraryImporterPublisher(db))
        wrapper.export_rmi_service_and_launch_application(sys.argv[1],
                                                          sys.argv[2:])

