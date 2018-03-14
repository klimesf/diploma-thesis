"use strict";

let chai = require('chai');

chai.should();

import Constant from './../../src/expression/Constant'
import IsNotNull from './../../src/expression/IsNotNull'
import ExpressionType from './../../src/expression/ExpressionType'
import BusinessOperationContext from './../../src/weaver/BusinessOperationContext'

describe('IsNotNull', () => {
    describe('#interpret', () => {
        it('evaluates if the argument is null', () => {
            const context = new BusinessOperationContext('user.create')
            let expression = new IsNotNull(new Constant(true, ExpressionType.BOOL))
            let result = expression.interpret(context)
            result.should.equal(true)

            expression = new IsNotNull(new Constant(null, ExpressionType.VOID))
            result = expression.interpret(context)
            result.should.equal(false)
        });
    });
});
