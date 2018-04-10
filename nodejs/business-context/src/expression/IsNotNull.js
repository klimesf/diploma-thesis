export default class IsNotNull {
    constructor(argument) {
        this.argument = argument
    }

    interpret(context) {
        return this.argument.interpret(context) != null
    }

    getName() {
        return 'is-not-null'
    }

    getArguments() {
        return [this.argument]
    }

    getProperties() {
        return {}
    }

    toString() {
        return this.argument.toString() + " is not null"
    }

    clone() {
        return new IsNotNull(this.argument.clone())
    }
}
