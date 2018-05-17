"use strict"

const BusinessContext = require('business-context/dist/BusinessContext').default
const BusinessContextIdentifier = require('business-context/dist/BusinessContextIdentifier').default
const Precondition = require('business-context/dist/Precondition').default
const PostCondition = require('business-context/dist/PostCondition').default
const PostConditionType = require('business-context/dist/PostConditionType').default
const IsNotNull = require('business-context/dist/expression/IsNotNull').default
const IsNotBlank = require('business-context/dist/expression/IsNotBlank').default
const ExpressionType = require('business-context/dist/expression/ExpressionType').default
const VariableReference = require('business-context/dist/expression/VariableReference').default
const FunctionCall = require('business-context/dist/expression/FunctionCall').default
const ObjectPropertyReference = require('business-context/dist/expression/ObjectPropertyReference').default
const Constant = require('business-context/dist/expression/Constant').default
const LogicalAnd = require('business-context/dist/expression/logical/And').default
const LogicalEquals = require('business-context/dist/expression/logical/Equals').default
const LogicalNegate = require('business-context/dist/expression/logical/Negate').default
const LogicalOr = require('business-context/dist/expression/logical/Or').default
const NumericAdd = require('business-context/dist/expression/numeric/Add').default
const NumericDivide = require('business-context/dist/expression/numeric/Divide').default
const NumericGreaterOrEqualTo = require('business-context/dist/expression/numeric/GreaterOrEqualTo').default
const NumericGreaterThan = require('business-context/dist/expression/numeric/GreaterThan').default
const NumericLessOrEqualTo = require('business-context/dist/expression/numeric/LessOrEqualTo').default
const NumericLessThan = require('business-context/dist/expression/numeric/LessThan').default
const NumericMultiply = require('business-context/dist/expression/numeric/Multiply').default
const NumericSubtract = require('business-context/dist/expression/numeric/Subtract').default

function convertExpressionType(type) {
    switch (type) {
        case ExpressionType.STRING.name:
            return ExpressionType.STRING;
        case ExpressionType.NUMBER.name:
            return ExpressionType.NUMBER;
        case ExpressionType.BOOL.name:
            return ExpressionType.BOOL;
        case ExpressionType.VOID.name:
            return ExpressionType.VOID;
        case ExpressionType.OBJECT.name:
            return ExpressionType.OBJECT;
        default:
            throw "unknown expression type " + type
    }
}

function findPropertyByName(properties, name) {
    const found = properties.find(property => property.key === name)
    if (found) return found.value
    else throw "property " + name + " not found"
}

function buildExpression(message) {
    let type = undefined
    switch (message.name) {
        case 'constant':
            type = convertExpressionType(findPropertyByName(message.properties, 'type'))
            return new Constant(type.deserialize(findPropertyByName(message.properties, 'value')), type)
        case 'is-not-null':
            return new IsNotNull(buildExpression(message['arguments'].pop()))
        case 'is-not-blank':
            return new IsNotBlank(buildExpression(message['arguments'].pop()))
        case 'function-call':
            type = convertExpressionType(findPropertyByName(message.properties, 'type'))
            return new FunctionCall(findPropertyByName(message.properties, 'methodName'), type, message['arguments'].map(arg => buildExpression(arg)))
        case 'object-property-reference':
            type = convertExpressionType(findPropertyByName(message.properties, 'type'))
            return new ObjectPropertyReference(findPropertyByName(message.properties, 'objectName'), findPropertyByName(message.properties, 'propertyName'), type)
        case 'variable-reference':
            type = convertExpressionType(findPropertyByName(message.properties, 'type'))
            return new VariableReference(findPropertyByName(message.properties, 'name'), type)
        case 'logical-and':
            return new LogicalAnd(buildExpression(message['arguments'][0]), buildExpression(message['arguments'][1]))
        case 'logical-equals':
            return new LogicalEquals(buildExpression(message['arguments'][0]), buildExpression(message['arguments'][1]))
        case 'logical-negate':
            return new LogicalNegate(buildExpression(message['arguments'][0]))
        case 'logical-or':
            return new LogicalOr(buildExpression(message['arguments'][0]), buildExpression(message['arguments'][1]))
        case 'numeric-add':
            return new NumericAdd(buildExpression(message['arguments'][0]), buildExpression(message['arguments'][1]))
        case 'numeric-divide':
            return new NumericDivide(buildExpression(message['arguments'][0]), buildExpression(message['arguments'][1]))
        case 'numeric-gte':
            return new NumericGreaterOrEqualTo(buildExpression(message['arguments'][0]), buildExpression(message['arguments'][1]))
        case 'numeric-gt':
            return new NumericGreaterThan(buildExpression(message['arguments'][0]), buildExpression(message['arguments'][1]))
        case 'numeric-lte':
            return new NumericLessOrEqualTo(buildExpression(message['arguments'][0]), buildExpression(message['arguments'][1]))
        case 'numeric-lt':
            return new NumericLessThan(buildExpression(message['arguments'][0]), buildExpression(message['arguments'][1]))
        case 'numeric-multiply':
            return new NumericMultiply(buildExpression(message['arguments'][0]), buildExpression(message['arguments'][1]))
        case 'numeric-subtract':
            return new NumericSubtract(buildExpression(message['arguments'][0]), buildExpression(message['arguments'][1]))
        default:
            throw "unknown expression " + message.name
    }
}

exports.buildContexts = messages => {
    return messages.map(message => {
        return new BusinessContext(
            new BusinessContextIdentifier(message.prefix, message.name),
            new Set(message.includedContexts.map(included => BusinessContextIdentifier.of(included))),
            new Set(message.preconditions.map(precondition => new Precondition(precondition.name, buildExpression(precondition.condition)))),
            new Set(message.postConditions.map(postCondition => new PostCondition(
                postCondition.name,
                PostConditionType[postCondition.type],
                postCondition.referenceName,
                buildExpression(postCondition.condition)
            )))
        )
    })
}

function buildExpressionMessage(expression) {
    return {
        name: expression.getName(),
        properties: Object.keys(expression.getProperties()).map(key => {
            return {
                key: key,
                value: expression.getProperties()[key]
            }
        }),
        arguments: expression.getArguments().map(argument => buildExpressionMessage(argument))
    }
}

exports.buildContextMessages = contexts => {
    return contexts.map(context => {
        return {
            prefix: context.identifier.prefix,
            name: context.identifier.name,
            includedContexts: [...context.includedContexts].map(included => included.toString()),
            preconditions: [...context.preconditions].map(precondition => {
                return {
                    name: precondition.name,
                    condition: buildExpressionMessage(precondition.condition)
                }
            }),
            postConditions: [...context.postConditions].map(postCondition => {
                return {
                    name: postCondition.name,
                    type: postCondition.type.name,
                    referenceName: postCondition.referenceName,
                    condition: buildExpressionMessage(postCondition.condition)
                }
            })
        }
    })
}
