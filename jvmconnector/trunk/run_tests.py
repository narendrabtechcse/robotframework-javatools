#!/usr/bin/env python

import unittest
import os
import sys
import re

sys.path.insert(0, '/usr/lib/python2.5/site-packages')
base = os.path.abspath(os.path.normpath(os.path.split(sys.argv[0])[0]))
for path in [ "./src/main/python", "./src/test/python" ]:
    path = os.path.join(base, path.replace('/', os.sep))
    if path not in sys.path:
        sys.path.insert(0, path)

testfile = re.compile("^test_.*\.py$", re.IGNORECASE)          

def get_tests(directory):
    sys.path.append(directory)
    tests = []
    modules = []
    for name in os.listdir(directory):
        if name.startswith("."): continue
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
    if not exists('dependencies.txt'):
        mvn_output = sh("mvn dependency:list -DoutputAbsoluteArtifactFilename=true").splitlines()
        jars = [re.sub('.*:', '', file) for file in mvn_output if re.search('jar', file)]
        dependencies_txt = open(os.path.join(base, 'dependencies.txt'), 'w')
        for jar in jars:
            dependencies_txt.write(jar + "\n")

    classes = os.path.join('target', 'classes')
    test_classes = os.path.join('target', 'test-classes')
    if not exists(classes) or not exists(test_classes):
        sh("mvn test-compile")

    dependencies = [classes, test_classes] + open('dependencies.txt', 'rb').read().splitlines()
    os.environ['CLASSPATH'] = os.pathsep.join(dependencies)

if __name__ == '__main__':
    rc = 0
    if os.name == 'java':
        tests = get_tests(".")
        suite = unittest.TestSuite(tests)
        runner = unittest.TextTestRunner(descriptions=0, verbosity=1)
        result = runner.run(suite)
        rc = len(result.failures) + len(result.errors)
        if rc > 250: rc = 250
    else:
        add_dependencies_to_path()
        rc = os.system("jython %s" % __file__)

    sys.exit(rc)
