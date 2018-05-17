export default class Add {
    constructor(left, right) {
        this.left = left
        this.right = right
    }

    interpret(context) {
        return this.left.interpret(context) + this.right.interpret(context)
    }

    getName() {
        return 'numeric-add'
    }

    getArguments() {
        return [this.left, this.right]
    }

    getProperties() {
        return {}
    }

    toString() {
        return this.left.toString() + " + " + this.right.toString()
    }

    clone() {
        return new Add(
            this.left.clone(),
            this.right.clone(),
        )
    }
}
