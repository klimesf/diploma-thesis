export default class Or {
    constructor(left, right) {
        this.left = left
        this.right = right
    }

    interpret(context) {
        return this.left.interpret(context) || this.right.interpret(context)
    }

    getName() {
        return 'logical-or'
    }

    getArguments() {
        return [this.left, this.right]
    }

    getProperties() {
        return {}
    }

    toString() {
        return this.left.toString() + " or " + this.right.toString()
    }

    clone() {
        return new Or(
            this.left.clone(),
            this.right.clone(),
        )
    }
}
