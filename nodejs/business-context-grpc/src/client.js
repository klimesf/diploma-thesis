"use strict";

const PROTO_PATH = __dirname + '/../../../proto/business_context.proto'

const BusinessContext = require('../../business-context/src/BusinessContext').default
const BusinessContextIdentifier = require('../../business-context/src/BusinessContextIdentifier').default
const Precondition = require('../../business-context/src/Precondition').default
const PostCondition = require('../../business-context/src/PostCondition').default
const PostConditionType = require('../../business-context/src/PostConditionType').default
const IsNotNull = require('../../business-context/src/expression/IsNotNull').default
const ExpressionType = require('../../business-context/src/expression/ExpressionType').default
const VariableReference = require('../../business-context/src/expression/VariableReference').default
const FunctionCall = require('../../business-context/src/expression/FunctionCall').default
const ObjectPropertyReference = require('../../business-context/src/expression/ObjectPropertyReference').default
const Constant = require('../../business-context/src/expression/Constant').default
const LogicalAnd = require('../../business-context/src/expression/logical/And').default
const LogicalEquals = require('../../business-context/src/expression/logical/Equals').default
const LogicalNegate = require('../../business-context/src/expression/logical/Negate').default
const LogicalOr = require('../../business-context/src/expression/logical/Or').default

const grpc = require('grpc')
const grpcPromise = require('grpc-promise');
const businessContextProto = grpc.load(PROTO_PATH).businessContext

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
        default:
            throw "unknown expression " + message.name
    }
}

function buildContexts(messages) {
    return messages.map(message => {
        return new BusinessContext(
            new BusinessContextIdentifier(message.prefix, message.name),
            message.includedContexts,
            message.preconditions.map(precondition => new Precondition(precondition.name, buildExpression(precondition.condition))),
            message.postConditions.map(postCondition => new PostCondition(
                postCondition.name,
                PostConditionType[postCondition.type],
                postCondition.referenceName,
                buildExpression(postCondition.condition)
            ))
        )
    })
}

exports.fetchContexts = async (identifiers, host, port) => {
    const client = new businessContextProto.BusinessContextServer(host + ':' + port, grpc.credentials.createInsecure())
    grpcPromise.promisifyAll(client)
    return client.fetchContexts()
        .sendMessage({requiredContexts: identifiers})
        .then(response => buildContexts(response.contexts))
        .catch(err => console.error(err))
}

exports.fetchAllContexts = async (host, port) => {
    const client = new businessContextProto.BusinessContextServer(host + ':' + port, grpc.credentials.createInsecure())
    grpcPromise.promisifyAll(client)
    return client.fetchAllContexts()
        .sendMessage({})
        .then(response => buildContexts(response.contexts))
        .catch(err => console.error(err))
}
