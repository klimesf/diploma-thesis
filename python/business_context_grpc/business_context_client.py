import grpc

from business_context.business_context_identifier import BusinessContextIdentifier
from . import business_context_pb2
from . import business_context_pb2_grpc


def retrieve_contexts(identifiers: [BusinessContextIdentifier]):
    channel = grpc.insecure_channel('localhost:5555')
    stub = business_context_pb2_grpc.BusinessContextServerStub(channel)
    response = stub.FetchContexts(business_context_pb2.BusinessContextRequestMessage())
    # TODO: implement transformation into AST
    print("Greeter client received: " + response.message)
