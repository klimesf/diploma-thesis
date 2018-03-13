from typing import Set
from business_context.identifier import Identifier
from business_context.rule import Precondition, PostCondition


class BusinessContext:
    def __init__(self, identifier: Identifier, included_contexts: Set[Identifier],
                 preconditions: Set[Precondition], post_conditions: Set[PostCondition]):
        self.identifier = identifier
        self.included_contexts = included_contexts
        self.preconditions = preconditions
        self.post_conditions = post_conditions

    def merge(self, other):
        for precondition in other.preconditions:
            self.preconditions.add(precondition)
        for post_condition in other.post_conditions:
            self.post_conditions.add(post_condition)
