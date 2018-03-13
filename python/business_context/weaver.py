from typing import Set

from business_context.operation_context import OperationContext
from business_context.identifier import Identifier
from business_context.registry import Registry
from business_context.rule import Precondition


class Weaver:
    def __init__(self, registry: Registry):
        self._registry = registry

    def evaluate_preconditions(self, operation_context: OperationContext):
        identifier = Identifier(operation_context.name)
        context = self._registry.get_context_by_identifier(identifier)

        failed = set()
        for precondition in context.preconditions:
            if not precondition.condition.interpret(operation_context):
                failed.add(precondition)

        if len(failed) > 0:
            raise BusinessRulesCheckFailed(failed)

    def apply_post_conditions(self, operation_context: OperationContext):
        identifier = Identifier(operation_context.name)
        context = self._registry.get_context_by_identifier(identifier)
        # TODO: implement
        raise Exception("Not implemented yet exception")
        pass


class BusinessRulesCheckFailed(BaseException):
    def __init__(self, failed: Set[Precondition]):
        self.failed = failed
