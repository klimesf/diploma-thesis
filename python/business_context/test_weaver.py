import unittest

from typing import Set
from business_context.context import BusinessContext
from business_context.expression import Constant, ExpressionType, LogicalEquals, ObjectPropertyReference
from business_context.identifier import Identifier
from business_context.operation_context import OperationContext
from business_context.registry import LocalBusinessContextLoader, RemoteLoader, Registry, RemoteBusinessContextLoader
from business_context.rule import Precondition, PostCondition, PostConditionType
from business_context.weaver import Weaver, BusinessRulesCheckFailed

auth_logged_in_identifier = Identifier("auth", "userLoggedIn")
user_create_identifier = Identifier("user", "create")
list_all_products_identifier = Identifier("product", "listAll")

precondition_1 = Precondition(name='precondition_1', condition=Constant(value=True, type=ExpressionType.BOOL))
precondition_2 = Precondition(name='precondition_2', condition=Constant(value=False, type=ExpressionType.BOOL))

post_condition = PostCondition('randomPostCondition', PostConditionType.FILTER_OBJECT_FIELD, 'user', Constant(value=True, type=ExpressionType.BOOL))
filter_user_email = PostCondition('filterUserEmail', PostConditionType.FILTER_OBJECT_FIELD, 'email', Constant(value=True, type=ExpressionType.BOOL))
filter_hidden_products = PostCondition('filterHiddenProducts', PostConditionType.FILTER_LIST_OF_OBJECTS, 'item',
                                       LogicalEquals(ObjectPropertyReference('item', 'hidden', ExpressionType.BOOL),
                                                     Constant(value=True, type=ExpressionType.BOOL)))
filter_buying_price = PostCondition(name='filterProductBuyingPrice', type=PostConditionType.FILTER_LIST_OF_OBJECTS_FIELD,
                                    reference_name='buying_price', condition=Constant(value=True, type=ExpressionType.BOOL))

auth_logged_in = BusinessContext(auth_logged_in_identifier, set(), {precondition_1}, {post_condition})
user_create = BusinessContext(user_create_identifier, {auth_logged_in_identifier}, {precondition_2}, {filter_user_email})
list_all_products = BusinessContext(list_all_products_identifier, set(), set(), {filter_hidden_products, filter_buying_price})


class MockLocalLoader(LocalBusinessContextLoader):
    def load(self) -> Set[BusinessContext]:
        return {user_create, list_all_products}


class MockRemoteLoader(RemoteLoader):
    def __init__(self):
        self._contexts = {auth_logged_in_identifier: auth_logged_in}

    def load_contexts(self, identifiers: Set[Identifier]) -> Set[BusinessContext]:
        result = set()
        for identifier in identifiers:
            if identifier in self._contexts:
                result.add(self._contexts[identifier])
        return result


class User:
    def __init__(self, name: str, email: str):
        self.name = name
        self.email = email


class Product:
    def __init__(self, name: str, buying_price: float, hidden: bool):
        self.name = name
        self.buying_price = buying_price
        self.hidden = hidden


class WeaverTest(unittest.TestCase):

    def test_evaluate_preconditions(self):
        registry = Registry(MockLocalLoader(), RemoteBusinessContextLoader(MockRemoteLoader()))
        weaver = Weaver(registry)
        operation_context = OperationContext('user.create')
        self.assertRaises(BusinessRulesCheckFailed, weaver.evaluate_preconditions, operation_context)

    def test_apply_post_conditions_filter_object_field(self):
        registry = Registry(MockLocalLoader(), RemoteBusinessContextLoader(MockRemoteLoader()))
        weaver = Weaver(registry)
        operation_context = OperationContext('user.create')
        operation_context.set_output(User('John Doe', 'john.doe@example.com'))
        weaver.apply_post_conditions(operation_context)
        self.assertEqual('John Doe', operation_context.get_output().name)
        self.assertEqual(None, operation_context.get_output().email)

    def test_apply_post_conditions_filter_list_of_objects(self):
        registry = Registry(MockLocalLoader(), RemoteBusinessContextLoader(MockRemoteLoader()))
        weaver = Weaver(registry)
        operation_context = OperationContext('product.listAll')
        operation_context.set_output([Product('Toothpaste', 123.45, False), Product('Toothbrush', 123.45, True)])
        weaver.apply_post_conditions(operation_context)
        self.assertEqual(1, len(operation_context.get_output()))
        self.assertEqual('Toothpaste', operation_context.get_output()[0].name)
        self.assertEqual(None, operation_context.get_output()[0].buying_price)
