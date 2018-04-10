export default class Precondition {

    constructor(name, condition) {
        this.name = name
        this.condition = condition
    }

    clone() {
        return new Precondition(
            this.name.slice(0),
            this.condition.clone()
        )
    }
}
