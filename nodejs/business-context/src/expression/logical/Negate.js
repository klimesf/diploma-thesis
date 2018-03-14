export default class Negate {
    constructor(arg, right) {
        this.arg = arg
        this.right = right
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
}
