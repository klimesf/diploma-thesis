import unittest

from typing import Set
from business_context.context import BusinessContext
from business_context.rule import Precondition, PostCondition, PostConditionType
from business_context.identifier import Identifier
from business_context.registry import Registry, LocalBusinessContextLoader, RemoteBusinessContextLoader, RemoteLoader, TransactionNotInProgress
from business_context.expression import Constant, ExpressionType


class BusinessContextRegistryTest(unittest.TestCase):

    def test_initialization(self):
        auth_logged_in_identifier = Identifier("auth", "userLoggedIn")
        user_create_identifier = Identifier("user", "create")

        precondition_1 = Precondition('precondition_1', Constant(value=True, type=ExpressionType.BOOL))
        precondition_2 = Precondition('precondition_2', Constant(value=False, type=ExpressionType.BOOL))

        post_condition_1 = PostCondition('post_condition_1', PostConditionType.FILTER_OBJECT_FIELD, 'user', Constant(value=True, type=ExpressionType.BOOL))
        post_condition_2 = PostCondition('post_condition_2', PostConditionType.FILTER_OBJECT_FIELD, 'user', Constant(value=True, type=ExpressionType.BOOL))

        auth_logged_in = BusinessContext(auth_logged_in_identifier, set(), {precondition_1}, {post_condition_1})
        user_create = BusinessContext(user_create_identifier, {auth_logged_in_identifier}, {precondition_2}, {post_condition_2})

        class MockLocalLoader(LocalBusinessContextLoader):

            def load(self) -> Set[BusinessContext]:
                return {user_create}

        class MockRemoteLoader(RemoteLoader):
            _contexts = {auth_logged_in_identifier: auth_logged_in}

            def load_contexts(self, identifiers: Set[Identifier]) -> Set[BusinessContext]:
                result = set()
                for identifier in identifiers:
                    if identifier in self._contexts:
                        result.add(self._contexts[identifier])
                return result

        registry = Registry(MockLocalLoader(), RemoteBusinessContextLoader(MockRemoteLoader()))
        context = registry.get_context_by_identifier(user_create_identifier)
        self.assertTrue(precondition_1 in context.preconditions)
        self.assertTrue(precondition_2 in context.preconditions)
        self.assertTrue(post_condition_1 in context.post_conditions)
        self.assertTrue(post_condition_2 in context.post_conditions)

    def test_no_transaction(self):
        class MockLocalLoader(LocalBusinessContextLoader):
            def load(self) -> Set[BusinessContext]:
                return set()

        class MockRemoteLoader(RemoteLoader):
            def load_contexts(self, identifiers: Set[Identifier]) -> Set[BusinessContext]:
                return set()

        registry = Registry(MockLocalLoader(), RemoteBusinessContextLoader(MockRemoteLoader()))
        with self.assertRaises(TransactionNotInProgress) as exception:
            registry.save_or_update_context(None)
        self.assertTrue(exception is not None)

    def test_commit(self):
        class MockLocalLoader(LocalBusinessContextLoader):
            def load(self) -> Set[BusinessContext]:
                return set()

        class MockRemoteLoader(RemoteLoader):
            def load_contexts(self, identifiers: Set[Identifier]) -> Set[BusinessContext]:
                return set()

        registry = Registry(MockLocalLoader(), RemoteBusinessContextLoader(MockRemoteLoader()))
        registry.begin_transaction()
        registry.save_or_update_context(BusinessContext(Identifier('user.validEmail'), set(), set(), set()))
        registry.commit_transaction()
        self.assertEquals(1, len(registry.get_all_contexts()))

    def test_rollback(self):
        class MockLocalLoader(LocalBusinessContextLoader):
            def load(self) -> Set[BusinessContext]:
                return set()

        class MockRemoteLoader(RemoteLoader):
            def load_contexts(self, identifiers: Set[Identifier]) -> Set[BusinessContext]:
                return set()

        registry = Registry(MockLocalLoader(), RemoteBusinessContextLoader(MockRemoteLoader()))
        registry.begin_transaction()
        registry.save_or_update_context(BusinessContext(Identifier('user.validEmail'), set(), set(), set()))
        registry.rollback_transaction()
        self.assertEquals(0, len(registry.get_all_contexts()))
