import Constant from './Constant'

export default class ObjectPropertyReference {
    constructor(objectName, propertyName, type) {
        this.objectName = objectName
        this.propertyName = propertyName
        this.type = type
    }

    interpret(context) {
        const object = context.getInputParameter(this.objectName)
        if (!object.hasOwnProperty(this.propertyName)) {
            throw "object " + this.objectName + " has no property " + this.propertyName
        }
        return object[this.propertyName]
    }

    getName() {
        return 'object-property-reference'
    }

    getArguments() {
        return []
    }

    getProperties() {
        return {
            objectName: this.objectName,
            propertyName: this.propertyName,
            type: this.type.name
        }
    }

    toString() {
        return '$' + this.objectName + '.' + this.propertyName
    }

    clone() {
        return new ObjectPropertyReference(
            JSON.parse(JSON.stringify(this.objectName)),
            JSON.parse(JSON.stringify(this.propertyName)),
            this.type,
        )
    }
}
