"use strict";

let chai = require('chai');

chai.should();

import Constant from './../../../src/expression/Constant'
import Equals from './../../../src/expression/logical/Equals'
import ExpressionType from './../../../src/expression/ExpressionType'
import BusinessOperationContext from './../../../src/weaver/BusinessOperationContext'

describe('Equals', () => {
    describe('#interpret', () => {
        it('evaluates if both arguments are true', () => {
            const context = new BusinessOperationContext('user.create')
            let expression = new Equals(new Constant(true, ExpressionType.BOOL), new Constant(true, ExpressionType.BOOL))
            let result = expression.interpret(context)
            result.should.equal(true)

            expression = new Equals(new Constant(true, ExpressionType.BOOL), new Constant(false, ExpressionType.BOOL))
            result = expression.interpret(context)
            result.should.equal(false)

            expression = new Equals(new Constant(false, ExpressionType.BOOL), new Constant(true, ExpressionType.BOOL))
            result = expression.interpret(context)
            result.should.equal(false)

            expression = new Equals(new Constant(false, ExpressionType.BOOL), new Constant(false, ExpressionType.BOOL))
            result = expression.interpret(context)
            result.should.equal(true)
        });
    });
});
