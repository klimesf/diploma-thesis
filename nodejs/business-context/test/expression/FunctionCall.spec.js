"use strict";

import Constant from '../../src/expression/Constant'

let chai = require('chai');

chai.should();

import FunctionCall from './../../src/expression/FunctionCall'
import ExpressionType from './../../src/expression/ExpressionType'
import BusinessOperationContext from './../../src/weaver/BusinessOperationContext'

describe('FunctionCall', () => {
    describe('#interpret', () => {
        it('returns result of the function call', () => {
            const context = new BusinessOperationContext('user.create')
            context.setFunction('sum', (a, b) => a + b)

            let expression = new FunctionCall(
                'sum',
                ExpressionType.NUMBER,
                [
                    new Constant(1, ExpressionType.NUMBER),
                    new Constant(2, ExpressionType.NUMBER)
                ]
            )
            let result = expression.interpret(context)
            result.should.equal(3)
        });
    });
});
