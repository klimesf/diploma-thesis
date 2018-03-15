import BusinessContextIdentifier from '../BusinessContextIdentifier'

export default class RemoteBusinessContextLoader {

    constructor(loaders) {
        this.loaders = loaders
    }

    loadContextsByIdentifier(identifiers) {
        const identifiersByPrefix = {}
        identifiers
            .map(identifier => BusinessContextIdentifier.of(identifier))
            .forEach(identifier => identifiersByPrefix[identifier.prefix] = identifier.toString())

        const loadedContexts = {}
        Object.keys(identifiersByPrefix).forEach(key => {
            if (!identifiersByPrefix.hasOwnProperty(key)) return;

            this.loaders[key].loadContexts(identifiersByPrefix[key])
                .forEach(context => {
                    loadedContexts[context.identifier.toString()] = context
                })
        })

        return loadedContexts
    }
}
