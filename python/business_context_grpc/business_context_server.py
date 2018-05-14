import time
import threading
import grpc
from concurrent import futures
from business_context.identifier import Identifier
from business_context.registry import Registry
from . import business_context_pb2
from . import business_context_pb2_grpc
from . import helpers


class ServerThread(threading.Thread):
    def __init__(self, registry: Registry, sleep_interval: int, port: int):
        self._port = port
        self._registry = registry
        threading.Thread.__init__(self)
        self.daemon = True
        self._stop_event = threading.Event()
        self._run = True
        self._sleep_interval = sleep_interval

    def run(self):
        self.serve()

    def stop(self):
        print('Stopping server')
        self._run = False
        self._stop_event.set()

    def serve(self):
        server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
        business_context_pb2_grpc.add_BusinessContextServerServicer_to_server(Server(self._registry), server)
        server.add_insecure_port('0.0.0.0:' + self._port.__str__())
        print('BusinessContextServer listening on port ' + self._port.__str__())
        server.start()
        try:
            while self._run:
                time.sleep(self._sleep_interval)
        except (KeyboardInterrupt, SystemExit):
            server.stop(0)
        server.stop(0)


class Server(business_context_pb2_grpc.BusinessContextServerServicer):
    def __init__(self, registry: Registry):
        self._registry = registry

    def FetchContexts(self, request, context):
        identifiers = set(map(Identifier, request.requiredContexts))
        contexts = self._registry.get_contexts_by_identifiers(identifiers)
        return business_context_pb2.BusinessContextsResponseMessage(contexts=list(map(helpers.build_context_message, contexts)))

    def FetchAllContexts(self, request, context):
        contexts = self._registry.get_all_contexts()
        return business_context_pb2.BusinessContextsResponseMessage(contexts=list(map(helpers.build_context_message, contexts)))

    def UpdateContext(self, request, context):
        self._registry.save_or_update_context(helpers.build_context(request.context))
        return business_context_pb2.Empty()

    def BeginTransaction(self, request, context):
        self._registry.begin_transaction()
        return business_context_pb2.Empty()

    def CommitTransaction(self, request, context):
        self._registry.commit_transaction()
        return business_context_pb2.Empty()

    def RollbackTransaction(self, request, context):
        self._registry.rollback_transaction()
        return business_context_pb2.Empty()
