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

}
