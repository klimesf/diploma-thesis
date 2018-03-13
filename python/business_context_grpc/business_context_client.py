import grpc
from typing import Set
from business_context.context import BusinessContext
from business_context.expression import Expression
from business_context.identifier import Identifier
from business_context.rule import Precondition, PostCondition, PostConditionType
from business_context_grpc.business_context_pb2 import BusinessContextMessage, PreconditionMessage, ExpressionMessage, PostConditionMessage, PostConditionTypeMessage
from . import business_context_pb2
from . import business_context_pb2_grpc


def build_expression(message: ExpressionMessage) -> Expression:
    # TODO: implement
    return Expression()


def build_precondition(message: PreconditionMessage) -> Precondition:
    return Precondition(message.name, build_expression(message.condition))


def convert_post_condition_type(type: PostConditionTypeMessage) -> PostConditionType:
    converter = {
        PostConditionTypeMessage.FILTER_OBJECT_FIELD: PostConditionType.FILTER_OBJECT_FIELD,
        PostConditionTypeMessage.FILTER_LIST_OF_OBJECTS: PostConditionType.FILTER_LIST_OF_OBJECTS,
        PostConditionTypeMessage.FILTER_LIST_OF_OBJECTS_FIELD: PostConditionType.FILTER_LIST_OF_OBJECTS_FIELD,
    }
    if type not in converter:
        raise Exception('Unknown post condition type')
    return converter[type]


def build_post_condition(message: PostConditionMessage) -> PostCondition:
    return PostCondition(name=message.name, type=convert_post_condition_type(message.type), reference_name=message.referenceName,
                         condition=build_expression(message.condition))


def build_context(message: BusinessContextMessage) -> BusinessContext:
    included_contexts = set()
    preconditions = set()
    post_conditions = set()
    return BusinessContext(identifier=Identifier(message.prefix, message.name), included_contexts=included_contexts,
                           preconditions=preconditions, post_conditions=post_conditions)


def retrieve_contexts(identifiers: [Identifier], port: int) -> Set[BusinessContext]:
    channel = grpc.insecure_channel('localhost:' + port.__str__())
    stub = business_context_pb2_grpc.BusinessContextServerStub(channel)

    required_contexts = []
    for identifier in identifiers:
        required_contexts.append(identifier.__str__())
    response = stub.FetchContexts(business_context_pb2.BusinessContextRequestMessage(requiredContexts=required_contexts))

    contexts = set()
    for message in response.contexts:
        contexts.add(build_context(message))

    return contexts
