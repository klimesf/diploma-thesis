export default class FunctionCall {
    constructor(methodName, type, args) {
        this.methodName = methodName
        this.type = type
        this.args = args
    }

    interpret(context) {
        const interpretedArgs = this.args.map(arg => arg.interpret(context))
        let func = context.getFunction(this.methodName)
        return func.apply(func, interpretedArgs)
    }

    getName() {
        return 'function-call'
    }

    getArguments() {
        return this.args
    }

    getProperties() {
        return {
            methodName: this.methodName,
            type: this.type.name,
        }
    }

    toString() {
        return 'call ' + this.methodName + '()'
    }
}
