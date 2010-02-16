#!/usr/bin/env python

import glob
import os
import shutil
import sys
import zipfile

CURDIR = os.path.abspath(os.path.dirname(__file__))
EXAMPLE = os.path.join(CURDIR, 'example')
ROOT = os.path.join(CURDIR, '..')
TARGET = os.path.join(ROOT, 'target')
LIB = os.path.join(EXAMPLE, 'lib')
REMOTE_LIBRARY = os.path.join(ROOT, 'src', 'main', 'python', 'RemoteApplications.py')


def main():
    _create_jar()
    _copy_libraries()
    if _run_tests() != 0:
        print "Failed to run the tests."
        sys.exit(1)
    zip_example()

def _create_jar():
    os.chdir(ROOT)
    os.system('bash jarjar.sh')

def _copy_libraries():
    shutil.copy(REMOTE_LIBRARY, LIB)
    jars = glob.glob(os.path.join(TARGET, 'jvmconnector-*-with-dependencies.jar'))
    shutil.copy(sorted(jars)[-1], LIB)

def _run_tests():
    runner = os.path.join(EXAMPLE, 'run.py')
    return os.system('python %s %s' % (runner, EXAMPLE)) >> 8

def remove(path):
    if os.path.exists(path):
        os.remove(path)

def zip_example():
    zip_path = os.path.join(CURDIR, 'remote_applications_example.zip')
    zip = zipfile.ZipFile(zip_path, 'w')
    paths = _get_paths([(LIB, '*.jar'), (LIB, '*.py'), (EXAMPLE, '*.*')])
    for path in paths:
        zip.write(path, path.replace(CURDIR, ''))
    print "Created zip file '%s'" % (zip_path)

def _get_paths(patterns):
    paths = []
    for dir, pattern in patterns:
        paths.extend(glob.glob(os.path.join(dir, pattern)))
    return paths


if __name__ == '__main__':
    main()