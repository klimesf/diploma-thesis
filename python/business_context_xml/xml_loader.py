from typing import Set

from xml.dom.minidom import parseString
from business_context.context import BusinessContext
from business_context.rule import Precondition, PostCondition, PostConditionType
from business_context.expression import Constant, ExpressionType, IsNotNull, VariableReference, LogicalAnd, FunctionCall, IsNotBlank, ObjectPropertyReference, LogicalEquals, LogicalNegate, LogicalOr
from business_context.identifier import Identifier
from business_context.registry import LocalBusinessContextLoader


def load_included_contexts(root):
    included = set()
    for identifier in root.getElementsByTagName('includedContext'):
        included.add(Identifier(identifier.attributes['prefix'].value, identifier.attributes['name'].value))
    return included


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


def convert_post_condition_message_type(type: str) -> PostConditionType:
    converter = {
        'FILTER_OBJECT_FIELD': PostConditionType.FILTER_OBJECT_FIELD,
        'FILTER_LIST_OF_OBJECTS': PostConditionType.FILTER_LIST_OF_OBJECTS,
        'FILTER_LIST_OF_OBJECTS_FIELD': PostConditionType.FILTER_LIST_OF_OBJECTS_FIELD,
    }
    if type not in converter:
        raise Exception('Unknown post condition type ' + type)
    return converter[type]


def get_text(nodelist):
    rc = ""
    for node in nodelist:
        if node.nodeType == node.TEXT_NODE:
            rc = rc + node.data
    return rc


def get_non_text(nodelist):
    nodes = []
    for node in nodelist:
        if node.nodeType == node.TEXT_NODE:
            continue
        nodes.append(node)
    return nodes


def build_expression(el):
    matcher = {
        'constant': lambda el: Constant(value=el.attributes['value'].value,
                                        type=convert_expression_type(el.attributes['type'].value)),
        'functionCall': lambda el: FunctionCall(method_name=el.attributes['methodName'].value,
                                                type=convert_expression_type(el.attributes['type'].value),
                                                arguments=list(map(build_expression, get_non_text(el.childNodes)))),
        'isNotNull': lambda el: IsNotNull(argument=build_expression(get_non_text(get_non_text(el.getElementsByTagName('argument'))[0].childNodes)[0])),
        'isNotBlank': lambda el: IsNotBlank(argument=build_expression(get_non_text(get_non_text(el.getElementsByTagName('argument'))[0].childNodes)[0])),
        'objectPropertyReference': lambda el: ObjectPropertyReference(object_name=el.attributes['objectName'].value,
                                                                      property_name=el.attributes['propertyName'].value,
                                                                      type=convert_expression_type(el.attributes['type'].value)),
        'variableReference': lambda el: VariableReference(name=el.attributes['name'].value,
                                                          type=convert_expression_type(el.attributes['type'].value)),
        'logicalAnd': lambda el: LogicalAnd(left=build_expression(get_non_text(el.getElementsByTagName('left')[0].childNodes)[0]), right=build_expression(get_non_text(el.getElementsByTagName('right')[0].childNodes)[0])),
        'logicalEquals': lambda el: LogicalEquals(left=build_expression(get_non_text(el.getElementsByTagName('left')[0].childNodes)[0]), right=build_expression(get_non_text(el.getElementsByTagName('right')[0].childNodes)[0])),
        'logicalNegate': lambda el: LogicalNegate(argument=build_expression(get_non_text(el.getElementsByTagName('argument')[0].childNodes)[0])),
        'logicalOr': lambda el: LogicalOr(left=build_expression(get_non_text(el.getElementsByTagName('left')[0].childNodes)[0]), right=build_expression(get_non_text(el.getElementsByTagName('right')[0].childNodes)[0])),
    }
    expression_name = el.tagName
    if expression_name not in matcher:
        raise Exception('Unknown Expression ' + expression_name)
    return matcher[expression_name](el)


def load_preconditions(root):
    preconditions = set()
    for precondition in root.getElementsByTagName('precondition'):
        preconditions.add(Precondition(
            name=precondition.attributes['name'].value,
            condition=build_expression(get_non_text(precondition.getElementsByTagName('condition')[0].childNodes)[0])
        ))
    return preconditions


def load_post_conditions(root):
    post_conditions = set()
    for post_condition in root.getElementsByTagName('postCondition'):
        post_conditions.add(PostCondition(
            name=post_condition.attributes['name'].value,
            type=convert_post_condition_message_type(get_text(post_condition.getElementsByTagName('type')[0].childNodes)),
            reference_name=get_text(post_condition.getElementsByTagName('referenceName')[0].childNodes),
            condition=build_expression(get_non_text(post_condition.getElementsByTagName('condition')[0].childNodes)[0])
        ))
    return post_conditions


def load_xml(document: str) -> BusinessContext:
    dom = parseString(document)
    root = dom.getElementsByTagName("businessContext")[0]

    return BusinessContext(
        Identifier(root.attributes['prefix'].value, root.attributes['name'].value),
        load_included_contexts(root),
        load_preconditions(root),
        load_post_conditions(root)
    )


class XmlBusinessContextLoader(LocalBusinessContextLoader):

    def __init__(self, files: list):
        super().__init__()
        self._files = files

    def load(self) -> Set[BusinessContext]:
        contexts = set()
        for file in self._files:
            with open(file, 'r') as xml:
                contexts.add(load_xml(xml.read()))
        return contexts
