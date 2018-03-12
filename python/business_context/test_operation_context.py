import unittest
from business_context.operation_context import *


class OperationContextTest(unittest.TestCase):
    def test_init(self):
        context = OperationContext('user.create')
        self.assertEqual('user.create', context.name)

    def test_get_undefined_input_parameter(self):
        context = OperationContext('user.create')
        self.assertRaises(UndefinedInputParameterException, context.get_input_parameter, "undefined")

    def test_set_input_parameter(self):
        context = OperationContext('user.create')
        context.set_input_parameter("name", "John Doe")
        self.assertEqual("John Doe", context.get_input_parameter("name"))

    def test_set_function(self):
        context = OperationContext('user.create')
        context.set_function("sum", lambda x, y: x + y)
        self.assertEqual(2, context.get_function("sum")(1, 1))

    def test_get_undefined_function(self):
        context = OperationContext('user.create')
        self.assertRaises(UndefinedFunctionException, context.get_function, "func")
