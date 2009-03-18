import unittest
import os
import sys
from robot.utils.asserts import *
from rmilauncher import *

application = "org.robotframework.jvmconnector.mocks.SomeClass"

class TestRmiLauncher(unittest.TestCase):
    def test_starts_application(self):
        os_library = _FakeOperatingSystemLibrary()
        rmi_launcher = RmiLauncher(os_library)
        rmi_launcher.start_application(application)
        
        assert_equals(self._get_expected_command(), os_library.command)

    def test_is_global_library(self):
        assert_equals('GLOBAL', getattr(RmiLauncher, 'ROBOT_LIBRARY_SCOPE'))

    def _get_expected_command(self):
        current_pythonpath = self._get_current_pythonpath()
        script_path = self._get_path_to_script()
        return "jython -Dpython.path=%s %s %s" % (current_pythonpath, script_path, application)

    def _get_current_pythonpath(self):
        return os.pathsep.join(sys.path)

    def _get_path_to_script(self):
        return "%s/src/main/python/remote/rmilauncher.py" % os.path.abspath(os.path.normpath(os.path.split(sys.argv[0])[0]))

from org.robotframework.jvmconnector.mocks import SomeClass
class TestRmiWrapper(unittest.TestCase):
    def setUp(self):
        self.library_importer_publisher = _FakePublisher()
        self.db_path = "/tmp/library.db"

    def test_exports_rmi_service_and_launches_application(self):
        class_loader = _FakeClassLoader()
        wrapper = RmiWrapper(self.db_path, self.library_importer_publisher, class_loader)
        wrapper.export_rmi_service_and_launch_application(application, ["one", "two"])
        
        assert_equals(self.db_path, self.library_importer_publisher.db_path)
        assert_equals(application, self.library_importer_publisher.application)
        assert_equals(application, class_loader.name)
        assert_equals(["one", "two"], class_loader.main_args)

    def test_application_is_launched_by_invoking_java_classes_main_method(self):
        wrapper = RmiWrapper(self.db_path, self.library_importer_publisher)
        wrapper.export_rmi_service_and_launch_application(application, ["one", "two"])
        assert_equals(application, self.library_importer_publisher.application)
        assert_equals(["one", "two"], [i for i in SomeClass.args])

from org.robotframework.jvmconnector.server import SimpleRobotRmiService
class TestMyRmiServicePublisher(unittest.TestCase):
    def setUp(self):
        self.class_loader = _FakeClassLoader()
        self.spring_publisher = _FakeServicePublisher()
        self.free_port = 11099
        self.port_finder = _StubPortFinder(self.free_port)
        self.publisher = MyRmiServicePublisher(self.class_loader, self.spring_publisher, self.port_finder)

    def test_exports_services(self):
        self.publisher.publish("mylib", SimpleRobotRmiService(), "org.robotframework.jvmconnector.server.RobotRmiService")
        self._assert_service_was_exported("mylib", self.free_port, SimpleRobotRmiService, "org.robotframework.jvmconnector.server.RobotRmiService")
        assert_equals("rmi://localhost:%s/mylib" % self.free_port, self.publisher.rmi_url)

    def _assert_service_was_exported(self, expected_service_name, expected_registry_port, expected_service, expected_service_interface):
        assert_equals(expected_service_name, self.spring_publisher.service_name)
        assert_equals(expected_registry_port, self.spring_publisher.registry_port)
        assert_true(isinstance(self.spring_publisher.service, expected_service))
        assert_equals(expected_service_interface, self.class_loader.name)
        assert_true(self.spring_publisher.prepare_was_called)
    
from java.net import ServerSocket
class TestFreePortFinder(unittest.TestCase):
    def setUp(self):
        self.port = 5555
        self.socket = _FakeServerSocket(self.port)
        self.port_finder = FreePortFinder()

    def test_finds_free_port(self):
        free_port = self.port_finder.find_free_port(self.socket)
        assert_equals(self.port, free_port)

    def test_frees_resources_after_port_is_found(self):
        self.port_finder.find_free_port(self.socket)
        assert_true(self.socket.closed)

    def test_frees_resources_even_in_case_of_error(self):
        def newGetLocalPort():
            raise Exception

        self.socket.getLocalPort = newGetLocalPort
        try:
            self.port_finder.find_free_port(self.socket)
        except: pass
        assert_true(self.socket.closed)

class TestRemoteLibraryImporter(unittest.TestCase):
    def test_imports_library(self):
        rmi_publisher = _FakeMyRmiServicePublisher()
        classloader = _FakeClassLoader(SimpleRobotRmiService)
        library_importer = RemoteLibraryImporter(rmi_publisher, classloader)
        url = library_importer.importLibrary("org.robotframework.jvmconnector.mocks.MockJavaLibrary")

        assert_true(rmi_publisher.publish_was_invoked)
        assert_equals("orgrobotframeworkjvmconnectormocksMockJavaLibrary", rmi_publisher.service_name)
        assert_true(isinstance(rmi_publisher.service, SimpleRobotRmiService))
        assert_equals("org.robotframework.jvmconnector.server.RobotRmiService", rmi_publisher.service_interface_name)
        assert_equals("rmi://localhost:11099/orgrobotframeworkjvmconnectormocksMockJavaLibrary", url)

class TestLibraryImporterPublisher(unittest.TestCase):
    def test_exports_remote_library_publisher(self):
        rmi_publisher = _FakeMyRmiServicePublisher()
        library_db = _FakeLibraryDb()
        service_publisher = LibraryImporterPublisher(rmi_publisher, library_db)
        service_publisher.publish(application)

        assert_true(rmi_publisher.publish_was_invoked)
        assert_equals("robotrmiservice", rmi_publisher.service_name)
        assert_true(isinstance(rmi_publisher.service, RemoteLibraryImporter))
        assert_equals("org.robotframework.jvmconnector.server.LibraryImporter", rmi_publisher.service_interface_name)
        #assert_equals("rmi://localhost:11099/robotrmiservice", url)

class _FakeLibraryDb:
    pass

class _FakeServerSocket:
    def __init__(self, port):
        self.port = port
    def getLocalPort(self):
        return self.port
    def close(self):
        self.closed = True

class _FakeOperatingSystemLibrary:
    def start_process(self, command):
        self.command = command

class _FakePublisher:
    def publish(self, application):
        self.application = application

class _FakeMyRmiServicePublisher:
    def publish(self, service_name="", service=None, service_interface_name=""):
        self.publish_was_invoked = True
        self.service_name = service_name
        self.service = service
        self.service_interface_name = service_interface_name
        self.rmi_url = "rmi://localhost:11099/%s" % service_name

class _FakeClassLoader:
    def __init__(self, class_=None):
        self.class_ = class_ or self

    def forName(self, name):
        self.name = name
        return self.class_

    def main(self, args):
        self.main_args = args

class _FakeServicePublisher:
    def setServiceName(self, service_name):
        self.service_name = service_name
    def setRegistryPort(self, registry_port):
        self.registry_port = registry_port
    def setService(self, service):
        self.service = service
    def setServiceInterface(self, interface):
        self.interface = interface
    def prepare(self):
        self.prepare_was_called = True

class _StubPortFinder:
    def __init__(self, port):
        self.port = port

    def find_free_port(self):
        return self.port

if __name__ == '__main__':
    unittest.main()
