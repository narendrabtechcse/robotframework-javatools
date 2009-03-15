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


from org.robotframework.jvmconnector.server import LibraryImporter
class MyLibraryImporter(LibraryImporter):
    def importLibrary(self, libraryName):
        "imported [%s]" % libraryName


from java.net import ServerSocket
class FreePortFinder:
    def find_free_port(self):
        try:
            s = ServerSocket(0)
            return s.getLocalPort()
        finally:
            s.close()

from org.springframework.remoting.rmi import RmiServiceExporter
class RmiExporter:
    def __init__(self, java_class=Class, exporter=RmiServiceExporter(), free_port_finder=FreePortFinder()): #, rmi_url_communicator):
        self.java_class = java_class
        self.exporter = exporter
        self.free_port_finder = free_port_finder

    def export(self, service_name="remoterobot", service=MyLibraryImporter(), service_interface_name="org.robotframework.jvmconnector.server.LibraryImporter"):
        self.exporter.setServiceName(service_name)
        self.exporter.setRegistryPort(11099)
        self.exporter.setService(service)
        self.exporter.setServiceInterface(self.java_class.forName(service_interface_name))
        self.exporter.prepare()

class RmiWrapper:
    def __init__(self, service_exporter=RmiExporter(), java_class=Class):
        self.service_exporter = service_exporter
        self.java_class = java_class

    def export_rmi_service_and_launch_application(self, application, args=None):
        self.service_exporter.export()
        self.java_class.forName(application).main(args)

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
        wrapper = MyRmiWrapper()
        wrapper.export_rmi_service_and_launch_application(sys.argv[1], sys.argv[2:])
