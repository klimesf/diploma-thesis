'use strict'

const BusinessContextRegistry = require('business-context-framework/dist/BusinessContextRegistry').default,
    BusinessContext = require('business-context-framework/dist/BusinessContext').default,
    BusinessContextIdentifier = require('business-context-framework/dist/BusinessContextIdentifier').default,
    Precondition = require('business-context-framework/dist/Precondition').default,
    PostCondition = require('business-context-framework/dist/PostCondition').default,
    PostConditionType = require('business-context-framework/dist/PostConditionType').default,
    Constant = require('business-context-framework/dist/expression/Constant').default,
    IsNotNull = require('business-context-framework/dist/expression/IsNotNull').default,
    VariableReference = require('business-context-framework/dist/expression/VariableReference').default,
    ObjectPropertyReference = require('business-context-framework/dist/expression/ObjectPropertyReference').default,
    ExpressionType = require('business-context-framework/dist/expression/ExpressionType').default,
    Equals = require('business-context-framework/dist/expression/logical/Equals').default,
    Or = require('business-context-framework/dist/expression/logical/Or').default,
    businessContextPort = process.env.BUSINESS_CONTEXT_PORT || 5553,
    server = require('business-context-grpc/dist/server'),
    XmlReader = require('business-context-xml/dist/XmlReader').default,
    path = require('path')

exports.setUp = () => {
    const registry = exports.businessContextRegistry = new BusinessContextRegistry(
        new XmlReader([
            path.join(__dirname, '..', 'business-contexts', 'auth', 'adminLoggedIn.xml'),
            path.join(__dirname, '..', 'business-contexts', 'auth', 'employeeLoggedIn.xml'),
            path.join(__dirname, '..', 'business-contexts', 'auth', 'userLoggedIn.xml'),
            path.join(__dirname, '..', 'business-contexts', 'user', 'createEmployee.xml'),
            path.join(__dirname, '..', 'business-contexts', 'user', 'delete.xml'),
            path.join(__dirname, '..', 'business-contexts', 'user', 'listAll.xml'),
            path.join(__dirname, '..', 'business-contexts', 'user', 'register.xml'),
            path.join(__dirname, '..', 'business-contexts', 'user', 'validEmail.xml'),
        ]),
        {
            loadContextsByIdentifier: (identifiers) => {
                return {}
            }
        }
    )
    exports.server = server.serve(businessContextPort, registry)
}
