"use strict";

import BusinessOperationContext from '../../src/weaver/BusinessOperationContext'
import ObjectPropertyReference from '../../src/expression/ObjectPropertyReference'

let chai = require('chai');
let expect = chai.expect

chai.should();

const BusinessContextWeaver = require('../../src/weaver/BusinessContextWeaver').default
const BusinessContextRegistry = require('../../src/BusinessContextRegistry').default
const BusinessContext = require('../../src/BusinessContext').default
const BusinessContextIdentifier = require('../../src/BusinessContextIdentifier').default
const Precondition = require('../../src/Precondition').default
const PostCondition = require('../../src/PostCondition').default
const PostConditionType = require('../../src/PostConditionType').default
const IsNotNull = require('../../src/expression/IsNotNull').default
const ExpressionType = require('../../src/expression/ExpressionType').default
const VariableReference = require('../../src/expression/VariableReference').default
const Constant = require('../../src/expression/Constant').default
const Equals = require('../../src/expression/logical/Equals').default

const registry = new BusinessContextRegistry(
    {
        load: () => {
            return [
                new BusinessContext(
                    BusinessContextIdentifier.of('user.create'),
                    new Set(),
                    new Set().add(
                        new Precondition('emailNotNull', new IsNotNull(new VariableReference('email', ExpressionType.STRING))),
                    ).add(
                        new Precondition('nameNotNull', new IsNotNull(new VariableReference('name', ExpressionType.STRING))),
                    ),
                    new Set().add(
                        new PostCondition('hideEmail', PostConditionType.FILTER_OBJECT_FIELD, 'email', new Constant(true, ExpressionType.BOOL))
                    )
                ),
                new BusinessContext(
                    BusinessContextIdentifier.of('product.listAll'),
                    new Set(),
                    new Set(),
                    new Set()
                        .add(new PostCondition(
                            'hideHiddenProducts',
                            PostConditionType.FILTER_LIST_OF_OBJECTS,
                            'item',
                            new Equals(new ObjectPropertyReference('item', 'hidden', ExpressionType.BOOL), new Constant(true, ExpressionType.BOOL))
                        ))
                        .add(new PostCondition(
                            'hideBuyingPrice',
                            PostConditionType.FILTER_LIST_OF_OBJECTS_FIELD,
                            'buyingPrice',
                            new Constant(true, ExpressionType.BOOL)
                        ))
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

describe('BusinessContextWeaver', () => {
    describe('#evaluatePreconditions', () => {
        it('evaluates applicable preconditions', () => {
            const weaver = new BusinessContextWeaver(registry)
            let operationContext = new BusinessOperationContext('user.create')
            operationContext.setInputParameter('email', 'john.doe@example.com')
            operationContext.setInputParameter('name', 'John Doe')
            weaver.evaluatePreconditions(operationContext)

            operationContext = new BusinessOperationContext('user.create')
            operationContext.setInputParameter('email', null)
            operationContext.setInputParameter('name', 'John Doe')
            expect(() => weaver.evaluatePreconditions(operationContext)).to.throw()

            operationContext = new BusinessOperationContext('user.create')
            operationContext.setInputParameter('email', 'john.doe@example.com')
            operationContext.setInputParameter('name', null)
            expect(() => weaver.evaluatePreconditions(operationContext)).to.throw()
        });
    });

    describe('#applyPostConditions', () => {
        it('applies applicable post-conditions', () => {
            const weaver = new BusinessContextWeaver(registry)
            let operationContext = new BusinessOperationContext('user.create')
            operationContext.setOutput({'name': 'John Doe', 'email': 'john.doe@example.com'})
            weaver.applyPostConditions(operationContext)
            expect(operationContext.getOutput().email).to.equal(null)

            operationContext = new BusinessOperationContext('product.listAll')
            operationContext.setOutput([
                {name: 'Product 1', hidden: false, buyingPrice: 123.45},
                {name: 'Product 2', hidden: true, buyingPrice: 123.45},
            ])
            weaver.applyPostConditions(operationContext)
            operationContext.getOutput().length.should.equal(1)
            const product = operationContext.getOutput().pop()
            product.name.should.equal('Product 1')
            expect(product.buyingPrice).to.equal(null)
        });
    });
});
