import unittest

from business_context.business_context import BusinessContext, Precondition, PostCondition
from business_context.business_context_identifier import BusinessContextIdentifier
from business_context.expression import Constant


class BusinessContextTest(unittest.TestCase):
    def test_merge(self):
        identifier_1 = BusinessContextIdentifier("auth", "userLoggedIn")
        identifier_2 = BusinessContextIdentifier("user", "create")

        precondition_1 = Precondition('precondition_1', Constant(True))
        precondition_2 = Precondition('precondition_2', Constant(True))

        post_condition_1 = Precondition('post_condition_1', Constant(True))
        post_condition_2 = Precondition('post_condition_2', Constant(True))

        context_1 = BusinessContext(identifier_1, set(), {precondition_1}, {post_condition_1})
        context_2 = BusinessContext(identifier_2, set(), {precondition_2}, {post_condition_2})

        context_1.merge(context_2)

        self.assertTrue(precondition_1 in context_1.preconditions)
        self.assertTrue(precondition_2 in context_1.preconditions)
        self.assertTrue(post_condition_1 in context_1.post_conditions)
        self.assertTrue(post_condition_2 in context_1.post_conditions)
