"use strict";

let chai = require('chai');

chai.should();

import BusinessContext from './../src/BusinessContext'
import BusinessContextRegistry from './../src/BusinessContextRegistry'
import BusinessContextIdentifier from './../src/BusinessContextIdentifier'

describe('BusinessContextRegistry', () => {
    describe('#saveOrUpdateContext', () => {
        it('cannot be run without transaction', () => {
            const registry = new BusinessContextRegistry(
                {
                    load: () => {
                        return []
                    }
                },
                {
                    loadContextsByIdentifier: (identifiers) => {
                        return {}
                    }
                }
            )
            try {
                registry.saveOrUpdateBusinessContext(null)
                throw "exception expected"
            } catch (e) {
                e.should.equal("transaction not in progress, cannot save or update business context")
            }
        })
    })
    describe('#commitTransaction', () => {
        it('saves all changes', async () => {
            const registry = new BusinessContextRegistry(
                {
                    load: () => {
                        return []
                    }
                },
                {
                    loadContextsByIdentifier: (identifiers) => {
                        return {}
                    }
                }
            )

            const authLoggedIn = new BusinessContext(
                BusinessContextIdentifier.of('auth.loggedIn'),
                new Set(),
                new Set(),
                new Set()
            )

            registry.beginTransaction()
            registry.saveOrUpdateBusinessContext(authLoggedIn)
            registry.commitTransaction()

            const contexts = await registry.getAllContexts()
            contexts.length.should.equal(1)
        })
    })
    describe('#rollbackTransaction', () => {
        it('aborts all changes', async () => {
            const registry = new BusinessContextRegistry(
                {
                    load: () => {
                        return []
                    }
                },
                {
                    loadContextsByIdentifier: (identifiers) => {
                        return {}
                    }
                }
            )

            const authLoggedIn = new BusinessContext(
                BusinessContextIdentifier.of('auth.loggedIn'),
                new Set(),
                new Set(),
                new Set()
            )

            registry.beginTransaction()
            registry.saveOrUpdateBusinessContext(authLoggedIn)
            registry.rollbackTransaction()

            const contexts = await registry.getAllContexts()
            contexts.length.should.equal(0)
        })
    })
})
