import unittest
import os
import sys
from robot.utils.asserts import *
from rmilauncher import *

application = "com.acme.SomeApp"

class _FakeOperatingSystemLibrary:
    def start_process(self, command):
        self.command = command

class _FakeRmiExporter:
    def export(self):
        self.export_was_invoked = True

class _FakeClass:
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

class TestRmiWrapper(unittest.TestCase):
    def test_exports_rmi_service_and_launches_application(self):
        rmi_exporter = _FakeRmiExporter()
        clazz = _FakeClass()
        wrapper = RmiWrapper(rmi_exporter, clazz)
        wrapper.export_rmi_service_and_launch_application(application, ["one", "two"])
        
        assert_true(rmi_exporter.export_was_invoked)
        assert_equals(application, clazz.name)
        assert_equals(["one", "two"], clazz.main_args)

def _get_expected_command():
    return "jython -Dpython.path=%s %s %s" % (_get_current_pythonpath(), _get_file(), application)

def _get_current_pythonpath():
    return os.pathsep.join(sys.path)

def _get_file():
    return "%s/src/main/python/remote/rmilauncher.py" % os.path.abspath(os.path.normpath(os.path.split(sys.argv[0])[0]))
