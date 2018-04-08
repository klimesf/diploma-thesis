"use strict";

const PROTO_PATH = __dirname + '/../../proto/business_context.proto'

const grpc = require('grpc')
const businessContextProto = grpc.load(PROTO_PATH).businessContext
const buildContexts = require('./helpers').buildContexts
const buildContextMessages = require('./helpers').buildContextMessages

function fetchContexts(registry) {
    return function (call, callback) {
        const contexts = registry.getContextsByIdentifiers(call.request.requiredContexts)
        const messages = buildContextMessages(contexts)

        callback(null, {contexts: messages})
    }
}

function fetchAllContexts(registry) {
    return function (call, callback) {
        const contexts = registry.getAllContexts()
        console.log(contexts)
        const messages = buildContextMessages(contexts)

        callback(null, {contexts: messages})
    }
}

function updateContext(registry) {
    return function (call, callback) {
        const contexts = registry.updateContext(buildContexts([call.request.context]).pop())
        callback(null, {})
    }
}

exports.serve = (port, registry) => {
    const server = new grpc.Server()
    server.addService(
        businessContextProto.BusinessContextServer.service,
        {
            fetchContexts: fetchContexts(registry),
            fetchAllContexts: fetchAllContexts(registry),
            updateContext: updateContext(registry)
        }
    )
    server.bind('0.0.0.0:' + port, grpc.ServerCredentials.createInsecure())
    server.start()
    console.log("Business context server listening on port " + port)
    return server
}
