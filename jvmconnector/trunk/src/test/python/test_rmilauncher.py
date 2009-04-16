import unittest
import os
import sys

from robot.utils.asserts import *
from java.net import ServerSocket

from org.robotframework.jvmconnector.mocks import SomeClass
from org.robotframework.jvmconnector.server import SimpleRobotRmiService

from rmilauncher import *

application = 'org.robotframework.jvmconnector.mocks.SomeClass'

class TestRmiLauncher(unittest.TestCase):

    def test_is_global_library(self):
        assert_equals('GLOBAL', getattr(RmiLauncher, 'ROBOT_LIBRARY_SCOPE'))

    def test_has_path_to_db(self):
        db_path1 = RmiLauncher(application).db_path
        db_path2 = RmiLauncher(application).db_path
        os.path.exists(db_path1)
        os.path.exists(db_path2)
        assert_not_equals(db_path1, db_path2)

class TestRmiLauncherStartingApplication(unittest.TestCase):

    def setUp(self):
        self.os_library = _FakeOperatingSystemLibrary()
        self.rmi_launcher = RmiLauncher(application, self.os_library)
        self.rmi_launcher.db_path = '/tempfile'

    def test_starts_application(self):
        self.rmi_launcher.start_application()
        assert_equals(self._get_expected_command(), self.os_library.command)

    def tests_passes_arguments(self):
        self.rmi_launcher.start_application('one two three',
                                            '-Done=two -Dthree=four')
        expected_command = self._get_expected_command('one two three',
                                                      '-Done=two -Dthree=four')

        assert_equals(expected_command, self.os_library.command)


    def _get_expected_command(self, args='', jvm_args=''):
        current_pythonpath = self._get_current_pythonpath()
        script_path = self._get_path_to_script()
        template = 'jython -Dpython.path=%s %s %s /tempfile %s %s'
        return template % (current_pythonpath, jvm_args, script_path,
                           application, args)

    def _get_current_pythonpath(self):
        return os.pathsep.join(sys.path)

    def _get_path_to_script(self):
        base = os.path.abspath(os.path.normpath(os.path.split(sys.argv[0])[0]))
        return '%s/src/main/python/rmilauncher.py' % base

class TestRmiWrapper(unittest.TestCase):

    def setUp(self):
        self.library_importer_publisher = _FakePublisher()
        self.wrapper = RmiWrapper(self.library_importer_publisher)

    def test_exports_rmi_service_and_launches_application(self):
        class_loader = _FakeClassLoader()
        self.wrapper.class_loader = class_loader
        args = ['one', 'two']
        self.wrapper.export_rmi_service_and_launch_application(application, args)
        
        assert_equals(application, self.library_importer_publisher.application)
        assert_equals(application, class_loader.name)
        assert_equals(args, class_loader.main_args)

    def test_application_is_launched_by_invoking_java_classes_main_method(self):
        args = ['one', 'two']
        self.wrapper.export_rmi_service_and_launch_application(application, args)
        assert_equals(application, self.library_importer_publisher.application)
        assert_equals(args, [i for i in SomeClass.args])

class TestMyRmiServicePublisher(unittest.TestCase):
    def setUp(self):
        self.class_loader = _FakeClassLoader()
        self.spring_publisher = _FakeServicePublisher()
        self.free_port = 11099
        self.port_finder = _StubPortFinder(self.free_port)
        self.publisher = MyRmiServicePublisher(self.class_loader,
                             self.spring_publisher, self.port_finder)

    def test_exports_services(self):
        interface_name = 'org.robotframework.jvmconnector.server.RobotRmiService'
        service_name = 'mylib'
        self.publisher.publish(service_name, SimpleRobotRmiService(), interface_name)
        self._assert_service_was_exported(service_name, self.free_port,
                                          SimpleRobotRmiService, interface_name)
        assert_equals('rmi://localhost:%s/mylib' % self.free_port, self.publisher.rmi_info)

    def _assert_service_was_exported(self, expected_service_name,
                                     expected_registry_port, expected_service,
                                     expected_service_interface):
        assert_equals(expected_service_name, self.spring_publisher.service_name)
        assert_equals(expected_registry_port, self.spring_publisher.registry_port)
        assert_true(isinstance(self.spring_publisher.service, expected_service))
        assert_equals(expected_service_interface, self.class_loader.name)
        assert_true(self.spring_publisher.prepare_was_called)
    
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

