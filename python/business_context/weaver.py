from typing import Set

from business_context.operation_context import OperationContext
from business_context.identifier import Identifier
from business_context.registry import Registry
from business_context.rule import Precondition, PostCondition, PostConditionType


class Weaver:
    def __init__(self, registry: Registry):
        self._registry = registry

    def evaluate_preconditions(self, operation_context: OperationContext):
        identifier = Identifier(operation_context.name)
        self._registry.wait_for_transaction()
        context = self._registry.get_context_by_identifier(identifier)

        failed = set()
        for precondition in context.preconditions:
            if not precondition.condition.interpret(operation_context):
                failed.add(precondition)

        if len(failed) > 0:
            raise BusinessRulesCheckFailed(failed)

    def apply_post_conditions(self, operation_context: OperationContext):
        identifier = Identifier(operation_context.name)
        self._registry.wait_for_transaction()
        context = self._registry.get_context_by_identifier(identifier)
        for post_condition in context.post_conditions:
            self._apply_post_condition(post_condition, operation_context)

    def _apply_post_condition(self, post_condition: PostCondition, operation_context: OperationContext):
        matcher = {
            PostConditionType.FILTER_OBJECT_FIELD: self._filter_object_field,
            PostConditionType.FILTER_LIST_OF_OBJECTS: self._filter_list_of_objects,
            PostConditionType.FILTER_LIST_OF_OBJECTS_FIELD: self._filter_list_of_objects_field,
        }
        if post_condition.type not in matcher:
            raise Exception('Unknown PostConditionType ' + post_condition.type)
        matcher[post_condition.type](post_condition, operation_context)

    def _filter_object_field(self, post_condition: PostCondition, operation_context: OperationContext):
        if post_condition.condition.interpret(operation_context):
            output = operation_context.get_output()
            if output is not None:
                setattr(output, post_condition.reference_name, None)

    def _filter_list_of_objects(self, post_condition: PostCondition, operation_context: OperationContext):
        result = []
        for item in operation_context.get_output():
            operation_context.set_input_parameter('item', item)
            if not post_condition.condition.interpret(operation_context):
                result.append(item)
        operation_context.set_output(result)

    def _filter_list_of_objects_field(self, post_condition: PostCondition, operation_context: OperationContext):
        for item in operation_context.get_output():
            operation_context.set_input_parameter('item', item)
            if post_condition.condition.interpret(operation_context):
                setattr(item, post_condition.reference_name, None)


class BusinessRulesCheckFailed(BaseException):
    def __init__(self, failed: Set[Precondition]):
        self.failed = failed

    def get_message(self):
        names = []
        for fail in self.failed:
            names.append(fail.name)
        return "; ".join(names)
