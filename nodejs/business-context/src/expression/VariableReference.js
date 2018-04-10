export default class VariableReference {
    constructor(name, type) {
        this.name = name
        this.type = type
    }

    interpret(context) {
        return context.getInputParameter(this.name)
    }

    getName() {
        return 'variable-reference'
    }

    getArguments() {
        return []
    }

    getProperties() {
        return {
            name: this.name,
            type: this.type.name,
        }
    }

    toString() {
        return '$' + this.name
    }

    clone() {
        return new VariableReference(
            JSON.parse(JSON.stringify(this.name)),
            this.type,
        )
    }
}