from org.robotframework.jvmconnector.mocks import MockJavaLibrary
class TestRemoteLibraryImporter(unittest.TestCase):
    def test_imports_library(self):
        rmi_publisher = _FakeMyRmiServicePublisher()
        classloader = _FakeClassLoader(MockJavaLibrary)
        library_importer = RemoteLibraryImporter(rmi_publisher, classloader)
        library_name = 'org.robotframework.jvmconnector.mocks.MockJavaLibrary'
        rmi_info = library_importer.importLibrary(library_name)

        expected_service_name = 'orgrobotframeworkjvmconnectormocksMockJavaLibrary'
        expected_interface_name = 'org.robotframework.jvmconnector.server.RobotRmiService'
        expected_rmi_info = '11099/orgrobotframeworkjvmconnectormocksMockJavaLibrary'

        assert_true(rmi_publisher.publish_was_invoked)
        assert_equals(expected_service_name, rmi_publisher.service_name)
        assert_true(isinstance(rmi_publisher.service, SimpleRobotRmiService))
        assert_equals(expected_interface_name, rmi_publisher.service_interface_name)
        assert_equals(expected_rmi_info, rmi_info)

class TestLibraryImporterPublisher(unittest.TestCase):
    def test_exports_remote_library_publisher(self):
        rmi_publisher = _FakeMyRmiServicePublisher()
        library_db = _FakeLibraryDb('path/to/db')
        library_importer_publisher = LibraryImporterPublisher(library_db,
                                                              rmi_publisher)
        library_importer_publisher.publish(application)

        expected_interface_name = 'org.robotframework.jvmconnector.server.LibraryImporter'
        expected_service_name = 'robotrmiservice'
        assert_true(rmi_publisher.publish_was_invoked)
        assert_equals(expected_service_name, rmi_publisher.service_name)
        assert_true(isinstance(rmi_publisher.service, RemoteLibraryImporter))
        assert_equals(expected_interface_name,
                      rmi_publisher.service_interface_name)
        assert_equals('path/to/db', library_db.db_path)
        assert_equals(application, library_db.application)
        assert_equals(rmi_publisher.rmi_info, library_db.rmi_info)

class TestLibaryDb(unittest.TestCase):
    def setUp(self):
        self.filecontents = ['%s%%0%%rmi://someservice\n' % (application)]
        self.file = _FakeFile(self.filecontents)
        self.builtin = _FakeBuiltin(self.file)
        self.db = LibraryDb('path/to/db', self.builtin)

    def test_stores(self):
        self.file = _FakeFile()
        self.builtin = _FakeBuiltin(self.file)
        db = LibraryDb('path/to/db', self.builtin)
        db.store(application, 'rmi://someservice')

        assert_equals('%s%%0%%rmi://someservice\n' % (application) , self.file.txt)
        self._assert_file_was_correctly_used('a')

    def test_stores_more_than_one(self):
        self.db._is_new = lambda: False
        self.db.store(application, 'rmi://someservice')

        assert_equals('%s%%1%%rmi://someservice\n' % (application) , self.file.txt)
        self._assert_file_was_correctly_used('a')

    def test_retrieves_application_rmi_info(self):
        assert_equals('rmi://someservice', self.db.retrieve(application))
        self._assert_file_was_correctly_used('r')

    def _assert_file_was_correctly_used(self, expected_mode):
        assert_equals('path/to/db', self.builtin.path)
        assert_equals(expected_mode, self.builtin.mode)
        assert_true(self.file.closed)

class _FakeBuiltin:
    def __init__(self, file):
        self.file = file
    def open(self, path, mode):
        self.path = path
        self.mode = mode
        return self.file

class _FakeFile:
    def __init__(self, lines=[]):
        self.lines = lines
    def write(self, txt):
        self.txt = txt
    def close(self):
        self.closed = True
    def read(self):
        return self.lines
    def __iter__(self):
        return iter(self.lines)

class _FakeLibraryDb:
    def __init__(self, db_path):
        self.db_path = db_path

    def store(self, application, rmi_info):
        self.application = application
        self.rmi_info = rmi_info

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
    def publish(self, service_name='', service=None, service_interface_name=''):
        self.publish_was_invoked = True
        self.service_name = service_name
        self.service = service
        self.service_interface_name = service_interface_name
        self.rmi_info = '11099/%s' % service_name

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
