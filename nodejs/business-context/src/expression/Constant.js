export default class Constant {
    constructor(value, type) {
        this.value = value
        this.type = type
    }

    interpret(context) {
        return this.value
    }

    getName() {
        return 'constant'
    }

    getArguments() {
        return []
    }

    getProperties() {
        return {
            value: this.type.serialize(this.value),
            type: this.type.name,
        }
    }

    toString() {
        return this.value
    }

    clone() {
        return new Constant(
            JSON.parse(JSON.stringify(this.value)),
            this.type,
        )
    }
}
