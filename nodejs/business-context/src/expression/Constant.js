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
            value: this.value,
            type: this.type,
        }
    }

    toString() {
        return this.value
    }
}
