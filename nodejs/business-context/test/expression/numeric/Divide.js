"use strict";

let chai = require('chai');

chai.should();

import Constant from './../../../src/expression/Constant'
import Divide from './../../../src/expression/numeric/Divide'
import ExpressionType from './../../../src/expression/ExpressionType'
import BusinessOperationContext from './../../../src/weaver/BusinessOperationContext'

describe('Divide', () => {
    describe('#interpret', () => {
        it('divides the two numbers', () => {
            const context = new BusinessOperationContext('user.create')
            let expression = new Divide(new Constant(6, ExpressionType.NUMBER), new Constant(3, ExpressionType.NUMBER))
            let result = expression.interpret(context)
            result.should.equal(2)
        });
    });
});
