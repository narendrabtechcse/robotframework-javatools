#!/usr/bin/python

import os
from os.path import abspath, dirname, exists, join, normpath
import sys
import re

class ArgumentResolver:
    def resolve_arguments(self, raw_args):
        return re.search(r'((?:\s*(?:-classpath|-cp)\s+\S+|\s*-\S+)*)\s*(\S+)\s*(.*)',
                         raw_args).groups()

class ExternalApplicationLauncher:

    def launch_java_with_rmi(self, args):
        command = self._get_command(args)
        os.popen2(command)

    def _get_command(self, args):
        if 'ApplicationLauncher.py' in args:
            return self._get_java(args)
        else:
            return self._wrap_java(args)

    def _get_java(self, args):
        return '%s %s' % (self._find_real_java_path(), ' '.join(args))

    def _find_real_java_path(self):
        fake_java_dir = normpath(abspath(dirname(__file__)))
        for path in os.environ['PATH'].split(os.pathsep):
            for java in ['java', 'java.sh', 'java.exe']:
                java_path = normpath(abspath(join(path, java)))
                if exists(java_path) and not fake_java_dir in java_path:
                    return java_path

    def _wrap_java(self, args):
        script = join(dirname(__file__), 'ApplicationLauncher.py')
        jvm_args, application, app_args = self._resolve_arguments(args)
        command = 'jython -Dpython.path=%s %s %s %s %s' % (self._get_python_path(),
                  jvm_args, script, self.application, args)

    def _resolve_arguments(self, args):
        return ArgumentResolver().resolve_arguments(args)
        
    def _get_python_path(self):
        for path_entry in sys.path:
            if exists(join(path_entry, 'robot')):
                return path_entry

    
if __name__ == '__main__':
    launcher = ExternalApplicationLauncher()
    launcher.launch_java_with_rmi(sys.argv[1:])
