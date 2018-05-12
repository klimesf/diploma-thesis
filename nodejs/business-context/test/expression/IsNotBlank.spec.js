"use strict";

let chai = require('chai');

chai.should();

import Constant from './../../src/expression/Constant'
import IsNotBlank from './../../src/expression/IsNotBlank'
import ExpressionType from './../../src/expression/ExpressionType'
import BusinessOperationContext from './../../src/weaver/BusinessOperationContext'

describe('IsNotBlank', () => {
    describe('#interpret', () => {
        it('evaluates if the argument is not a blank string', () => {
            const context = new BusinessOperationContext('user.create')
            let expression = new IsNotBlank(new Constant("Hello", ExpressionType.STRING))
            let result = expression.interpret(context)
            result.should.equal(true)

            expression = new IsNotBlank(new Constant("", ExpressionType.STRING))
            result = expression.interpret(context)
            result.should.equal(false)

            expression = new IsNotBlank(new Constant(null, ExpressionType.STRING))
            result = expression.interpret(context)
            result.should.equal(false)
        });
    });
});
