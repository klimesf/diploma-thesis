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

    setInputParameter(name, value) {
        this.inputParameters[name] = value
    }

    getFunction(name) {
        if (!this.functions.hasOwnProperty(name)) {
            throw "Undefined function " + name
        }
        return this.functions[name]
    }

    setFunction(name, value) {
        this.functions[name] = value
    }

    getOutput() {
        return this.output
    }

    setOutput(output) {
        this.output = output
    }
}
