"use strict";

let chai = require('chai');

chai.should();

import Constant from './../../../src/expression/Constant'
import Or from './../../../src/expression/logical/Or'
import ExpressionType from './../../../src/expression/ExpressionType'
import BusinessOperationContext from './../../../src/weaver/BusinessOperationContext'

describe('Or', () => {
    describe('#interpret', () => {
        it('evaluates if both arguments are true', () => {
            const context = new BusinessOperationContext('user.create')
            let expression = new Or(new Constant(true, ExpressionType.BOOL), new Constant(true, ExpressionType.BOOL))
            let result = expression.interpret(context)
            result.should.equal(true)

            expression = new Or(new Constant(true, ExpressionType.BOOL), new Constant(false, ExpressionType.BOOL))
            result = expression.interpret(context)
            result.should.equal(true)

            expression = new Or(new Constant(false, ExpressionType.BOOL), new Constant(true, ExpressionType.BOOL))
            result = expression.interpret(context)
            result.should.equal(true)

            expression = new Or(new Constant(false, ExpressionType.BOOL), new Constant(false, ExpressionType.BOOL))
            result = expression.interpret(context)
            result.should.equal(false)
        });
    });
});
