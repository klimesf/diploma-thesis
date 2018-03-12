import unittest

from business_context.identifier import Identifier


class IdentifierTest(unittest.TestCase):
    def test_split(self):
        identifier = Identifier("auth", "loggedIn")
        self.assertEqual("auth", identifier.prefix)
        self.assertEqual("loggedIn", identifier.name)

    def test_single(self):
        identifier = Identifier("auth.loggedIn")
        self.assertEqual("auth", identifier.prefix)
        self.assertEqual("loggedIn", identifier.name)

    def test_str(self):
        identifier = Identifier("auth.loggedIn")
        self.assertEqual('auth.loggedIn', identifier.__str__())
