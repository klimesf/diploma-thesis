import unittest

from business_context.expression import *


class FunctionCallTest(unittest.TestCase):
    def test(self):
        context = OperationContext('user.create')
        context.set_function('add', lambda x, y: x + y)
        expr = FunctionCall(method_name='add', type=ExpressionType.NUMBER, arguments=[Constant(1, ExpressionType.NUMBER), Constant(2, ExpressionType.NUMBER)])
        self.assertEqual(3, expr.interpret(context))


class IsNotNullTest(unittest.TestCase):
    def test(self):
        context = OperationContext('user.create')
        expr = IsNotNull(Constant(value=True, type=ExpressionType.BOOL))
        self.assertTrue(expr.interpret(context))
        expr = IsNotNull(Constant(value=False, type=ExpressionType.BOOL))
        self.assertTrue(expr.interpret(context))
        expr = IsNotNull(Constant(value=None, type=ExpressionType.BOOL))
        self.assertFalse(expr.interpret(context))


class IsNotBlankTest(unittest.TestCase):
    def test(self):
        context = OperationContext('user.create')
        expr = IsNotBlank(Constant(value="Hello", type=ExpressionType.STRING))
        self.assertTrue(expr.interpret(context))
        expr = IsNotBlank(Constant(value="", type=ExpressionType.STRING))
        self.assertFalse(expr.interpret(context))
        expr = IsNotBlank(Constant(value=None, type=ExpressionType.STRING))
        self.assertFalse(expr.interpret(context))


class ObjectPropertyReferenceTest(unittest.TestCase):
    class User:
        name: str

        def __init__(self, name):
            self.name = name

    def test(self):
        context = OperationContext('user.create')
        context.set_input_parameter('user', self.User('John Doe'))
        expr = ObjectPropertyReference(object_name='user', property_name='name', type=ExpressionType.STRING)
        self.assertEqual('John Doe', expr.interpret(context))
        expr = ObjectPropertyReference(object_name='user', property_name='email', type=ExpressionType.STRING)
        self.assertRaises(AttributeError, expr.interpret, context)


class VariableReferenceTest(unittest.TestCase):
    def test(self):
        context = OperationContext('user.create')
        context.set_input_parameter('name', 'John Doe')
        expr = VariableReference('name', type=ExpressionType.STRING)
        self.assertEqual('John Doe', expr.interpret(context))


class LogicalAndTest(unittest.TestCase):
    def test(self):
        context = OperationContext('user.create')
        expr = LogicalAnd(Constant(value=True, type=ExpressionType.BOOL), Constant(value=True, type=ExpressionType.BOOL))
        self.assertTrue(expr.interpret(context))
        expr = LogicalAnd(Constant(value=True, type=ExpressionType.BOOL), Constant(value=False, type=ExpressionType.BOOL))
        self.assertFalse(expr.interpret(context))
        expr = LogicalAnd(Constant(value=False, type=ExpressionType.BOOL), Constant(value=True, type=ExpressionType.BOOL))
        self.assertFalse(expr.interpret(context))
        expr = LogicalAnd(Constant(value=False, type=ExpressionType.BOOL), Constant(value=False, type=ExpressionType.BOOL))
        self.assertFalse(expr.interpret(context))


class LogicalEqualsTest(unittest.TestCase):
    def test(self):
        context = OperationContext('user.create')
        expr = LogicalEquals(Constant(value=True, type=ExpressionType.BOOL), Constant(value=True, type=ExpressionType.BOOL))
        self.assertTrue(expr.interpret(context))
        expr = LogicalEquals(Constant(value=True, type=ExpressionType.BOOL), Constant(value=False, type=ExpressionType.BOOL))
        self.assertFalse(expr.interpret(context))
        expr = LogicalEquals(Constant(value=False, type=ExpressionType.BOOL), Constant(value=True, type=ExpressionType.BOOL))
        self.assertFalse(expr.interpret(context))
        expr = LogicalEquals(Constant(value=False, type=ExpressionType.BOOL), Constant(value=False, type=ExpressionType.BOOL))
        self.assertTrue(expr.interpret(context))


class LogicalNegateTest(unittest.TestCase):
    def test(self):
        context = OperationContext('user.create')
        expr = LogicalNegate(Constant(value=True, type=ExpressionType.BOOL))
        self.assertFalse(expr.interpret(context))
        expr = LogicalNegate(Constant(value=False, type=ExpressionType.BOOL))
        self.assertTrue(expr.interpret(context))


class LogicalOrTest(unittest.TestCase):
    def test(self):
        context = OperationContext('user.create')
        expr = LogicalOr(Constant(value=True, type=ExpressionType.BOOL), Constant(value=True, type=ExpressionType.BOOL))
        self.assertTrue(expr.interpret(context))
        expr = LogicalOr(Constant(value=True, type=ExpressionType.BOOL), Constant(value=False, type=ExpressionType.BOOL))
        self.assertTrue(expr.interpret(context))
        expr = LogicalOr(Constant(value=False, type=ExpressionType.BOOL), Constant(value=True, type=ExpressionType.BOOL))
        self.assertTrue(expr.interpret(context))
        expr = LogicalOr(Constant(value=False, type=ExpressionType.BOOL), Constant(value=False, type=ExpressionType.BOOL))
        self.assertFalse(expr.interpret(context))
