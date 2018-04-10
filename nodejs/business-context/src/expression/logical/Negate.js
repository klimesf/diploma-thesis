export default class Negate {
    constructor(arg) {
        this.arg = arg
    }

    interpret(context) {
        return !this.arg.interpret(context)
    }

    getName() {
        return 'logical-negate'
    }

    getArguments() {
        return [this.arg]
    }

    getProperties() {
        return {}
    }

    toString() {
        return 'not ' + this.arg.toString()
    }

    clone() {
        return new Negate(
            this.arg.clone()
        )
    }
}
