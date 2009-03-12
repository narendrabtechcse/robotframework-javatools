#!/usr/bin/env python

import unittest
import os
import sys
import re

base = os.path.abspath(os.path.normpath(os.path.split(sys.argv[0])[0]))
for path in [ "src/main/python" ]:
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

if __name__ == '__main__':
    tests = get_tests(".")
    print tests
    suite = unittest.TestSuite(tests)
    runner = unittest.TextTestRunner(descriptions=0, verbosity=1)
    result = runner.run(suite)
    rc = len(result.failures) + len(result.errors)
    if rc > 250: rc = 250
    sys.exit(rc)
