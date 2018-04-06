import time
import threading
import grpc
from concurrent import futures
from typing import Set, List, Dict
from business_context.context import BusinessContext
from business_context.expression import Expression
from business_context.identifier import Identifier
from business_context.registry import Registry
from business_context.rule import Precondition, PostCondition, PostConditionType
from business_context_grpc.business_context_pb2 import BusinessContextMessage, PreconditionMessage, PostConditionMessage, ExpressionMessage, PostConditionTypeMessage, ExpressionPropertyMessage
from . import business_context_pb2
from . import business_context_pb2_grpc


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


def convert_post_condition_type(type: PostConditionType) -> PostConditionTypeMessage:
    converter = {
        PostConditionType.FILTER_OBJECT_FIELD: 1,
        PostConditionType.FILTER_LIST_OF_OBJECTS: 2,
        PostConditionType.FILTER_LIST_OF_OBJECTS_FIELD: 3,
    }
    if type not in converter:
        raise Exception('Unknown post condition type')
    return converter[type]
    pass


def build_expression(expression: Expression) -> ExpressionMessage:
    return ExpressionMessage(name=expression.get_name(), properties=build_expression_properties(expression.get_properties()),
                             arguments=build_expression_arguments(expression.get_arguments()))


def build_expression_properties(properties: Dict[str, str]) -> List[ExpressionPropertyMessage]:
    messages = []
    for key, value in properties.items():
        message = ExpressionPropertyMessage(key=key, value=value.__str__())
        messages.append(message)
    return messages


def build_expression_arguments(arguments: List[Expression]) -> List[ExpressionMessage]:
    return list(map(build_expression, arguments))


def build_post_condition(post_condition: PostCondition) -> PostConditionMessage:
    return PostConditionMessage(name=post_condition.name, type=convert_post_condition_type(post_condition.type),
                                referenceName=post_condition.reference_name, condition=build_expression(post_condition.condition))


def build_precondition(precondition: Precondition) -> PreconditionMessage:
    return PreconditionMessage(name=precondition.name, condition=build_expression(precondition.condition))


def build_context_message(context: BusinessContext) -> BusinessContextMessage:
    included_contexts = list(map(Identifier.__str__, context.included_contexts))
    precondition_messages = list(map(build_precondition, context.preconditions))
    post_condition_messages = list(map(build_post_condition, context.post_conditions))
    return BusinessContextMessage(prefix=context.identifier.prefix, name=context.identifier.name, includedContexts=included_contexts,
                                           preconditions=precondition_messages, postConditions=post_condition_messages)


class Server(business_context_pb2_grpc.BusinessContextServerServicer):
    def __init__(self, registry: Registry):
        self._registry = registry

    def FetchContexts(self, request, context):
        identifiers = set(map(Identifier, request.requiredContexts))
        contexts = self._registry.get_contexts_by_identifiers(identifiers)
        return business_context_pb2.BusinessContextsResponseMessage(contexts=list(map(build_context_message, contexts)))

    def FetchAllContexts(self, request, context):
        contexts = self._registry.get_all_contexts()
        return business_context_pb2.BusinessContextsResponseMessage(contexts=list(map(build_context_message, contexts)))
