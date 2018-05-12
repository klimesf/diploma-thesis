export default class IsNotNull {
    constructor(argument) {
        this.argument = argument
    }

    interpret(context) {
        let string = this.argument.interpret(context)
        return string != null && typeof string === 'string' && string.length > 0
    }

    getName() {
        return 'is-not-blank'
    }

    getArguments() {
        return [this.argument]
    }

    getProperties() {
        return {}
    }

    toString() {
        return this.argument.toString() + " is not blank"
    }

    clone() {
        return new IsNotNull(this.argument.clone())
    }
}
