import glob as _glob
import os as _os

_pattern = _os.path.join(_os.path.dirname(__file__), '..',  
                         'target', '*-jar-with-dependencies.jar')
_paths = _glob.glob(_pattern)
if _paths:
    _paths.sort()
    JAVA_AGENT_JAR = _paths[-1]
