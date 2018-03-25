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
    businessContextPort = process.env.BUSINESS_CONTEXT_PORT || 5553,
    server = require('business-context-grpc/dist/server')

exports.setUp = () => {
    const registry = exports.businessContextRegistry = new BusinessContextRegistry(
        {
            load: () => {
                return [
                    new BusinessContext(
                        BusinessContextIdentifier.of('auth.adminLoggedIn'),
                        new Set(),
                        new Set()
                            .add(new Precondition('Signed user must be an administrator', new Equals(
                                new ObjectPropertyReference('user', 'role', ExpressionType.STRING),
                                new Constant("ADMINISTRATOR", ExpressionType.STRING)
                            ))),
                        new Set()
                    ),
                    new BusinessContext(
                        BusinessContextIdentifier.of('auth.employeeLoggedIn'),
                        new Set(),
                        new Set()
                            .add(new Precondition('Signed user must be an employee', new Equals(
                                new ObjectPropertyReference('user', 'role', ExpressionType.STRING),
                                new Constant("EMPLOYEE", ExpressionType.STRING)
                            ))),
                        new Set()
                    ),
                    new BusinessContext(
                        BusinessContextIdentifier.of('auth.userLoggedIn'),
                        new Set(),
                        new Set()
                            .add(new Precondition('User must be signed in', new IsNotNull(new VariableReference('user', ExpressionType.OBJECT))),),
                        new Set()
                    ),
                    new BusinessContext(
                        BusinessContextIdentifier.of('user.validEmail'),
                        new Set(),
                        new Set()
                            .add(new Precondition('Email must not be null', new IsNotNull(new VariableReference('email', ExpressionType.STRING))),),
                        new Set()
                    ),
                    new BusinessContext(
                        BusinessContextIdentifier.of('user.delete'),
                        new Set()
                            .add(BusinessContextIdentifier.of('auth.adminLoggedIn')),
                        new Set(),
                        new Set()
                    ),
                    new BusinessContext(
                        BusinessContextIdentifier.of('user.createEmployee'),
                        new Set()
                            .add(BusinessContextIdentifier.of('auth.adminLoggedIn'))
                            .add(BusinessContextIdentifier.of('user.validEmail')),
                        new Set(),
                        new Set()
                    ),
                    new BusinessContext(
                        BusinessContextIdentifier.of('user.register'),
                        new Set().add(BusinessContextIdentifier.of('user.validEmail')),
                        new Set(),
                        new Set()
                    ),
                    new BusinessContext(
                        BusinessContextIdentifier.of('user.listAll'),
                        new Set(),
                        new Set(),
                        new Set()
                    ),
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
