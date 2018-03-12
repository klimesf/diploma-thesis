from enum import Enum

from business_context.expression import Expression


class BusinessRule:
    name: str
    condition: Expression


class Precondition(BusinessRule):
    def __init__(self, name, condition):
        self.name = name
        self.condition = condition


class PostConditionType(Enum):
    FILTER_OBJECT_FIELD = 1
    FILTER_LIST_OF_OBJECTS = 2
    FILTER_LIST_OF_OBJECTS_FIELD = 3


class PostCondition(BusinessRule):
    reference_name: str
    type: PostConditionType

    def __init__(self, name, type, reference_name, condition):
        self.name = name
        self.type = type
        self.referenceName = reference_name
        self.condition = condition
