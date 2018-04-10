"use strict";

import IsNotNull from '../src/expression/IsNotNull'

let chai = require('chai');

chai.should();

import BusinessContext from './../src/BusinessContext'
import BusinessContextIdentifier from './../src/BusinessContextIdentifier'
import PostCondition from './../src/PostCondition'
import PostConditionType from './../src/PostConditionType'
import Precondition from './../src/Precondition'

describe('BusinessContext', () => {
    describe('#merge', () => {
        it('adds all preconditions and postconditions', () => {
            const userCreate = new BusinessContext(
                BusinessContextIdentifier.of('user.create'),
                new Set().add(BusinessContextIdentifier.of('auth.loggedIn')),
                new Set().add(new Precondition('emailFilled', null)),
                new Set().add(new PostCondition('filterEmailField', PostConditionType.FILTER_OBJECT_FIELD, 'email', null))
            )

            const authLoggedIn = new BusinessContext(
                BusinessContextIdentifier.of('auth.loggedIn'),
                new Set(),
                new Set().add(new Precondition('otherPrecondition'), null),
                new Set().add(new PostCondition('otherPostCondition', PostConditionType.FILTER_OBJECT_FIELD, 'item', null))
            )

            userCreate.merge(authLoggedIn)

            userCreate.preconditions.size.should.equal(2)
            userCreate.postConditions.size.should.equal(2)
        });
    });
});
