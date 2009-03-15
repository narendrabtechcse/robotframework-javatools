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

    def _get_expected_command(self):
        current_pythonpath = self._get_current_pythonpath()
        script_path = self._get_file()
        return "jython -Dpython.path=%s %s %s" % (current_pythonpath, script_path, application)

    def _get_current_pythonpath(self):
        return os.pathsep.join(sys.path)

    def _get_file(self):
        return "%s/src/main/python/remote/rmilauncher.py" % os.path.abspath(os.path.normpath(os.path.split(sys.argv[0])[0]))

from org.robotframework.jvmconnector.mocks import SomeClass
class TestRmiWrapper(unittest.TestCase):
    def setUp(self):
        self.rmi_exporter = _FakeRmiExporter()

    def test_exports_rmi_service_and_launches_application(self):
        java_class = _FakeJavaClass()
        wrapper = RmiWrapper(self.rmi_exporter, java_class)
        wrapper.export_rmi_service_and_launch_application(application, ["one", "two"])
        
        assert_true(self.rmi_exporter.export_was_invoked)
        assert_equals(application, java_class.name)
        assert_equals(["one", "two"], java_class.main_args)

    def test_loads_class_correctly(self):
        wrapper = RmiWrapper(self.rmi_exporter)
        wrapper.export_rmi_service_and_launch_application(application, ["one", "two"])
        assert_equals(["one", "two"], [i for i in SomeClass.args])

class TestRmiExporter(unittest.TestCase):
    def setUp(self):
        self.java_class = _FakeJavaClass()
        self.spring_exporter = _FakeServiceExporter()
        self.port_finder = _StubPortFinder(11099)
        self.exporter = RmiExporter(self.java_class, self.spring_exporter, self.port_finder)

    def test_exports_default_service(self):
        self.exporter.export()

        self._assert_default_service_was_exported()

    def test_gets_rmi_url(self):
        self.exporter.export()
        assert_equals("rmi://localhost:11099/remoterobot", self.exporter.rmi_url)
    
    def _assert_default_service_was_exported(self):
        assert_equals("remoterobot", self.spring_exporter.service_name)
        assert_equals(11099, self.spring_exporter.registry_port)
        assert_true(isinstance(self.spring_exporter.service, MyLibraryImporter))
        assert_equals("org.robotframework.jvmconnector.server.LibraryImporter", self.java_class.name)
        assert_true(self.spring_exporter.prepare_was_called)
    

class _FakeOperatingSystemLibrary:
    def start_process(self, command):
        self.command = command

class _FakeRmiExporter:
    def export(self):
        self.export_was_invoked = True

class _FakeJavaClass:
    def forName(self, name):
        self.name = name
        return self 

    def main(self, args):
        self.main_args = args

class _FakeServiceExporter:
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
