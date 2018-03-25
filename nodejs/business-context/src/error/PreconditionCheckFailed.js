export default class PreconditionCheckFailed {

    constructor(failedPreconditions) {
        this.failedPreconditions = failedPreconditions
    }

    getMessage() {
        return this.failedPreconditions.map(p => p.name).join('; ')
    }
}
