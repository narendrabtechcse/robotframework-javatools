#!/usr/bin/env python

import unittest
import os
import sys
import re
from tempfile import gettempdir

base = os.path.abspath(os.path.normpath(os.path.split(sys.argv[0])[0]))
testfile = re.compile('^test_.*\.py$', re.IGNORECASE)          

def get_tests(directory):
    sys.path.append(directory)
    tests = []
    modules = []
    for name in os.listdir(directory):
        if name.startswith('.'): continue
        fullname = os.path.join(directory, name)
        if os.path.isdir(fullname):
            tests.extend(get_tests(fullname))
        elif testfile.match(name):
            modname = os.path.splitext(name)[0]
            modules.append(__import__(modname))
    tests.extend([ unittest.defaultTestLoader.loadTestsFromModule(module)
                   for module in modules ])
    return tests

def exists(file_name):
    file = os.path.join(base, file_name)
    return os.path.exists(file)

def sh(command):
    process = os.popen(command)
    output = process.read()
    process.close()
    return output

def add_dependencies_to_path():
    main = os.path.join('src', 'main', 'python')
    test = os.path.join('src', 'test', 'python')
    for path in [ main, test ]:
        path = os.path.join(base, path.replace('/', os.sep))
        if path not in sys.path:
            sys.path.insert(0, path)

    if not exists('dependencies.txt'):
        os.environ['MAVEN_OPTS'] = '-DoutputAbsoluteArtifactFilename=true'
        mvn_output = sh('mvn dependency:list').splitlines()
        jars = [re.sub('.*:(C:)?', '\\1', file) for file in mvn_output if re.search('jar', file)]
        dependencies_txt = open(os.path.join(base, 'dependencies.txt'), 'w')
        for jar in jars:
            dependencies_txt.write(jar + '\n')

    classes = os.path.join('target', 'classes')
    test_classes = os.path.join('target', 'test-classes')
    if not exists(classes) or not exists(test_classes):
        sh('mvn test-compile')

    dependencies = [classes, test_classes] + open('dependencies.txt', 'rb').read().splitlines()
    os.environ['CLASSPATH'] = os.pathsep.join(dependencies)
    os.environ['PYTHONPATH'] = os.pathsep.join(sys.path)

def get_python_path():
    for path in sys.path:
        if os.path.exists(os.path.join(path, 'robot')):
            return path
    

if __name__ == '__main__':
    rc = 0
    if os.name == 'java':
        tests = get_tests('.')
        suite = unittest.TestSuite(tests)
        runner = unittest.TextTestRunner(descriptions=0, verbosity=1)
        result = runner.run(suite)
        rc = len(result.failures) + len(result.errors)
        if rc > 250: rc = 250
    else:
        python_path = get_python_path()
        add_dependencies_to_path()
        if len(sys.argv[1:]) > 0:
            runner = os.path.join(python_path, 'robot', 'runner.py')
            args_as_string = ' '.join(sys.argv[1:])
            rc = os.system('jython -Dpython.path=%s %s --loglevel TRACE --outputdir %s %s' % (python_path, runner, gettempdir(), args_as_string))
        else:
            rc = os.system('jython -Dpython.path=%s %s' % (python_path, __file__))

    sys.exit(rc)
