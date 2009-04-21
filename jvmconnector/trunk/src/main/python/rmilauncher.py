import re
import time
import sys
import __builtin__

from os import pathsep,path
from tempfile import mktemp

from java.lang import Class
from java.net import ServerSocket

from robot.utils import timestr_to_secs
from robot.running import NAMESPACES
from robot.running.importer import Importer

from org.springframework.remoting import RemoteAccessException
from org.springframework.beans.factory import BeanCreationException
from org.springframework.remoting.rmi import RmiServiceExporter

from org.robotframework.jvmconnector.client import RobotRemoteLibrary
from org.robotframework.jvmconnector.server import LibraryImporter
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
        service = SimpleRobotRmiService(library=lib)
        interface_name = 'org.robotframework.jvmconnector.server.RobotRmiService'
        self.rmi_publisher.publish(service_name, service, interface_name)
        return self.rmi_publisher.rmi_info


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


class ApplicationLauncher:
    """A library for starting java applications in separate JVMs and importing
    remote libraries for accessing them.
    """

    def __init__(self, application, timeout='60 seconds',
                 os_library=OperatingSystem(), builtin=BuiltIn()):
        """ApplicationLauncher takes one mandatory and one optional argument.

        `application` is a required argument, it is the name of the main
        class or the class that has the main method.

        `timeout` is the timeout used to wait for importing a remote library.
        """
        self.application = application
        self.timeout = timestr_to_secs(timeout)
        self.os_library = os_library
        self.builtin = builtin
        self.db_path = mktemp('.robot-rmi-launcher')

    def start_application(self, jvm_args='', args=''):
        """Starts the application with given arguments.

        `jvm_args` optional jvm arguments.
        `args` optional application arguments..

        Example:
        | Start Application | -Dproperty=value | one two three | 
        """
        pythonpath = pathsep.join(sys.path)
        command = 'jython -Dpython.path=%s %s %s %s %s %s' % (pythonpath,
                  jvm_args, __file__, self.db_path, self.application, args)
        self.os_library.start_process(command)
    
    def import_remote_library(self, library_name, *args):
        """Imports a library.

        Example:
        | Import Remote Library | SwingLibrary |
        """
        library_url = self._run_remote_import(library_name)
        newargs = self._add_name_to_args_if_necessary(library_name, args)
        self._prepare_for_reimport_if_necessary(library_url, *newargs) 
        self.builtin.import_library('rmilauncher.RemoteLibrary',
                                    library_url,
                                    *newargs)

    def _add_name_to_args_if_necessary(self, library_name, args):
        if len(args) >= 2 and args[-2].upper() == 'WITH NAME':
            return args

        return sum((args,), ('WITH NAME', library_name))

    def _prepare_for_reimport_if_necessary(self, library_url, *args):
        lib = Importer().import_library('rmilauncher.RemoteLibrary',
                                        sum((args,), (library_url,)))
        testlibs = NAMESPACES.current._testlibs
        if testlibs.has_key(lib.name):
            testlibs.pop(lib.name)

    def _run_remote_import(self, library_name): 
        start_time = time.time()
        while time.time() - start_time < self.timeout:
            url = self._retrieve_base_rmi_url()
            try:
                rmi_client = self._create_rmi_client(url)
                return rmi_client.getObject().importLibrary(library_name)
            except (BeanCreationException, RemoteAccessException):
                time.sleep(2)
        raise RuntimeError('Importing %s timed out.' % library_name)

    def _retrieve_base_rmi_url(self):
        return LibraryDb(self.db_path).retrieve_base_rmi_url()

    def _create_rmi_client(self, url):
        rmi_client = RmiProxyFactoryBean(serviceUrl=url,
                                         serviceInterface=LibraryImporter)
        rmi_client.prepare()
        rmi_client.afterPropertiesSet()
        return rmi_client

if __name__ == '__main__':
    if len(sys.argv[1:]) >= 1:
        db = LibraryDb(sys.argv[1])
        wrapper = RmiWrapper(LibraryImporterPublisher(db))
        wrapper.export_rmi_service_and_launch_application(sys.argv[2],
                                                          sys.argv[3:])

