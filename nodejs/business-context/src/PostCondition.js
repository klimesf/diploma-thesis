export default class PostCondition {

    constructor(name, type, referenceName, condition) {
        this.name = name
        this.type = type
        this.referenceName = referenceName
        this.condition = condition
    }

    clone() {
        return new PostCondition(
            JSON.parse(JSON.stringify(this.name)),
            this.type,
            JSON.parse(JSON.stringify(this.referenceName)),
            this.condition.clone()
        )
    }

}
