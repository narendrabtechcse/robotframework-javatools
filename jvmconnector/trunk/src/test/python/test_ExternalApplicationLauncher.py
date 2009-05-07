import unittest
from ExternalApplicationLauncher import ExternalApplicationLauncher, ArgumentResolver

class TestArgumentResolver(unittest.TestCase):
    def test_resolves_arguments(self):
        jvm_args = '-d32 -client -classpath lib/foo.jar:lib/bar.jar -Dfoo=bar '\
                   + '-version:1.5 -jre-restrict-search -da:org.robotframework '\
                   + ' -esa -dsa -agentlib:foolib -agentpath:/tmp/agent'
        app_args = 'foo bar baz'
        self._test_resolver(jvm_args, app_args)

    def _test_resolver(self, expected_jvm_args, expected_app_args):
        expected_app = 'org.robotframework.mock.MyApp'
        args = '%s %s %s' % (expected_jvm_args, expected_app, expected_app_args)
        jvm_args, app, app_args = ArgumentResolver().resolve_arguments(args)
        self.assertEquals(expected_jvm_args, jvm_args)
        self.assertEquals(expected_app, app)
        self.assertEquals(expected_app_args, app_args)
        

if __name__ == '__main__':
    unittest.main()
