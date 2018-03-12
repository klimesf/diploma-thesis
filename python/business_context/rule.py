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
