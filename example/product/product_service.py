from flask import Flask, jsonify, abort, request
from typing import Optional, List
from business_context.registry import *
from business_context.weaver import *
from business_context.operation_context import *
from business_context_grpc.business_context_server import *
from business_context_grpc.business_context_client import GrpcRemoteLoader
from business_context_xml.xml_loader import XmlBusinessContextLoader

app = Flask(__name__)

registry = Registry(
    XmlBusinessContextLoader([
        'business-contexts/changePrice.xml',
        'business-contexts/changeStock.xml',
        'business-contexts/create.xml',
        'business-contexts/detail.xml',
        'business-contexts/hidden.xml',
        'business-contexts/listAll.xml',
    ]),
    RemoteBusinessContextLoader(GrpcRemoteLoader({
        'auth': {'host': 'user', 'port': 5553}
    }))
)
weaver = Weaver(registry)


class Product:
    def __init__(self, id: int, name: str, description: str, costPrice: int, sellPrice: int, stockCount: int, hidden: bool):
        self.id = id
        self.name = name
        self.description = description
        self.costPrice = costPrice
        self.sellPrice = sellPrice
        self.stockCount = stockCount
        self.hidden = hidden


class User:
    def __init__(self, id, role):
        self.id = id
        self.role = role


def string_length(string):
    return len(string)


class ProductRepository:
    def __init__(self):
        self.products = {
            1: Product(1, "Rolex", "Beautiful watch for wealthy engineers", None, 50000, 5, False),
            2: Product(2, "Ferrari 458 Italia", "Beautiful car for wealthy engineers", None, 150000, 5, False),
            3: Product(3, "Hamburger", "A meal for the working class", None, 6, 5, False),
            4: Product(4, "A dirty sock", "Very dirty and cheap sock", None, 1, 5, True),
            5: Product(5, "Wardrobe", "Wooden wardrobe for your clothes", None, 300, 1, False),
            6: Product(6, "A chair", "Prime italian leather, it can bend over backwards", None, 399, 0, False),
        }
        self.nextId = 7

    @business_operation("product.listAll", weaver)
    def get_all(self) -> List[Product]:
        return list(self.products.values())

    @business_operation("product.detail", weaver)
    def get(self, id: int) -> Optional[Product]:
        if id not in self.products:
            return None
        return self.products[id]

    @business_operation("product.changePrice", weaver)
    def change_price(self, id, costPrice: int, sellPrice: int, user: User) -> Optional[Product]:
        if id not in self.products:
            return None
        self.products[id].costPrice = int(costPrice)
        self.products[id].sellPrice = int(sellPrice)
        return self.products[id]

    @business_operation("product.changeStock", weaver)
    def change_stock(self, id, stockCount: int, user: User) -> Optional[Product]:
        if id not in self.products:
            return None
        self.products[id].stockCount = stockCount
        return self.products[id]

    @business_operation("product.create", weaver, functions={'length': string_length})
    def create_product(self, name, description, stockCount, user) -> Optional[Product]:
        product = Product(self.nextId, name, description, 0, 0, stockCount, False)
        self.products[self.nextId] = product
        self.nextId += 1
        return product


product_repository = ProductRepository()


def create_product_response(product):
    return {
        'id': product.id,
        'costPrice': product.costPrice,
        'sellPrice': product.sellPrice,
        'stockCount': product.stockCount,
        'name': product.name,
        'description': product.description
    }


@app.route("/", methods=['GET'])
def list_all_products():
    result = []
    for product in product_repository.get_all():
        result.append(create_product_response(product))
    return jsonify(result)


@app.route("/<int:id>", methods=['GET'])
def get_product(id: int):
    product = product_repository.get(id)
    if product is None: abort(404)
    return jsonify(create_product_response(product))


@app.route("/<int:id>/price", methods=['POST'])
def change_price(id: int):
    content = request.get_json()
    try:
        product = product_repository.change_price(
            id,
            int(content['costPrice']),
            int(content['sellPrice']),
            User(request.headers.get('X-User-Id'), request.headers.get('X-User-Role'))
        )
        if product is None: abort(404)
        print("Changed price of product " + str(id))
        return jsonify(create_product_response(product))
    except BusinessRulesCheckFailed as e:
        print("Could not change price of product: " + e.get_message())
        return jsonify({
            'message': e.get_message()
        }), 422


@app.route("/<int:id>/stock", methods=['POST'])
def change_stock(id: int):
    content = request.get_json()
    try:
        product = product_repository.change_stock(
            id,
            int(content['stockCount']),
            User(request.headers.get('X-User-Id'), request.headers.get('X-User-Role'))
        )
        if product is None: abort(404)
        print("Changed stock count of product " + str(id))
        return jsonify(create_product_response(product))
    except BusinessRulesCheckFailed as e:
        print("Could not change stock count of product: " + e.get_message())
        return jsonify({
            'message': e.get_message()
        }), 422


@app.route("/", methods=['POST'])
def create_product():
    content = request.get_json()
    try:
        product = product_repository.create_product(
            content['name'],
            content['description'],
            int(content['stockCount']),
            User(request.headers.get('X-User-Id'), request.headers.get('X-User-Role'))
        )
        if product is None: abort(404)
        print("Created product " + str(product.id))
        return jsonify(create_product_response(product))
    except BusinessRulesCheckFailed as e:
        print("Could not create product: " + e.get_message())
        return jsonify({
            'message': e.get_message()
        }), 422


if __name__ == '__main__':
    server = ServerThread(registry=registry, sleep_interval=60 * 60 * 24, port=5552)
    server.start()
    app.run(host='0.0.0.0', port=5502)
