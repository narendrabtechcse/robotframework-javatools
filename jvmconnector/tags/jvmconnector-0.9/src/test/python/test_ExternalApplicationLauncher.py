import unittest
from ExternalApplicationLauncher import ExternalApplicationLauncher, ArgumentResolver

class TestArgumentResolver(unittest.TestCase):
    def test_resolves(self):
        jvm_args = '-d32 -client -classpath lib/foo.jar:lib/bar.jar -Dfoo=bar '\
                   + '-version:1.5 -jre-restrict-search -da:org.robotframework '\
                   + ' -esa -dsa -agentlib:foolib -agentpath:/tmp/agent'
        app_args = 'foo bar baz'
        self._test_resolver(jvm_args, app_args)

    def test_classpath_last(self):
        jvm_args = '-d32 -client -Dfoo=bar -agentpath:/tmp/agent '\
                   + '-esa -dsa -agentlib:foolib -classpath lib/foo.jar:lib/bar.jar'
        app_args = 'foo bar baz'
        self._test_resolver(jvm_args, app_args)

    def test_jvm_style_app_args(self):
        jvm_args = '-d32 -client -Dfoo=bar -agentpath:/tmp/agent '\
                   + '-esa -dsa -agentlib:foolib -classpath lib/foo.jar:lib/bar.jar'
        app_args = '-dsa -Dfoo=bar baz'
        self._test_resolver(jvm_args, app_args)

    def test_without_jvm_args(self):
        app_args = 'foo bar baz'
        self._test_resolver('', app_args)

    def test_without_app_args(self):
        jvm_args = '-d32 -client -Dfoo=bar -agentpath:/tmp/agent '\
                   + '-esa -dsa -agentlib:foolib -classpath lib/foo.jar:lib/bar.jar'
        self._test_resolver(jvm_args, '')

    def test_without_jvm_and_app_args(self):
        self._test_resolver('', '')

    def test_class_without_package(self):
        jvm_args = '-d32 -client -Dfoo=bar -agentpath:/tmp/agent'
        self._test_resolver(jvm_args, 'foo bar', 'SomeApp')

    def _test_resolver(self, expected_jvm_args, expected_app_args,
                       expected_app = 'org.robotframework.mock.MyApp'):
        args = '%s %s %s' % (expected_jvm_args, expected_app, expected_app_args)
        jvm_args, app, app_args = ArgumentResolver().resolve_arguments(args)
        self.assertEquals(expected_jvm_args, jvm_args)
        self.assertEquals(expected_app, app)
        self.assertEquals(expected_app_args, app_args)
        

if __name__ == '__main__':
    unittest.main()
