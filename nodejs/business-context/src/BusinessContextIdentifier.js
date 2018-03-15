const PATTERN = /^[a-zA-Z0-9]+$/
const PREFIXED_PATTERN = /^([a-zA-Z0-9]+)\.([a-zA-Z0-9]+)$/

export default class BusinessContextIdentifier {

    constructor(prefix, name) {
        if (!PATTERN.test(prefix)) {
            throw new Error("invalid prefix")
        }
        this.prefix = prefix
        if (!PATTERN.test(name)) {
            throw new Error("invalid name")
        }
        this.name = name
    }

    toString() {
        return this.prefix + '.' + this.name
    }

    static of(prefixedName) {
        if (!PREFIXED_PATTERN.test(prefixedName)) {
            throw new Error("invalid prefixed name")
        }
        const match = PREFIXED_PATTERN.exec(prefixedName)
        return new BusinessContextIdentifier(match[1], match[2])
    }
}
