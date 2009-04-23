import sys
import os


class PythonpathHelper:
    def get_python_path(self):
        return os.pathsep.join(sys.path)
