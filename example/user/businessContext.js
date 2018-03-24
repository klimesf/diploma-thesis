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
    ExpressionType = require('business-context-framework/dist/expression/ExpressionType').default,
    businessContextPort = process.env.BUSINESS_CONTEXT_PORT || 5553,
    server = require('business-context-grpc/dist/server')

exports.setUp = () => {
    const registry = exports.businessContextRegistry = new BusinessContextRegistry(
        {
            load: () => {
                return [
                    new BusinessContext(
                        BusinessContextIdentifier.of('user.validEmail'),
                        new Set(),
                        new Set().add(
                            new Precondition('emailNotNull', new IsNotNull(new VariableReference('email', ExpressionType.STRING))),
                        ),
                        new Set()
                    )
                ]
            }
        },
        {
            loadContextsByIdentifier: (identifiers) => {
                return {}
            }
        }
    )
    exports.server = server.serve(businessContextPort, registry)
}
