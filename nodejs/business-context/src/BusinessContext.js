import BusinessContextIdentifier from './BusinessContextIdentifier'

export default class BusinessContext {

    constructor(identifier, includedContexts, preconditions, postConditions) {
        this.identifier = identifier
        this.includedContexts = includedContexts
        this.preconditions = preconditions
        this.postConditions = postConditions
    }

    merge(other) {
        other.preconditions.forEach(precondition => this.preconditions.add(precondition))
        other.postConditions.forEach(postCondition => this.postConditions.add(postCondition))
    }

    clone() {
        return new BusinessContext(
            new BusinessContextIdentifier(JSON.parse(JSON.stringify(this.identifier.prefix)), JSON.parse(JSON.stringify(this.identifier.name))),
            new Set([...this.includedContexts].map(included => BusinessContextIdentifier.of(included.toString()))),
            new Set([...this.preconditions].map(precondition => precondition.clone())),
            new Set([...this.postConditions].map(postCondition => postCondition.clone()))
        )
    }
}
