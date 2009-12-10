#!/usr/bin/env python

import os
import sys

from run_tests import add_dependencies_to_path, get_python_path

if __name__ == '__main__':
    add_dependencies_to_path()
    python_path = get_python_path()
    base = os.path.dirname(__file__)
    libdoc = os.path.join(base, 'doc', 'libdoc.py')
    output = os.path.join(base, 'doc', 'RemoteApplications.html')
    lib = os.path.join(base, 'src', 'main', 'python', 'RemoteApplications.py')
    command = 'jython -Dpython.path=%s %s --output %s %s' % (python_path, libdoc, output, lib)
    sys.exit(os.system(command))
