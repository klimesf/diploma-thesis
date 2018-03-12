from business_context.business_context_identifier import BusinessContextIdentifier
from business_context.expression import Expression


class BusinessRule:
    name: str
    condition: Expression


class Precondition(BusinessRule):
    def __init__(self, name, condition):
        self.name = name
        self.condition = condition


class PostCondition(BusinessRule):
    reference_name: str

    def __init__(self, name, reference_name, condition):
        self.name = name
        self.referenceName = reference_name
        self.condition = condition


class BusinessContext:
    identifier: BusinessContextIdentifier
    included_contexts: set
    preconditions: set
    post_conditions: set

    def __init__(self, identifier: BusinessContextIdentifier, included_contexts: set,
                 preconditions: set, post_conditions: set):
        self.identifier = identifier
        self.included_contexts = included_contexts
        self.preconditions = preconditions
        self.post_conditions = post_conditions

    def merge(self, other):
        for precondition in other.preconditions:
            self.preconditions.add(precondition)
        for post_condition in other.post_conditions:
            self.post_conditions.add(post_condition)
