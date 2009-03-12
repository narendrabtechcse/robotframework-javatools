import unittest
import os
import sys
from robot.utils.asserts import assert_equals
from rmilauncher import RmiLauncher

application = "com.acme.SomeApp"

class _MockOperatingSystemLibrary:
    def start_process(self, command):
        self.command = command

    def get_command_used(self):
        return self.command

class TestRmiLauncher(unittest.TestCase):
    def test_launches_rmi_service_and_application(self):
        os_library = _MockOperatingSystemLibrary()
        rmi_launcher = RmiLauncher(os_library)
        rmi_launcher.launch_rmi_and_application(application)
        
        assert_equals(_get_expected_command(), os_library.get_command_used())

def _get_expected_command():
    return "jython -Dpython.path=%s %s %s" % (_get_current_pythonpath(), _get_file(), application)

def _get_current_pythonpath():
    return os.pathsep.join(sys.path)

def _get_file():
    return "%s/src/main/python/remote/rmilauncher.py" % os.path.abspath(os.path.normpath(os.path.split(sys.argv[0])[0]))
