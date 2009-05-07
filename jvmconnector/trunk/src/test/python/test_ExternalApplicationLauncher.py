import unittest
import ExternalApplicationLauncher

class TestArgumentResolver(unittest.TestCase):
    def setUp(self):
        jvm_args = ['-d32 ', '-client', '-classpath'
        'lib/foo.jar:lib/bar.jar', '-Dfoo=bar', '-version:1.5',
        '-jre-restrict-search ', '-da:org.robotframework', '-esa ', '-dsa',
        '-agentlib:foolib', '-agentpath:/tmp/agent']
        self.jvm_args = ' '.join(jvm_args)
        self.app_args = 'foo bar baz'
        self.app = 'org.robotframework.mock.MyApp'
                  
    def test_resolves_arguments(self):
        args = '%s %s %s' % (self.jvm_args, self.app, self.app_args)
        jvm_args, app, app_args = ArgumentResolver().resolve_arguments(args)
        self.assertEquls(self.jvm_args, jvm_args)
        self.assertEquls(self.app, app)
        self.assertEquls(self.app_args, app_args)

if __name__ == '__main__':
    unittest.main()
