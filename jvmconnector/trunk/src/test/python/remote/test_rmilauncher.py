import unittest
import os
import sys
from robot.utils.asserts import *
from rmilauncher import *

application = "org.robotframework.jvmconnector.mocks.SomeClass"

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

class TestRmiLauncher(unittest.TestCase):
    def test_starts_application(self):
        os_library = _FakeOperatingSystemLibrary()
        rmi_launcher = RmiLauncher(os_library)
        rmi_launcher.start_application(application)
        
        assert_equals(_get_expected_command(), os_library.command)

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

    def test_uses_java_lang_class_for_loading_classes(self):
        wrapper = RmiWrapper(self.rmi_exporter)
        wrapper.export_rmi_service_and_launch_application(application, ["one", "two"])
        assert_equals(["one", "two"], _to_list(SomeClass.args))

#class TestRmiExporter(unittest.TestCase):
    #def test_exports_default_service(self):
        #java_class = _FakeJavaClass()
        #exporter = RmiExporter(java_class)
        #exporter.export()

def _to_list(java_array):
    list = []
    for i in java_array:
        list.append(i)
    return list

def _get_expected_command():
    return "jython -Dpython.path=%s %s %s" % (_get_current_pythonpath(), _get_file(), application)

def _get_current_pythonpath():
    return os.pathsep.join(sys.path)

def _get_file():
    return "%s/src/main/python/remote/rmilauncher.py" % os.path.abspath(os.path.normpath(os.path.split(sys.argv[0])[0]))
