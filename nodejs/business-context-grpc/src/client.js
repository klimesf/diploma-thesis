"use strict";

const PROTO_PATH = __dirname + '/../../proto/business_context.proto'

const buildContexts = require('./helpers').buildContexts
const buildContextMessages = require('./helpers').buildContextMessages

const grpc = require('grpc')
const grpcPromise = require('grpc-promise');
const businessContextProto = grpc.load(PROTO_PATH).businessContext

exports.fetchContexts = (identifiers, host, port) => {
    const client = new businessContextProto.BusinessContextServer(host + ':' + port, grpc.credentials.createInsecure())
    grpcPromise.promisifyAll(client)
    return client.fetchContexts()
        .sendMessage({requiredContexts: identifiers})
        .then(response => buildContexts(response.contexts))
        .catch(err => console.error(err))
}

exports.fetchAllContexts = (host, port) => {
    const client = new businessContextProto.BusinessContextServer(host + ':' + port, grpc.credentials.createInsecure())
    grpcPromise.promisifyAll(client)
    return client.fetchAllContexts()
        .sendMessage({})
        .then(response => buildContexts(response.contexts))
        .catch(err => console.error(err))
}

exports.updateContext = (context, host, port) => {
    const client = new businessContextProto.BusinessContextServer(host + ':' + port, grpc.credentials.createInsecure())
    grpcPromise.promisifyAll(client)
    return client.updateContext()
        .sendMessage({context: buildContextMessages([context]).pop()})
        .then(_ => console.log("Context updated"))
        .catch(err => console.error(err))
}
