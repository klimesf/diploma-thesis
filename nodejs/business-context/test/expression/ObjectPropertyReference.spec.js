"use strict";

let chai = require('chai');

chai.should();

import ObjectPropertyReference from './../../src/expression/ObjectPropertyReference'
import ExpressionType from './../../src/expression/ExpressionType'
import BusinessOperationContext from './../../src/weaver/BusinessOperationContext'

describe('ObjectPropertyReference', () => {
    describe('#interpret', () => {
        it('returns the value of the object property', () => {
            const context = new BusinessOperationContext('user.create')
            context.setInputParameter('user', {name: 'John Doe', email: 'john.doe@example.com'})
            let expression = new ObjectPropertyReference('user', 'name', ExpressionType.STRING)
            let result = expression.interpret(context)
            result.should.equal('John Doe')
        });
    });
});
