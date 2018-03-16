"use strict";

const PROTO_PATH = __dirname + '/../../../proto/business_context.proto'

const grpc = require('grpc')
const businessContextProto = grpc.load(PROTO_PATH).businessContext

function buildExpression(expression) {
    return {
        name: expression.getName(),
        properties: Object.keys(expression.getProperties()).map(key => {
            return {
                key: key,
                value: expression.getProperties()[key]
            }
        }),
        arguments: expression.getArguments().map(argument => buildExpression(argument))
    }
}

function buildContextMessages(contexts) {
    return contexts.map(context => {
        return {
            prefix: context.identifier.prefix,
            name: context.identifier.name,
            includedContexts: [...context.includedContexts].map(included => included.toString()),
            preconditions: [...context.preconditions].map(precondition => {
                return {
                    name: precondition.name,
                    condition: buildExpression(precondition.condition)
                }
            }),
            postConditions: [...context.postConditions].map(postCondition => {
                return {
                    name: postCondition.name,
                    type: postCondition.type.name,
                    referenceName: postCondition.referenceName,
                    condition: buildExpression(postCondition.condition)
                }
            })
        }
    })
}

function fetchContexts(registry) {
    return function (call, callback) {
        const contexts = registry.getContextsByIdentifiers(call.request.requiredContexts)
        const messages = buildContextMessages(contexts)

        callback(null, {contexts: messages})
    }
}

exports.serve = (port, registry) => {
    const server = new grpc.Server()
    server.addService(businessContextProto.BusinessContextServer.service, {fetchContexts: fetchContexts(registry)})
    server.bind('localhost:' + port, grpc.ServerCredentials.createInsecure())
    server.start()
    console.log("Business context server listening on port " + port)
    return server
}
