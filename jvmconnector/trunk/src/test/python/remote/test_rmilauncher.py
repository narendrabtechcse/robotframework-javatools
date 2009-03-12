import unittest
from robot.utils.asserts import assert_equals

class TestSpikulous(unittest.TestCase):
    def test_lol(self):
        assert_equals("lol", "foo")
