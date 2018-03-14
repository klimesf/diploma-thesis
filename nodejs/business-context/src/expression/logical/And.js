export default class And {
    constructor(left, right) {
        this.left = left
        this.right = right
    }

    interpret(context) {
        return this.left.interpret(context) && this.right.interpret(context)
    }

    getName() {
        return 'logical-and'
    }

    getArguments() {
        return [this.left, this.right]
    }

    getProperties() {
        return {}
    }

    toString() {
        return this.left.toString() + " and " + this.right.toString()
    }
}
