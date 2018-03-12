import unittest

from business_context.business_operation import BusinessOperationContext, UndefinedInputParameterException, UndefinedFunctionException


class BusinessOperationContextTest(unittest.TestCase):
    def test_init(self):
        context = BusinessOperationContext('user.create')
        self.assertEqual('user.create', context.name)

    def test_set_input_parameter(self):
        context = BusinessOperationContext('user.create')
        context.set_input_parameter("name", "John Doe")
        self.assertEqual("John Doe", context.get_input_parameter("name"))

    def test_get_undefined_input_parameter(self):
        context = BusinessOperationContext('user.create')
        self.assertRaises(UndefinedInputParameterException, context.get_input_parameter, "name")

    def test_set_function(self):
        context = BusinessOperationContext('user.create')
        context.set_function("sum", lambda x, y: x + y)
        self.assertEqual(2, context.get_function("sum")(1, 1))

    def test_get_undefined_function(self):
        context = BusinessOperationContext('user.create')
        self.assertRaises(UndefinedFunctionException, context.get_function, "func")
