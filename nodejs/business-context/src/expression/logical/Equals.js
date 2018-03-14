export default class Equals {
    constructor(left, right) {
        this.left = left
        this.right = right
    }

    interpret(context) {
        return this.left.interpret(context) === this.right.interpret(context)
    }

    getName() {
        return 'logical-equals'
    }

    getArguments() {
        return [this.left, this.right]
    }

    getProperties() {
        return {}
    }

    toString() {
        return this.left.toString() + " equals " + this.right.toString()
    }
}
