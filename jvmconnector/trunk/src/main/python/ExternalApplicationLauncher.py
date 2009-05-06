#!/usr/bin/python

import os
from os.path import abspath, dirname, exists, join, normpath
import sys


class ExternalApplicationLauncher:

    def launch_java_with_rmi(self, args):
        print args
        command = self._get_command(args)
        print command
        os.popen2(command)

    def _get_command(self, args):
        if 'ApplicationLauncher.py' in args:
            return '%s %s' % (self._get_java(), ' '.join(args))
        else:
            return  ' '.join( [self._get_java(), 'ApplicationLauncher.py'] + args) 

    def _get_jython(self, args):
        script = join(dirname(__file__), 'ApplicationLauncher.py')
        jvm_args, application, app_args = self._resolve_arguments(args)
        command = 'jython -Dpython.path=%s %s %s %s %s' % (self._get_python_path(),
                  jvm_args, script, self.application, args)

    def _resolve_arguments(self, args):
        
        
    def _get_python_path(self):
        for path_entry in sys.path:
            if exists(join(path_entry, 'robot')):
                return path_entry

    def _get_java(self):
        fake_java_dir = normpath(abspath(dirname(__file__)))
        for path in os.environ['PATH'].split(os.pathsep):
            for java in ['java', 'java.sh', 'java.exe']:
                java_path = normpath(abspath(join(path, java)))
                if exists(java_path) and not fake_java_dir in java_path:
                    print java_path
                    return java_path
    
if __name__ == '__main__':
    launcher = ExternalApplicationLauncher()
    launcher.launch_java_with_rmi(sys.argv[1:])
