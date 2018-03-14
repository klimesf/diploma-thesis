"use strict";

let chai = require('chai');

chai.should();

import VariableReference from './../../src/expression/VariableReference'
import ExpressionType from './../../src/expression/ExpressionType'
import BusinessOperationContext from './../../src/weaver/BusinessOperationContext'

describe('VariableReference', () => {
    describe('#interpret', () => {
        it('returns the value of the variable', () => {
            const context = new BusinessOperationContext('user.create')
            context.setInputParameter('name', 'John Doe')
            let expression = new VariableReference('name', ExpressionType.STRING)
            let result = expression.interpret(context)
            result.should.equal('John Doe')
        });
    });
});
