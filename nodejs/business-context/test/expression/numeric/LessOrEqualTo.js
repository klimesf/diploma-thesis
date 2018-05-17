"use strict";

let chai = require('chai');

chai.should();

import Constant from './../../../src/expression/Constant'
import LessOrEqualTo from './../../../src/expression/numeric/LessOrEqualTo'
import ExpressionType from './../../../src/expression/ExpressionType'
import BusinessOperationContext from './../../../src/weaver/BusinessOperationContext'

describe('LessOrEqualTo', () => {
    describe('#interpret', () => {
        it('compares the two numbers', () => {
            const context = new BusinessOperationContext('user.create')

            let expression = new LessOrEqualTo(new Constant(6, ExpressionType.NUMBER), new Constant(3, ExpressionType.NUMBER))
            let result = expression.interpret(context)
            result.should.equal(false)

            expression = new LessOrEqualTo(new Constant(6, ExpressionType.NUMBER), new Constant(6, ExpressionType.NUMBER))
            result = expression.interpret(context)
            result.should.equal(true)

            expression = new LessOrEqualTo(new Constant(3, ExpressionType.NUMBER), new Constant(6, ExpressionType.NUMBER))
            result = expression.interpret(context)
            result.should.equal(true)
        });
    });
});
