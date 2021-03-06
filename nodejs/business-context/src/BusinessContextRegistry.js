export default class BusinessContextRegistry {

    constructor(localLoader, remoteLoader) {
        this.localLoader = localLoader
        this.remoteLoader = remoteLoader
        this.contexts = {}
        this.localContexts = {}
        this.transactionChanges = []
        this.transactionInProgress = false
        this.initialize()
    }

    initialize() {
        // Load local contexts
        this.localLoader.load().map(context => {
            const identifier = context.identifier.toString()
            if (this.contexts.hasOwnProperty(identifier)) {
                throw "duplicate context: " + identifier
            }
            this.contexts[identifier] = context
        })

        Object.values(this.contexts).forEach(context => {
            this.localContexts[context.identifier.toString()] = context.clone()
        })

        // Analyze and find out which contexts to fetch
        const remoteContextsIdentifiers = new Set()
        Object.values(this.contexts)
            .map(context => context.includedContexts)
            .forEach(identifier => {
                if (!remoteContextsIdentifiers.hasOwnProperty(identifier) && !this.contexts.hasOwnProperty(identifier)) {
                    remoteContextsIdentifiers.add(identifier)
                }
            })

        // Load remote contexts
        const remoteContexts = this.remoteLoader.loadContextsByIdentifier(remoteContextsIdentifiers)

        // Include remote contexts into the local ones
        Object.values(this.contexts).forEach(context => {
            context.includedContexts.forEach(includedContextIdentifier => {
                if (this.contexts.hasOwnProperty(includedContextIdentifier)) {
                    context.merge(this.contexts[includedContextIdentifier])
                    return
                }

                if (!remoteContexts.hasOwnProperty(includedContextIdentifier)) {
                    throw "unknown context: " + includedContextIdentifier
                }

                context.merge(remoteContexts[includedContextIdentifier])
            })
        })
    }

    getContextByIdentifier(identifier) {
        if (!this.contexts.hasOwnProperty(identifier)) {
            throw "unknown context: " + identifier
        }
        return this.contexts[identifier]
    }

    getContextsByIdentifiers(identifiers) {
        return identifiers.map(identifier => this.getContextByIdentifier(identifier))
    }

    getAllContexts() {
        return Object.values(this.localContexts)
    }

    saveOrUpdateBusinessContext(context) {
        if (!this.transactionInProgress) {
            throw "transaction not in progress, cannot save or update business context"
        }
        this.transactionChanges.push(context)
    }

    beginTransaction() {
        this.transactionInProgress = true
        this.transactionChanges = []
    }

    commitTransaction() {
        this.transactionChanges.forEach(context => {
            let identifier = context.identifier.toString()
            this.localContexts[identifier] = context.clone()

            const remoteContextsIdentifiers = new Set()
            context.includedContexts
                .forEach(identifier => {
                    if (!remoteContextsIdentifiers.hasOwnProperty(identifier) && !this.contexts.hasOwnProperty(identifier)) {
                        remoteContextsIdentifiers.add(identifier)
                    }
                })

            const remoteContexts = this.remoteLoader.loadContextsByIdentifier(remoteContextsIdentifiers)

            context.includedContexts.forEach(includedContextIdentifier => {
                if (this.contexts.hasOwnProperty(includedContextIdentifier)) {
                    context.merge(this.contexts[includedContextIdentifier])
                    return
                }

                if (!remoteContexts.hasOwnProperty(includedContextIdentifier)) {
                    throw "unknown context: " + includedContextIdentifier
                }

                context.merge(remoteContexts[includedContextIdentifier])
            })
        })
        this.transactionChanges = []
        this.transactionInProgress = false
    }

    rollbackTransaction() {
        this.transactionChanges = []
        this.transactionInProgress = false
    }

    waitForTransaction() {
        while (this.transactionInProgress) {
            // spin lock
        }
    }

}
