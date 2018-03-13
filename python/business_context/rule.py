from enum import Enum

from business_context.expression import Expression


class Precondition:
    def __init__(self, name: str, condition: Expression):
        self.name = name
        self.condition = condition


class PostConditionType(Enum):
    FILTER_OBJECT_FIELD = 1
    FILTER_LIST_OF_OBJECTS = 2
    FILTER_LIST_OF_OBJECTS_FIELD = 3


class PostCondition:
    def __init__(self, name: str, type: PostConditionType, reference_name: str, condition: Expression):
        self.name = name
        self.type = type
        self.reference_name = reference_name
        self.condition = condition
