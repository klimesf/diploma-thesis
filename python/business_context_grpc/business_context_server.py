import time
import threading
import grpc
from concurrent import futures
from typing import Set, List
from business_context.context import BusinessContext
from business_context.expression import Expression
from business_context.identifier import Identifier
from business_context.registry import Registry
from business_context.rule import Precondition, PostCondition
from business_context_grpc.business_context_pb2 import BusinessContextMessage, PreconditionMessage, PostConditionMessage, ExpressionMessage
from . import business_context_pb2
from . import business_context_pb2_grpc


class ServerThread(threading.Thread):
    _port: int
    _registry: Registry
    _run: bool
    _sleep_interval: int

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
        server.add_insecure_port('[::]:' + self._port.__str__())
        print('BusinessContextServer listening on port ' + self._port.__str__())
        server.start()
        try:
            while self._run:
                time.sleep(self._sleep_interval)
        except (KeyboardInterrupt, SystemExit):
            server.stop(0)
        server.stop(0)


class Server(business_context_pb2_grpc.BusinessContextServerServicer):
    _registry: Registry

    def __init__(self, registry: Registry):
        self._registry = registry

    def FetchContexts(self, request, context):
        identifiers = set()
        for required in request.requiredContexts:
            identifiers.add(Identifier(required))

        contexts = self._registry.get_contexts_by_identifiers(identifiers)
        return business_context_pb2.BusinessContextsResponseMessage(contexts=self.build_context_messages(contexts))

    def build_context_messages(self, contexts: Set[BusinessContext]) -> List[BusinessContextMessage]:
        messages = []
        for context in contexts:
            included_contexts = []
            for included in context.included_contexts:
                included_contexts.append(included.__str__())
            precondition_messages = []
            # for precondition in context.preconditions:
            #     precondition_messages.append(self.build_precondition(precondition))
            post_condition_messages = []
            # for post_condition in context.post_conditions:
            #     post_condition_messages.append(self.build_post_condition(post_condition))
            messages.append(BusinessContextMessage(prefix=context.identifier.prefix, name=context.identifier.name, includedContexts=included_contexts,
                                                   preconditions=precondition_messages, postConditions=post_condition_messages))
        return messages

    def build_precondition(self, precondition: Precondition) -> PreconditionMessage:
        # TODO: implement
        return PreconditionMessage()

    def build_post_condition(self, post_condition: PostCondition) -> PostConditionMessage:
        # TODO: implement
        return PreconditionMessage()

    def build_expression(self, expression: Expression) -> ExpressionMessage:
        # TODO: implement
        return ExpressionMessage()
