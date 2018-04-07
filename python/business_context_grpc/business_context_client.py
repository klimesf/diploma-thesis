import grpc
from typing import Set
from business_context.context import BusinessContext
from business_context.identifier import Identifier
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
