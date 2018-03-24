import grpc
from typing import Set, List
from business_context.context import BusinessContext
from business_context.expression import Expression, Constant, ExpressionType, FunctionCall, IsNotNull, ObjectPropertyReference, VariableReference, LogicalAnd, LogicalOr, LogicalEquals, LogicalNegate
from business_context.identifier import Identifier
from business_context.rule import Precondition, PostCondition, PostConditionType
from business_context_grpc.business_context_pb2 import BusinessContextMessage, PreconditionMessage, ExpressionMessage, PostConditionMessage, PostConditionTypeMessage, ExpressionPropertyMessage
from . import business_context_pb2
from . import business_context_pb2_grpc


def convert_expression_type(name: str) -> ExpressionType:
    converter = {
        'string': ExpressionType.STRING,
        'number': ExpressionType.NUMBER,
        'bool': ExpressionType.BOOL,
        'void': ExpressionType.VOID,
    }
    if name not in converter:
        raise Exception('Unknown expression type ' + name)
    return converter[name]


def find_property_by_name(name: str, properties: List[ExpressionPropertyMessage]) -> any:
    result = None
    for property in properties:
        if property.key == name:
            return property.value
    return result


def build_expression(message: ExpressionMessage) -> Expression:
    matcher = {
        'constant': lambda m: Constant(value=find_property_by_name('value', m.properties),
                                       type=convert_expression_type(find_property_by_name('type', m.properties))),
        'function-call': lambda m: FunctionCall(method_name=find_property_by_name('methodName', m.properties),
                                                type=convert_expression_type(find_property_by_name('type', m.properties)),
                                                arguments=list(map(build_expression, m.arguments))),
        'is-not-null': lambda m: IsNotNull(argument=build_expression(m.arguments[0])),
        'object-property-reference': lambda m: ObjectPropertyReference(object_name=find_property_by_name('objectName', m.properties),
                                                                       property_name=find_property_by_name('propertyName', m.properties),
                                                                       type=convert_expression_type(find_property_by_name('type', m.properties))),
        'variable-reference': lambda m: VariableReference(name=find_property_by_name('name', m.properties),
                                                          type=convert_expression_type(find_property_by_name('type', m.properties))),
        'logical-and': lambda m: LogicalAnd(left=build_expression(m.arguments[0]), right=build_expression(m.arguments[1])),
        'logical-equals': lambda m: LogicalEquals(left=build_expression(m.arguments[0]), right=build_expression(m.arguments[1])),
        'logical-negate': lambda m: LogicalNegate(argument=build_expression(m.arguments[0])),
        'logical-or': lambda m: LogicalOr(left=build_expression(m.arguments[0]), right=build_expression(m.arguments[1])),
    }
    if message.name not in matcher:
        raise Exception('Unknown Expression ' + message.name)
    return matcher[message.name](message)


def build_precondition(message: PreconditionMessage) -> Precondition:
    return Precondition(message.name, build_expression(message.condition))


def convert_post_condition_type(type: PostConditionTypeMessage) -> PostConditionType:
    converter = {
        1: PostConditionType.FILTER_OBJECT_FIELD,
        2: PostConditionType.FILTER_LIST_OF_OBJECTS,
        3: PostConditionType.FILTER_LIST_OF_OBJECTS_FIELD,
    }
    if type not in converter:
        raise Exception('Unknown post condition type')
    return converter[type]


def build_post_condition(message: PostConditionMessage) -> PostCondition:
    return PostCondition(name=message.name, type=convert_post_condition_type(message.type), reference_name=message.referenceName,
                         condition=build_expression(message.condition))


def build_context(message: BusinessContextMessage) -> BusinessContext:
    included_contexts = set(map(Identifier, message.includedContexts))
    preconditions = set(map(build_precondition, message.preconditions))
    post_conditions = set(map(build_post_condition, message.postConditions))
    return BusinessContext(identifier=Identifier(message.prefix, message.name), included_contexts=included_contexts,
                           preconditions=preconditions, post_conditions=post_conditions)


def retrieve_contexts(identifiers: [Identifier], host: str, port: int) -> Set[BusinessContext]:
    channel = grpc.insecure_channel(host + ':' + port.__str__())
    stub = business_context_pb2_grpc.BusinessContextServerStub(channel)
    required_contexts = list(map(Identifier.__str__, identifiers))
    response = stub.FetchContexts(business_context_pb2.BusinessContextRequestMessage(requiredContexts=required_contexts))
    return set(map(build_context, response.contexts))
