export default class BusinessOperationContext {
    constructor(name) {
        this.name = name
        this.inputParameters = {}
        this.functions = {}
        this.output = null
    }

    getInputParameter(name) {
        if (!this.inputParameters.hasOwnProperty(name)) {
            throw "Undefined input parameter " + name
        }
        return this.inputParameters[name]
    }
}
