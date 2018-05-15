from flask import Flask, jsonify, abort
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
        'business-contexts/create.xml',
        'business-contexts/detail.xml',
        'business-contexts/hidden.xml',
        'business-contexts/listAll.xml',
        'business-contexts/stock.xml',
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

    @business_operation("product.listAll", weaver)
    def get_all(self) -> List[Product]:
        return list(self.products.values())

    @business_operation("product.detail", weaver)
    def get(self, id: int) -> Optional[Product]:
        if id not in self.products:
            return None
        return self.products[id]


product_repository = ProductRepository()


@app.route("/")
def list_all_products():
    result = []
    for product in product_repository.get_all():
        result.append({
            'id': product.id,
            'sellPrice': product.sellPrice,
            'name': product.name,
            'description': product.description
        })
    return jsonify(result)


@app.route("/<int:id>")
def get_product(id: int):
    product = product_repository.get(id)
    if product is None: abort(404)
    return jsonify({
        'id': product.id,
        'sellPrice': product.sellPrice,
        'name': product.name,
        'description': product.description
    })


if __name__ == '__main__':
    server = ServerThread(registry=registry, sleep_interval=60 * 60 * 24, port=5552)
    server.start()
    app.run(host='0.0.0.0', port=5502)
