"use strict";

let chai = require('chai');

chai.should();

import Constant from './../../../src/expression/Constant'
import Subtract from './../../../src/expression/numeric/Subtract'
import ExpressionType from './../../../src/expression/ExpressionType'
import BusinessOperationContext from './../../../src/weaver/BusinessOperationContext'

describe('Subtract', () => {
    describe('#interpret', () => {
        it('subtracts the two numbers', () => {
            const context = new BusinessOperationContext('user.create')
            let expression = new Subtract(new Constant(6, ExpressionType.NUMBER), new Constant(2, ExpressionType.NUMBER))
            let result = expression.interpret(context)
            result.should.equal(4)
        });
    });
});
