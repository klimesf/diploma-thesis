import unittest

from business_context.business_context import Precondition, BusinessContext
from business_context.business_context_identifier import BusinessContextIdentifier
from business_context.business_context_registry import BusinessContextRegistry, LocalBusinessContextLoader, RemoteBusinessContextLoader, RemoteLoader
from business_context.expression import Constant

auth_logged_in_identifier = BusinessContextIdentifier("auth", "userLoggedIn")
user_create_identifier = BusinessContextIdentifier("user", "create")

precondition_1 = Precondition('precondition_1', Constant(True))
precondition_2 = Precondition('precondition_2', Constant(True))

post_condition_1 = Precondition('post_condition_1', Constant(True))
post_condition_2 = Precondition('post_condition_2', Constant(True))

auth_logged_in = BusinessContext(auth_logged_in_identifier, set(), {precondition_1}, {post_condition_1})
user_create = BusinessContext(user_create_identifier, {auth_logged_in_identifier}, {precondition_2}, {post_condition_2})


class BusinessContextRegistryTest(unittest.TestCase):
    class MockLocalLoader(LocalBusinessContextLoader):

        def load(self) -> set:
            return {user_create}

    class MockRemoteLoader(RemoteLoader):
        _contexts = {auth_logged_in_identifier: auth_logged_in}

        def load_contexts(self, identifiers: set) -> set:
            result = set()
            for identifier in identifiers:
                if identifier in self._contexts:
                    result.add(self._contexts[identifier])
            return result

    def test(self):
        registry = BusinessContextRegistry(self.MockLocalLoader(), RemoteBusinessContextLoader(self.MockRemoteLoader()))
        context = registry.get_context_by_identifier(user_create_identifier)
        self.assertTrue(precondition_1 in context.preconditions)
        self.assertTrue(precondition_2 in context.preconditions)
        self.assertTrue(post_condition_1 in context.post_conditions)
        self.assertTrue(post_condition_2 in context.post_conditions)
