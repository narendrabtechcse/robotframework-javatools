import time
import sys
from robot.utils import timestr_to_secs
from java.lang import Class
from org.springframework.remoting import RemoteConnectFailureException
from org.springframework.beans.factory import BeanCreationException
from org.robotframework.jvmconnector.client import RobotRemoteLibrary

class RemoteLibrary:

    def __init__(self, uri='rmi://localhost:1099/jvmConnector', 
                 timeout="60 seconds"):
        self.uri = uri
        self.timeout = timeout
        self.open_connection()

    def open_connection(self):
        start_time = time.time()
        timeout = timestr_to_secs(self.timeout)
        while time.time() - start_time < timeout:
            try:
                self.remote_lib = RobotRemoteLibrary(self.uri)
                return
            except (BeanCreationException, RemoteConnectFailureException):
                time.sleep(2)
        message = "Could not get connection to '%s' in '%s'!" 
        raise RuntimeError(message %(self.uri, self.timeout))
        
    def get_keyword_names(self):
        return list(self.remote_lib.getKeywordNames())

    def run_keyword(self, name, args):
        try:
            return self.remote_lib.runKeyword(name, args)
        except RemoteConnectFailureException:
            print '*DEBUG* Reconnecting to remote library.' 
            self.open_connection()
            return self.remote_lib.runKeyword(name, args)

from java.net import ServerSocket
class FreePortFinder:
    def find_free_port(self, socket=ServerSocket(0)):
        try:
            return socket.getLocalPort()
        finally:
            socket.close()

from org.springframework.remoting.rmi import RmiServiceExporter
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
        self.rmi_info = "rmi://localhost:%s/%s" % (port, service_name)

class LibraryDb:
    def __init__(self, path):
        self.path = path

    def store(self, application, rmi_info):
        file = open(self.path, 'w')
        file.write("%s:0:%s" % (application, rmi_info))
        file.close()

from org.robotframework.jvmconnector.server import LibraryImporter
from org.robotframework.jvmconnector.server import SimpleRobotRmiService
import re
class RemoteLibraryImporter(LibraryImporter):
    def __init__(self, rmi_publisher=MyRmiServicePublisher(),
                 class_loader=Class):
        self.rmi_publisher = rmi_publisher
        self.class_loader = class_loader

    def importLibrary(self, library_name):
        service_name = re.sub('\.', '', library_name)
        service = self.class_loader.forName(library_name)()
        interface_name = 'org.robotframework.jvmconnector.server.RobotRmiService'
        self.rmi_publisher.publish(service_name, service, interface_name)
        return self.rmi_publisher.rmi_info

class LibraryImporterPublisher:
    def __init__(self, library_db,
                 rmi_publisher=MyRmiServicePublisher()):
        self.library_db = library_db
        self.rmi_publisher = rmi_publisher

    def publish(self, application):
        interface_name = 'org.robotframework.jvmconnector.server.LibraryImporter'
        self.rmi_publisher.publish("robotrmiservice", RemoteLibraryImporter(),
                                   interface_name)
        self.library_db.store(application, self.rmi_publisher.rmi_info)

class RmiWrapper:
    def __init__(self, library_importer_publisher):
        self.library_importer_publisher = library_importer_publisher
        self.class_loader = Class

    def export_rmi_service_and_launch_application(self, application, args):
        self.library_importer_publisher.publish(application)
        self.class_loader.forName(application).main(args)

from robot.libraries.OperatingSystem import OperatingSystem
from os import pathsep
from tempfile import mktemp
class RmiLauncher:
    ROBOT_LIBRARY_SCOPE = 'GLOBAL'

    def __init__(self, os_library=OperatingSystem()):
        self.os_library = os_library
        self.db_path = mktemp('.robot-rmi-launcher')

    def start_application(self, application, args='', jvm_args=''):
        pythonpath = pathsep.join(sys.path)
        command = "jython -Dpython.path=%s %s %s %s %s %s" % (pythonpath,
                  jvm_args, __file__, self.db_path, application, args)
        self.os_library.start_process(command)
    
    #todo: - use something like RemoteLibrary's open_connection and the rmi url
    #        from the communication file here
    #      - should call RemoteLibraryImporter's importLibrary with 'library_name' (not directly
    #        but with rmi), returns the remote library's rmi url
    def import_remote_library(self, library_name):
        pass

if __name__ == '__main__':
    if len(sys.argv[1:]) >= 1:
        db = LibraryDb(sys.argv[1])
        wrapper = RmiWrapper(LibraryImporterPublisher(db))
        wrapper.export_rmi_service_and_launch_application(sys.argv[2],
                                                          sys.argv[2:])
