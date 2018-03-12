import unittest

from business_context.expression import *


class FunctionCallTest(unittest.TestCase):
    def test(self):
        context = BusinessOperationContext('user.create')
        context.set_function('add', lambda x, y: x + y)
        expr = FunctionCall('add', 1, 2)
        self.assertEquals(3, expr.interpret(context))


class IsNotNullTest(unittest.TestCase):
    def test(self):
        context = BusinessOperationContext('user.create')
        expr = IsNotNull(Constant(True))
        self.assertTrue(expr.interpret(context))
        expr = IsNotNull(Constant(False))
        self.assertTrue(expr.interpret(context))
        expr = IsNotNull(Constant(None))
        self.assertFalse(expr.interpret(context))


class ObjectPropertyReferenceTest(unittest.TestCase):
    class User:
        name: str

        def __init__(self, name):
            self.name = name

    def test(self):
        context = BusinessOperationContext('user.create')
        context.set_input_parameter('user', self.User('John Doe'))
        expr = ObjectPropertyReference('user', 'name')
        self.assertEquals('John Doe', expr.interpret(context))
        expr = ObjectPropertyReference('user', 'email')
        self.assertRaises(AttributeError, expr.interpret, context)


class VariableReferenceTest(unittest.TestCase):
    def test(self):
        context = BusinessOperationContext('user.create')
        context.set_input_parameter('name', 'John Doe')
        expr = VariableReference('name')
        self.assertEquals('John Doe', expr.interpret(context))


class LogicalAndTest(unittest.TestCase):
    def test(self):
        context = BusinessOperationContext('user.create')
        expr = LogicalAnd(Constant(True), Constant(True))
        self.assertTrue(expr.interpret(context))
        expr = LogicalAnd(Constant(True), Constant(False))
        self.assertFalse(expr.interpret(context))
        expr = LogicalAnd(Constant(False), Constant(True))
        self.assertFalse(expr.interpret(context))
        expr = LogicalAnd(Constant(False), Constant(False))
        self.assertFalse(expr.interpret(context))


class LogicalEqualsTest(unittest.TestCase):
    def test(self):
        context = BusinessOperationContext('user.create')
        expr = LogicalEquals(Constant(True), Constant(True))
        self.assertTrue(expr.interpret(context))
        expr = LogicalEquals(Constant(True), Constant(False))
        self.assertFalse(expr.interpret(context))
        expr = LogicalEquals(Constant(False), Constant(True))
        self.assertFalse(expr.interpret(context))
        expr = LogicalEquals(Constant(False), Constant(False))
        self.assertTrue(expr.interpret(context))


class LogicalNegateTest(unittest.TestCase):
    def test(self):
        context = BusinessOperationContext('user.create')
        expr = LogicalNegate(Constant(True))
        self.assertFalse(expr.interpret(context))
        expr = LogicalNegate(Constant(False))
        self.assertTrue(expr.interpret(context))


class LogicalOrTest(unittest.TestCase):
    def test(self):
        context = BusinessOperationContext('user.create')
        expr = LogicalOr(Constant(True), Constant(True))
        self.assertTrue(expr.interpret(context))
        expr = LogicalOr(Constant(True), Constant(False))
        self.assertTrue(expr.interpret(context))
        expr = LogicalOr(Constant(False), Constant(True))
        self.assertTrue(expr.interpret(context))
        expr = LogicalOr(Constant(False), Constant(False))
        self.assertFalse(expr.interpret(context))
