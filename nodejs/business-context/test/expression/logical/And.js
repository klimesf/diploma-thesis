"use strict";

let chai = require('chai');

chai.should();

import Constant from './../../../src/expression/Constant'
import And from './../../../src/expression/logical/And'
import ExpressionType from './../../../src/expression/ExpressionType'
import BusinessOperationContext from './../../../src/weaver/BusinessOperationContext'

describe('And', () => {
    describe('#interpret', () => {
        it('evaluates if both arguments are true', () => {
            const context = new BusinessOperationContext('user.create')
            let expression = new And(new Constant(true, ExpressionType.BOOL), new Constant(true, ExpressionType.BOOL))
            let result = expression.interpret(context)
            result.should.equal(true)

            expression = new And(new Constant(true, ExpressionType.BOOL), new Constant(false, ExpressionType.BOOL))
            result = expression.interpret(context)
            result.should.equal(false)

            expression = new And(new Constant(false, ExpressionType.BOOL), new Constant(true, ExpressionType.BOOL))
            result = expression.interpret(context)
            result.should.equal(false)

            expression = new And(new Constant(false, ExpressionType.BOOL), new Constant(false, ExpressionType.BOOL))
            result = expression.interpret(context)
            result.should.equal(false)
        });
    });
});
