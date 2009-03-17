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
class MyRmiServiceExporter:
    def __init__(self, class_loader=Class, exporter=RmiServiceExporter(), port_finder=FreePortFinder()):
        self.class_loader = class_loader
        self.exporter = exporter
        self.port_finder = port_finder

    def export(self, service_name, service, service_interface_name):
        self.exporter.setServiceName(service_name)
        port = self.port_finder.find_free_port()
        self.exporter.setRegistryPort(port)
        self.exporter.setService(service)
        self.exporter.setServiceInterface(self.class_loader.forName(service_interface_name))
        self.exporter.prepare()
        self.rmi_url = "rmi://localhost:%s/%s" % (port, service_name)

from org.robotframework.jvmconnector.server import LibraryImporter
from org.robotframework.jvmconnector.server import SimpleRobotRmiService
import re
class RemoteLibraryImporter(LibraryImporter):
    def __init__(self, rmi_exporter=MyRmiServiceExporter(), class_loader=Class):
        self.rmi_exporter = rmi_exporter
        self.class_loader = class_loader

    def importLibrary(self, libraryName):
        service_name = re.sub('\.', '', libraryName)
        service = self.class_loader.forName(libraryName)()
        self.rmi_exporter.export(service_name, service, 'org.robotframework.jvmconnector.server.RobotRmiService')
        return self.rmi_exporter.rmi_url

class RmiWrapper:
    def __init__(self, service_exporter=MyRmiServiceExporter(), class_loader=Class):
        self.service_exporter = service_exporter
        self.class_loader = class_loader

    def export_rmi_service_and_launch_application(self, application, args):
        self.service_exporter.export()
        self.class_loader.forName(application).main(args)

from robot.libraries.OperatingSystem import OperatingSystem
from os import pathsep
class RmiLauncher:
    def __init__(self, os_library=OperatingSystem()):
        self.os_library = os_library

    def start_application(self, application):
        pythonpath = pathsep.join(sys.path)
        self.os_library.start_process("jython -Dpython.path=%s %s %s" % (pythonpath, __file__, application))

if __name__ == '__main__':
    if len(sys.argv[1:]) >= 1:
        wrapper = RmiWrapper()
        wrapper.export_rmi_service_and_launch_application(sys.argv[1], sys.argv[2:])
