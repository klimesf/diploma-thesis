from business_context.business_context_identifier import BusinessContextIdentifier
from business_context.expression import Expression


class BusinessRulePrecondition:
    name: str
    condition: Expression


class BusinessRulePostCondition:
    name: str
    condition: Expression


class BusinessContext:
    identifier: BusinessContextIdentifier
    preconditions: [BusinessRulePrecondition]
    post_conditions: [BusinessRulePostCondition]
