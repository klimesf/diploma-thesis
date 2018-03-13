import unittest
import time
from typing import Set
from business_context.context import BusinessContext
from business_context.identifier import Identifier
from business_context.registry import LocalBusinessContextLoader, RemoteLoader, RemoteBusinessContextLoader, Registry
from business_context_grpc.business_context_client import retrieve_contexts
from business_context_grpc.business_context_server import ServerThread


class GrpcBusinessOperationContextExchangeTest(unittest.TestCase):

    def test_communication(self):
        auth_logged_in_identifier = Identifier("auth", "userLoggedIn")
        user_create_identifier = Identifier("user", "create")
        auth_logged_in = BusinessContext(auth_logged_in_identifier, set(), set(), set())
        user_create = BusinessContext(user_create_identifier, set(), set(), set())

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
        time.sleep(1) # Give server time to initialize

        retrieved = retrieve_contexts({user_create_identifier}, port)
        self.assertEqual(1, len(retrieved))
        received_context = retrieved.pop()
        self.assertEqual(user_create_identifier, received_context.identifier)

        server.stop()
        server.join()
