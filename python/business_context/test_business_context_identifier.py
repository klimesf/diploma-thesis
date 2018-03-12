import unittest

from business_context.business_context_identifier import BusinessContextIdentifier


class BusinessContextIdentifierTest(unittest.TestCase):
    def test_split(self):
        identifier = BusinessContextIdentifier("auth", "loggedIn")
        self.assertEqual("auth", identifier.prefix)
        self.assertEqual("loggedIn", identifier.name)

    def test_single(self):
        identifier = BusinessContextIdentifier("auth.loggedIn")
        self.assertEqual("auth", identifier.prefix)
        self.assertEqual("loggedIn", identifier.name)
