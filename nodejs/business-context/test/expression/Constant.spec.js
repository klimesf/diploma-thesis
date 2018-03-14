"use strict";

let chai = require('chai')
    , path = require('path');

chai.should();

import Constant from './../../src/expression/Constant'
import ExpressionType from './../../src/expression/ExpressionType'
import BusinessOperationContext from './../../src/weaver/BusinessOperationContext'

describe('Constant', () => {
    describe('#interpret', () => {
        it('returns the value of the Constant', () => {
            const constant = new Constant(true, ExpressionType.BOOL)
            const context = new BusinessOperationContext('user.create')
            const result = constant.interpret(context)
            result.should.equal(true)
        });
    });
});
