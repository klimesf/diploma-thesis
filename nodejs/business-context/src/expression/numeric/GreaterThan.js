export default class GreaterThan {
    constructor(left, right) {
        this.left = left
        this.right = right
    }

    interpret(context) {
        return this.left.interpret(context) > this.right.interpret(context)
    }

    getName() {
        return 'numeric-gt'
    }

    getArguments() {
        return [this.left, this.right]
    }

    getProperties() {
        return {}
    }

    toString() {
        return this.left.toString() + " > " + this.right.toString()
    }

    clone() {
        return new GreaterThan(
            this.left.clone(),
            this.right.clone(),
        )
    }
}
