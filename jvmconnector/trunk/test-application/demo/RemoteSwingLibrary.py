import time

from org.robotframework.jvmconnector.client import RobotRemoteLibrary


class RemoteSwingLibrary(RobotRemoteLibrary):

    def __init__(self, host='localhost', port='1099',
                 timeout=30, retry_interval=5):
        self.host = host
        self.port = port
        start_time = time.time()
        timeout = int(timeout)
        retry_interval = int(retry_interval)
        while(True):
            try:
                RobotRemoteLibrary.__init__(self, host, port)
                break
            except:
                time.sleep(retry_interval)
                if time.time() - start_time >= timeout:
                    raise
