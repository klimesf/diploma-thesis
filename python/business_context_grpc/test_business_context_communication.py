import unittest
import time
from typing import Set
from business_context.context import BusinessContext
from business_context.expression import Constant, ExpressionType, IsNotNull, VariableReference, LogicalAnd
from business_context.identifier import Identifier
from business_context.registry import LocalBusinessContextLoader, RemoteLoader, RemoteBusinessContextLoader, Registry
from business_context.rule import PostCondition, PostConditionType, Precondition
from business_context_grpc.business_context_client import retrieve_contexts
from business_context_grpc.business_context_server import ServerThread


class GrpcBusinessOperationContextExchangeTest(unittest.TestCase):

    def test_communication(self):
        auth_logged_in_identifier = Identifier("auth", "userLoggedIn")
        user_create_identifier = Identifier("user", "create")

        propertiesNotNull = Precondition(name='propertiesNotNull',
                                         condition=LogicalAnd(IsNotNull(VariableReference(name='email', type=ExpressionType.STRING)),
                                                              IsNotNull(VariableReference(name='name', type=ExpressionType.STRING))))

        hide_user_email = PostCondition(name='hideUserEmail', type=PostConditionType.FILTER_OBJECT_FIELD,
                                        reference_name='email', condition=Constant(True, type=ExpressionType.BOOL))

        auth_logged_in = BusinessContext(auth_logged_in_identifier, set(), set(), set())
        user_create = BusinessContext(user_create_identifier, {auth_logged_in_identifier}, {propertiesNotNull}, {hide_user_email})

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

        server_registry = Registry(MockLocalLoader(), RemoteBusinessContextLoader(MockRemoteLoader()))

        port = 5551
        server = ServerThread(registry=server_registry, sleep_interval=1, port=port)
        server.start()
        time.sleep(1)  # Give server time to initialize

        retrieved = retrieve_contexts({user_create_identifier}, port)
        self.assertEqual(1, len(retrieved))
        received_context = retrieved.pop()
        self.assertEqual(user_create_identifier, received_context.identifier)
        self.assertEqual(1, len(received_context.included_contexts))
        self.assertEqual(1, len(received_context.preconditions))
        self.assertEqual(1, len(received_context.post_conditions))

        server.stop()
        server.join()
