import unittest
import rmi_launcher

class TestRMILauncher(unittest.TestCase):
    
    def setUp(self):
        self.rmi_launcher = rmi_launcher.RMILauncher()
    
    def test_java_in_arguments(self):
        cmd = ['java', 'foo.bar']
        expected = '%s RMILauncher config.xml foo.bar' % (self.rmi_launcher._get_java())
        self.assertEquals(self.rmi_launcher._get_command(cmd), expected)
        
    def test_RMILauncher_in_arguments(self):
        print self.rmi_launcher._get_java()

if __name__ == '__main__':
    unittest.main()