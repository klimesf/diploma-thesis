from typing import List, Dict
from business_context.expression import *
from business_context.context import BusinessContext
from business_context.identifier import Identifier
from business_context.rule import Precondition, PostCondition, PostConditionType
from business_context_grpc.business_context_pb2 import BusinessContextMessage, PreconditionMessage, PostConditionMessage, ExpressionMessage, PostConditionTypeMessage, ExpressionPropertyMessage


def convert_expression_type(name: str) -> ExpressionType:
    converter = {
        'string': ExpressionType.STRING,
        'number': ExpressionType.NUMBER,
        'bool': ExpressionType.BOOL,
        'void': ExpressionType.VOID,
        'object': ExpressionType.OBJECT,
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
        'is-not-blank': lambda m: IsNotBlank(argument=build_expression(m.arguments[0])),
        'object-property-reference': lambda m: ObjectPropertyReference(object_name=find_property_by_name('objectName', m.properties),
                                                                       property_name=find_property_by_name('propertyName', m.properties),
                                                                       type=convert_expression_type(find_property_by_name('type', m.properties))),
        'variable-reference': lambda m: VariableReference(name=find_property_by_name('name', m.properties),
                                                          type=convert_expression_type(find_property_by_name('type', m.properties))),
        'logical-and': lambda m: LogicalAnd(left=build_expression(m.arguments[0]), right=build_expression(m.arguments[1])),
        'logical-equals': lambda m: LogicalEquals(left=build_expression(m.arguments[0]), right=build_expression(m.arguments[1])),
        'logical-negate': lambda m: LogicalNegate(argument=build_expression(m.arguments[0])),
        'logical-or': lambda m: LogicalOr(left=build_expression(m.arguments[0]), right=build_expression(m.arguments[1])),
        'numeric-add': lambda m: NumericAdd(left=build_expression(m.arguments[0]), right=build_expression(m.arguments[1])),
        'numeric-divide': lambda m: NumericDivide(left=build_expression(m.arguments[0]), right=build_expression(m.arguments[1])),
        'numeric-gte': lambda m: NumericGreaterOrEqualTo(left=build_expression(m.arguments[0]), right=build_expression(m.arguments[1])),
        'numeric-gt': lambda m: NumericGreaterThan(left=build_expression(m.arguments[0]), right=build_expression(m.arguments[1])),
        'numeric-lte': lambda m: NumericLessOrEqualTo(left=build_expression(m.arguments[0]), right=build_expression(m.arguments[1])),
        'numeric-lt': lambda m: NumericLessThan(left=build_expression(m.arguments[0]), right=build_expression(m.arguments[1])),
        'numeric-multiply': lambda m: NumericMultiply(left=build_expression(m.arguments[0]), right=build_expression(m.arguments[1])),
        'numeric-subtract': lambda m: NumericSubtract(left=build_expression(m.arguments[0]), right=build_expression(m.arguments[1])),
    }
    if message.name not in matcher:
        raise Exception('Unknown Expression ' + message.name)
    return matcher[message.name](message)


def build_precondition(message: PreconditionMessage) -> Precondition:
    return Precondition(message.name, build_expression(message.condition))


def convert_post_condition_message_type(type: PostConditionTypeMessage) -> PostConditionType:
    converter = {
        1: PostConditionType.FILTER_OBJECT_FIELD,
        2: PostConditionType.FILTER_LIST_OF_OBJECTS,
        3: PostConditionType.FILTER_LIST_OF_OBJECTS_FIELD,
    }
    if type not in converter:
        raise Exception('Unknown post condition type')
    return converter[type]


def build_post_condition(message: PostConditionMessage) -> PostCondition:
    return PostCondition(name=message.name, type=convert_post_condition_message_type(message.type), reference_name=message.referenceName,
                         condition=build_expression(message.condition))


def build_context(message: BusinessContextMessage) -> BusinessContext:
    included_contexts = set(map(Identifier, message.includedContexts))
    preconditions = set(map(build_precondition, message.preconditions))
    post_conditions = set(map(build_post_condition, message.postConditions))
    return BusinessContext(identifier=Identifier(message.prefix, message.name), included_contexts=included_contexts,
                           preconditions=preconditions, post_conditions=post_conditions)


def convert_post_condition_type(type: PostConditionType) -> PostConditionTypeMessage:
    converter = {
        PostConditionType.FILTER_OBJECT_FIELD: 1,
        PostConditionType.FILTER_LIST_OF_OBJECTS: 2,
        PostConditionType.FILTER_LIST_OF_OBJECTS_FIELD: 3,
    }
    if type not in converter:
        raise Exception('Unknown post condition type')
    return converter[type]
    pass


def build_expression_message(expression: Expression) -> ExpressionMessage:
    return ExpressionMessage(name=expression.get_name(), properties=build_expression_properties(expression.get_properties()),
                             arguments=build_expression_arguments(expression.get_arguments()))


def build_expression_properties(properties: Dict[str, str]) -> List[ExpressionPropertyMessage]:
    messages = []
    for key, value in properties.items():
        message = ExpressionPropertyMessage(key=key, value=value.__str__())
        messages.append(message)
    return messages


def build_expression_arguments(arguments: List[Expression]) -> List[ExpressionMessage]:
    return list(map(build_expression_message, arguments))


def build_post_condition_message(post_condition: PostCondition) -> PostConditionMessage:
    return PostConditionMessage(name=post_condition.name, type=convert_post_condition_type(post_condition.type),
                                referenceName=post_condition.reference_name, condition=build_expression_message(post_condition.condition))


def build_precondition_message(precondition: Precondition) -> PreconditionMessage:
    return PreconditionMessage(name=precondition.name, condition=build_expression_message(precondition.condition))


def build_context_message(context: BusinessContext) -> BusinessContextMessage:
    included_contexts = list(map(Identifier.__str__, context.included_contexts))
    precondition_messages = list(map(build_precondition_message, context.preconditions))
    post_condition_messages = list(map(build_post_condition_message, context.post_conditions))
    return BusinessContextMessage(prefix=context.identifier.prefix, name=context.identifier.name, includedContexts=included_contexts,
                                  preconditions=precondition_messages, postConditions=post_condition_messages)
