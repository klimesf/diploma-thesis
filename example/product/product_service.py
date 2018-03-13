from flask import Flask, jsonify
from business_context.rule import *
from business_context.expression import *
from business_context.context import *
from business_context.registry import *
from business_context.weaver import *
from business_context.operation_context import *
from business_context_grpc.business_context_server import *

app = Flask(__name__)


class MockLocalLoader(LocalBusinessContextLoader):
    def load(self) -> Set[BusinessContext]:
        return {list_all_products}


class MockRemoteLoader(RemoteLoader):
    def __init__(self):
        self._contexts = {}

    def load_contexts(self, identifiers: Set[Identifier]) -> Set[BusinessContext]:
        result = set()
        for identifier in identifiers:
            if identifier in self._contexts:
                result.add(self._contexts[identifier])
        return result


list_all_products_identifier = Identifier("product", "listAll")

filter_hidden_products = PostCondition('filterHiddenProducts', PostConditionType.FILTER_LIST_OF_OBJECTS, 'item',
                                       LogicalEquals(ObjectPropertyReference('item', 'hidden', ExpressionType.BOOL),
                                                     Constant(value=True, type=ExpressionType.BOOL)))
filter_buying_price = PostCondition(name='filterProductBuyingPrice', type=PostConditionType.FILTER_LIST_OF_OBJECTS_FIELD,
                                    reference_name='buying_price', condition=Constant(value=True, type=ExpressionType.BOOL))
list_all_products = BusinessContext(list_all_products_identifier, set(), set(), {filter_hidden_products, filter_buying_price})

registry = Registry(MockLocalLoader(), RemoteBusinessContextLoader(MockRemoteLoader()))

weaver = Weaver(registry)


class Product:
    def __init__(self, id: int, costPrice: int, sellPrice: int, stockCount: int, hidden: bool):
        self.id = id
        self.costPrice = costPrice
        self.sellPrice = sellPrice
        self.stockCount = stockCount
        self.hidden = hidden


class ProductRepository:
    def __init__(self):
        self.products = {
            1: Product(1, None, None, None, False),
            2: Product(2, None, None, None, True),
        }

    @business_operation("product.listAll", weaver)
    def get_all(self) -> List[Product]:
        return list(self.products.values())


product_repository = ProductRepository()


@app.route("/")
def list_all_products():
    result = []
    for product in product_repository.get_all():
        result.append({
            'id': product.id,
            'costPrice': product.costPrice,
        })
    return jsonify(result)


if __name__ == '__main__':
    server = ServerThread(registry=registry, sleep_interval=60 * 60 * 24, port=5552)
    server.start()
    app.run(port=5502)
