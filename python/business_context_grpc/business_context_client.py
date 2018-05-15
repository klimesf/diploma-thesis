import grpc
from typing import Set
from business_context.context import BusinessContext
from business_context.identifier import Identifier
from business_context.registry import RemoteLoader
from . import business_context_pb2
from . import business_context_pb2_grpc
from . import helpers


def retrieve_contexts(identifiers: [Identifier], host: str, port: int) -> Set[BusinessContext]:
    channel = grpc.insecure_channel(host + ':' + port.__str__())
    stub = business_context_pb2_grpc.BusinessContextServerStub(channel)
    required_contexts = list(map(Identifier.__str__, identifiers))
    response = stub.FetchContexts(business_context_pb2.BusinessContextRequestMessage(requiredContexts=required_contexts))
    return set(map(helpers.build_context, response.contexts))


def retrieve_all_contexts(host: str, port: int) -> Set[BusinessContext]:
    channel = grpc.insecure_channel(host + ':' + port.__str__())
    stub = business_context_pb2_grpc.BusinessContextServerStub(channel)
    response = stub.FetchAllContexts(business_context_pb2.Empty())
    return set(map(helpers.build_context, response.contexts))


def update_context(context: BusinessContext, host: str, port: int):
    channel = grpc.insecure_channel(host + ':' + port.__str__())
    stub = business_context_pb2_grpc.BusinessContextServerStub(channel)
    stub.UpdateContext(business_context_pb2.BusinessContextUpdateRequestMessage(context=helpers.build_context_message(context)))


class GrpcRemoteLoader(RemoteLoader):
    def __init__(self, addresses):
        self._addresses = addresses

    def load_contexts(self, identifiers: Set[Identifier]) -> Set[BusinessContext]:
        result = set()

        prefixes = {}
        for identifier in identifiers:
            if identifier.prefix not in prefixes:
                prefixes[identifier.prefix] = []
            prefixes[identifier.prefix].append(identifier)

        for prefix, identifiers in prefixes.items():
            if prefix not in self._addresses:
                raise BaseException("Could not find service for prefix " + prefix)
            for retrieved in retrieve_contexts(identifiers, self._addresses[prefix]['host'], self._addresses[prefix]['port']):
                result.add(retrieved)

        return result
