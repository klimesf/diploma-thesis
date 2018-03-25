from enum import Enum
from typing import List, Dict

from business_context.operation_context import OperationContext


class ExpressionType(Enum):
    STRING = 'string'
    NUMBER = 'number'
    BOOL = 'bool'
    VOID = 'void'
    OBJECT = 'object'


class Expression:
    def interpret(self, context: OperationContext):
        pass

    def get_name(self):
        pass

    def get_arguments(self) -> List['Expression']:
        pass

    def get_properties(self) -> Dict[str, str]:
        pass


class Constant(Expression):
    def __init__(self, value: any, type: ExpressionType):
        self.value = value
        self.type = type

    def interpret(self, context: OperationContext):
        return self.value

    def get_name(self):
        return 'constant'

    def get_arguments(self) -> List[Expression]:
        return []

    def get_properties(self) -> Dict[str, str]:
        return {
            'value': self.value,
            'type': self.type.value,
        }

    def __str__(self) -> str:
        return self.value.__str__()


class FunctionCall(Expression):
    def __init__(self, method_name: str, type: ExpressionType, arguments: List[Expression]):
        self.method_name = method_name
        self.type = type
        self.arguments = arguments

    def interpret(self, context: OperationContext):
        args = []
        for arg in self.arguments:
            args.append(arg.interpret(context))
        return context.get_function(self.method_name)(*args)

    def get_name(self):
        return 'function-call'

    def get_arguments(self) -> List[Expression]:
        return self.arguments

    def get_properties(self) -> Dict[str, str]:
        return {
            'methodName': self.method_name,
            'type': self.type.value
        }

    def __str__(self) -> str:
        return 'call ' + self.method_name + '()'


class IsNotNull(Expression):
    def __init__(self, argument):
        self.argument = argument

    def interpret(self, context: OperationContext):
        return self.argument.interpret(context) is not None

    def get_name(self):
        return 'is-not-null'

    def get_arguments(self) -> List[Expression]:
        return [self.argument]

    def get_properties(self) -> Dict[str, str]:
        return {}

    def __str__(self) -> str:
        return self.argument.__str__() + ' is not null'


class ObjectPropertyReference(Expression):
    def __init__(self, object_name: str, property_name: str, type: ExpressionType):
        self.object_name = object_name
        self.property_name = property_name
        self.type = type

    def interpret(self, context: OperationContext):
        return getattr(context.get_input_parameter(self.object_name), self.property_name)

    def get_name(self):
        return 'object-property-reference'

    def get_arguments(self) -> List[Expression]:
        return []

    def get_properties(self) -> Dict[str, str]:
        return {
            'objectName': self.object_name,
            'propertyName': self.property_name,
            'type': self.type.value,
        }

    def __str__(self) -> str:
        return '$' + self.object_name + '.' + self.property_name


class VariableReference(Expression):
    def __init__(self, name: str, type: ExpressionType):
        self.name = name
        self.type = type

    def interpret(self, context: OperationContext):
        return context.get_input_parameter(self.name)

    def get_name(self):
        return 'variable-reference'

    def get_arguments(self) -> List['Expression']:
        return []

    def get_properties(self) -> Dict[str, str]:
        return {
            'name': self.name,
            'type': self.type.value,
        }

    def __str__(self) -> str:
        return '$' + self.name


class LogicalAnd(Expression):
    def __init__(self, left: Expression, right: Expression):
        self.left = left
        self.right = right

    def interpret(self, context: OperationContext):
        return self.left.interpret(context) and self.right.interpret(context)

    def get_name(self):
        return 'logical-and'

    def get_arguments(self) -> List[Expression]:
        return [self.left, self.right]

    def get_properties(self) -> Dict[str, str]:
        return {}

    def __str__(self) -> str:
        return self.left.__str__() + ' and ' + self.right.__str__()


class LogicalEquals(Expression):
    def __init__(self, left: Expression, right: Expression):
        self.left = left
        self.right = right

    def interpret(self, context: OperationContext):
        return self.left.interpret(context) == self.right.interpret(context)

    def get_name(self):
        return 'logical-equals'

    def get_arguments(self) -> List[Expression]:
        return [self.left, self.right]

    def get_properties(self) -> Dict[str, str]:
        return {}

    def __str__(self) -> str:
        return self.left.__str__() + ' equals ' + self.right.__str__()


class LogicalNegate(Expression):
    def __init__(self, argument: Expression):
        self.argument = argument

    def interpret(self, context: OperationContext):
        return not self.argument.interpret(context)

    def get_name(self):
        return 'logical-negate'

    def get_arguments(self) -> List['Expression']:
        return [self.argument]

    def get_properties(self) -> Dict[str, str]:
        return {}

    def __str__(self) -> str:
        return 'not ' + self.argument.__str__()


class LogicalOr(Expression):
    def __init__(self, left: Expression, right: Expression):
        self.left = left
        self.right = right

    def interpret(self, context: OperationContext):
        return self.left.interpret(context) or self.right.interpret(context)

    def get_name(self):
        return 'logical-or'

    def get_arguments(self) -> List[Expression]:
        return [self.left, self.right]

    def get_properties(self) -> Dict[str, str]:
        return {}

    def __str__(self) -> str:
        return self.left.__str__() + ' or ' + self.right.__str__()
