"use strict";

let chai = require('chai');

chai.should();

import Constant from './../../../src/expression/Constant'
import Multiply from './../../../src/expression/numeric/Multiply'
import ExpressionType from './../../../src/expression/ExpressionType'
import BusinessOperationContext from './../../../src/weaver/BusinessOperationContext'

describe('Multiply', () => {
    describe('#interpret', () => {
        it('multiplies the two numbers', () => {
            const context = new BusinessOperationContext('user.create')
            let expression = new Multiply(new Constant(3, ExpressionType.NUMBER), new Constant(2, ExpressionType.NUMBER))
            let result = expression.interpret(context)
            result.should.equal(6)
        });
    });
});
