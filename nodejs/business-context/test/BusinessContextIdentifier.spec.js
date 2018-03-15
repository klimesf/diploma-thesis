"use strict";

let chai = require('chai');
let expect = chai.expect

chai.should();

import BusinessContextIdentifier from './../src/BusinessContextIdentifier'

describe('BusinessContextIdentifier', () => {
    describe('#constructor', () => {
        it('accepts valid name', () => {
            const identifier = new BusinessContextIdentifier('user', 'create')
            identifier.prefix.should.equal('user')
            identifier.name.should.equal('create')
        });
        it('does not accept invalid name', () => {
            expect(() => new BusinessContextIdentifier('user.123', 'create_')).to.throw()
        });
    });
    describe('#toString', () => {
        it('returns prefixed name', () => {
            const identifier = new BusinessContextIdentifier('user', 'create')
            identifier.toString().should.equal('user.create')
        });
    });
    describe('#of', () => {
        it('accepts valid prefixed name', () => {
            const identifier = BusinessContextIdentifier.of('user.create')
            identifier.prefix.should.equal('user')
            identifier.name.should.equal('create')
        });
        it('does not accept invalid name', () => {
            expect(() => BusinessContextIdentifier.of('user.a123.')).to.throw()
            expect(() => BusinessContextIdentifier.of('user.')).to.throw()
            expect(() => BusinessContextIdentifier.of('.')).to.throw()
        });
    });
});
