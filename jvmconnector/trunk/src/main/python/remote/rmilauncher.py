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
    def __init__(self, class_loader=Class, exporter=RmiServiceExporter(), port_finder=FreePortFinder()):
        self.class_loader = class_loader
        self.exporter = exporter
        self.port_finder = port_finder

    def publish(self, service_name, service, service_interface_name):
        self.exporter.setServiceName(service_name)
        port = self.port_finder.find_free_port()
        self.exporter.setRegistryPort(port)
        self.exporter.setService(service)
        self.exporter.setServiceInterface(self.class_loader.forName(service_interface_name))
        self.exporter.prepare()
        self.rmi_url = "rmi://localhost:%s/%s" % (port, service_name)

class LibraryDb:
    pass

class LibraryImporterPublisher:
    def __init__(self, my_rmi_service_publisher=MyRmiServicePublisher(), library_db=LibraryDb()):
        self.my_rmi_service_publisher = my_rmi_service_publisher

    #todo: takes the communication file name as parameter and writes the rmi service url to it
    def publish(self, application):
        self.my_rmi_service_publisher.publish("robotrmiservice", RemoteLibraryImporter(), "org.robotframework.jvmconnector.server.LibraryImporter")

from org.robotframework.jvmconnector.server import LibraryImporter
from org.robotframework.jvmconnector.server import SimpleRobotRmiService
import re
class RemoteLibraryImporter(LibraryImporter):
    def __init__(self, rmi_publisher=MyRmiServicePublisher(), class_loader=Class):
        self.rmi_publisher = rmi_publisher
        self.class_loader = class_loader

    def importLibrary(self, library_name):
        service_name = re.sub('\.', '', library_name)
        service = self.class_loader.forName(library_name)()
        self.rmi_publisher.publish(service_name, service, 'org.robotframework.jvmconnector.server.RobotRmiService')
        return self.rmi_publisher.rmi_url

class RmiWrapper:
    def __init__(self, library_db_path, remote_library_importer=LibraryImporterPublisher(), class_loader=Class):
        remote_library_importer.db_path = library_db_path
        self.remote_library_importer = remote_library_importer
        self.class_loader = class_loader

    def export_rmi_service_and_launch_application(self, application, args):
        self.remote_library_importer.publish(application)
        self.class_loader.forName(application).main(args)

from robot.libraries.OperatingSystem import OperatingSystem
from os import pathsep
class RmiLauncher:
    ROBOT_LIBRARY_SCOPE = 'GLOBAL'

    def __init__(self, os_library=OperatingSystem()):
        self.os_library = os_library

    #todo: - handle args and jvm args
    #      - passes the communication file name as argument to the new process
    def start_application(self, application):
        pythonpath = pathsep.join(sys.path)
        self.os_library.start_process("jython -Dpython.path=%s %s %s" % (pythonpath, __file__, application))
    
    #todo: - use something like RemoteLibrary's open_connection and the rmi url
    #        from the communication file here
    #      - should call RemoteLibraryImporter's importLibrary with 'library_name' (not directly
    #        but with rmi), returns the remote library's rmi url
    def import_remote_library(self, library_name):
        pass

if __name__ == '__main__':
    if len(sys.argv[1:]) >= 1:
        wrapper = RmiWrapper()
        wrapper.export_rmi_service_and_launch_application(sys.argv[1], sys.argv[2:])
