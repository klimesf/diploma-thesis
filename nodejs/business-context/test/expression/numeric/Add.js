"use strict";

let chai = require('chai');

chai.should();

import Constant from './../../../src/expression/Constant'
import Add from './../../../src/expression/numeric/Add'
import ExpressionType from './../../../src/expression/ExpressionType'
import BusinessOperationContext from './../../../src/weaver/BusinessOperationContext'

describe('Add', () => {
    describe('#interpret', () => {
        it('adds the two numbers', () => {
            const context = new BusinessOperationContext('user.create')
            let expression = new Add(new Constant(1, ExpressionType.NUMBER), new Constant(1, ExpressionType.NUMBER))
            let result = expression.interpret(context)
            result.should.equal(2)
        });
    });
});
