from concurrent import futures
import time

import grpc

from . import business_context_pb2
from . import business_context_pb2_grpc

_ONE_DAY_IN_SECONDS = 60 * 60 * 24


class Greeter(business_context_pb2_grpc.BusinessContextServerServicer):

    def SayHello(self, request, context):
        # TODO: implement response creation
        return business_context_pb2.BusinessContextsResponseMessage()


def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    business_context_pb2_grpc.add_BusinessContextServerServicer_to_server(Greeter(), server)
    server.add_insecure_port('[::]:5555')
    server.start()
    try:
        while True:
            time.sleep(_ONE_DAY_IN_SECONDS)
    except KeyboardInterrupt:
        server.stop(0)


if __name__ == '__main__':
    serve()
