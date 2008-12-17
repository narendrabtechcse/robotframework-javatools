import time
from robot.utils import normalize
from java.net import ConnectException

from org.robotframework.jvmconnector.client import RobotRemoteLibrary

class RemoteLibrary:
    def __init__(self, uri='rmi://localhost:1099/jvmConnector', timeout=120, retry_interval=5):
        self.uri = uri
        self.timeout = timeout
        self.retry_interval = retry_interval
        self.open_connection()

    def open_connection(self):
        start_time = time.time()
        timeout = int(self.timeout)
        retry_interval = int(self.retry_interval)
        while True:
            try:
                self.remote_lib = RobotRemoteLibrary(self.uri)
                break
            except:
                time.sleep(retry_interval)
                if time.time() - start_time >= timeout:
                    raise
            else:
                break

    def get_keyword_names(self):
        keyword_names = list(self.remote_lib.getKeywordNames())
        keyword_names.append('openconnection')
        return keyword_names

    def run_keyword(self, name, args):
        if normalize(name) == 'openconnection':
            self.open_connection()
        else:
            return self.remote_lib.runKeyword(name, args)
