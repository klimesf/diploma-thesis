from business_context.business_operation import BusinessOperationContext


class Expression:
    def interpret(self, context: BusinessOperationContext):
        pass

    def get_name(self):
        pass

    def get_arguments(self):
        pass

    def get_properties(self):
        pass


class Constant(Expression):
    value: any

    def __init__(self, value):
        self.value = value

    def interpret(self, context: BusinessOperationContext):
        return self.value

    def get_name(self):
        return 'constant'


class FunctionCall(Expression):
    methodName: str
    arguments: [any]

    def __init__(self, methodName, *arguments):
        self.methodName = methodName
        self.arguments = arguments

    def interpret(self, context: BusinessOperationContext):
        return context.get_function(self.methodName)(*self.arguments)

    def get_name(self):
        return 'function-call'


class IsNotNull(Expression):
    argument: Expression

    def __init__(self, argument):
        self.argument = argument

    def interpret(self, context: BusinessOperationContext):
        return self.argument.interpret(context) is not None

    def get_name(self):
        return 'is-not-null'


class ObjectPropertyReference(Expression):
    objectName: str
    propertyName: str

    def __init__(self, objectName, propertyName):
        self.objectName = objectName
        self.propertyName = propertyName

    def interpret(self, context: BusinessOperationContext):
        return getattr(context.get_input_parameter(self.objectName), self.propertyName)

    def get_name(self):
        return 'object-property-reference'


class VariableReference(Expression):
    name: str

    def __init__(self, name):
        self.name = name

    def interpret(self, context: BusinessOperationContext):
        return context.get_input_parameter(self.name)

    def get_name(self):
        return 'variable-reference'


class LogicalAnd(Expression):
    left: Expression
    right: Expression

    def __init__(self, left: Expression, right: Expression):
        self.left = left
        self.right = right

    def interpret(self, context: BusinessOperationContext):
        return self.left.interpret(context) and self.right.interpret(context)

    def get_name(self):
        return 'logical-and'


class LogicalEquals(Expression):
    left: Expression
    right: Expression

    def __init__(self, left: Expression, right: Expression):
        self.left = left
        self.right = right

    def interpret(self, context: BusinessOperationContext):
        return self.left.interpret(context) == self.right.interpret(context)

    def get_name(self):
        return 'logical-equals'


class LogicalNegate(Expression):
    argument: Expression
    right: Expression

    def __init__(self, argument: Expression):
        self.argument = argument

    def interpret(self, context: BusinessOperationContext):
        return not self.argument.interpret(context)

    def get_name(self):
        return 'logical-negate'


class LogicalOr(Expression):
    left: Expression
    right: Expression

    def __init__(self, left: Expression, right: Expression):
        self.left = left
        self.right = right

    def interpret(self, context: BusinessOperationContext):
        return self.left.interpret(context) or self.right.interpret(context)

    def get_name(self):
        return 'logical-or'
