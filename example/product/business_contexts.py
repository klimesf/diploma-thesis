from typing import Optional
from business_context.rule import *
from business_context.registry import *
from business_context.expression import *
from business_context.context import *
from business_context.operation_context import *
from business_context_grpc.business_context_client import retrieve_contexts

# Identifiers
list_all_products_identifier = Identifier("product", "listAll")
product_detail_identifier = Identifier("product", "detail")

# Conditions
product_is_not_hidden = Precondition(
    name='Cannot use hidden product',
    condition=LogicalEquals(ObjectPropertyReference('product', 'hidden', ExpressionType.BOOL), Constant(value=False, type=ExpressionType.BOOL))
)

product_name_is_valid = Precondition(
    name='Product name must be at least 5 characters long',
    # TODO: add correct precondition
    condition=IsNotNull(VariableReference('name', ExpressionType.STRING))
)

filter_hidden_products = PostCondition(
    name='filterHiddenProducts',
    type=PostConditionType.FILTER_LIST_OF_OBJECTS,
    reference_name='item',
    condition=LogicalEquals(ObjectPropertyReference('item', 'hidden', ExpressionType.BOOL), Constant(value=True, type=ExpressionType.BOOL))
)

filter_buying_prices = PostCondition(
    name='filterProductsBuyingPrices',
    type=PostConditionType.FILTER_LIST_OF_OBJECTS_FIELD,
    reference_name='buyingPrice',
    condition=Constant(value=True, type=ExpressionType.BOOL)
)

filter_buying_price = PostCondition(
    name='filterProductBuyingPrice',
    type=PostConditionType.FILTER_OBJECT_FIELD,
    reference_name='buyingPrice',
    condition=Constant(value=True, type=ExpressionType.BOOL)
)

# Contexts
product_hidden = BusinessContext(
    identifier=Identifier("product", "hidden"),
    included_contexts=set(),
    preconditions={product_is_not_hidden},
    post_conditions=set()
)

product_stock = BusinessContext(
    identifier=Identifier("product", "stock"),
    included_contexts=set(),
    preconditions=set(),  # TODO: add preconditions
    post_conditions=set()
)

product_change_price = BusinessContext(
    identifier=Identifier("product", "changePrice"),
    included_contexts={Identifier("auth", "adminLoggedIn")},
    preconditions=set(),
    post_conditions=set()
)

product_create = BusinessContext(
    identifier=Identifier("product", "create"),
    included_contexts={Identifier("auth", "employeeLoggedIn")},
    preconditions={product_name_is_valid},
    post_conditions=set()
)

product_list_all = BusinessContext(
    identifier=Identifier("product", "listAll"),
    included_contexts=set(),
    preconditions=set(),
    post_conditions={filter_hidden_products, filter_buying_prices}
)

product_detail = BusinessContext(
    identifier=product_detail_identifier,
    included_contexts=set(),
    preconditions=set(),
    post_conditions={filter_buying_price}
)


class LocalLoader(LocalBusinessContextLoader):
    def load(self) -> Set[BusinessContext]:
        return {product_hidden, product_stock, product_change_price, product_create, product_list_all, product_detail}


class GrpcRemoteLoader(RemoteLoader):
    def __init__(self, addresses):
        self._addresses = addresses

    def load_contexts(self, identifiers: Set[Identifier]) -> Set[BusinessContext]:
        result = set()

        prefixes = {}
        for identifier in identifiers:
            if identifier.prefix not in prefixes:
                prefixes[identifier.prefix] = []
            prefixes[identifier.prefix].append(identifier)

        for prefix, identifiers in prefixes.items():
            if prefix not in self._addresses:
                raise BaseException("Could not find service for prefix " + prefix)
            for retrieved in retrieve_contexts(identifiers, self._addresses[prefix]['host'], self._addresses[prefix]['port']):
                result.add(retrieved)

        return result
