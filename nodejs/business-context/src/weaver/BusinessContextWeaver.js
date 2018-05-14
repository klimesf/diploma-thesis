import PreconditionCheckFailed from './../error/PreconditionCheckFailed'
import PostConditionType from './../PostConditionType'

export default class BusinessContextWeaver {

    constructor(registry) {
        this.registry = registry
    }

    evaluatePreconditions(operationContext) {
        this.registry.waitForTransaction()
        const businessContext = this.registry.getContextByIdentifier(operationContext.name)
        const failed = []
        businessContext.preconditions.forEach(precondition => {
            if (!precondition.condition.interpret(operationContext)) {
                failed.push(precondition)
            }
        })
        if (failed.length > 0) {
            throw new PreconditionCheckFailed(failed)
        }
    }

    applyPostConditions(operationContext) {
        this.registry.waitForTransaction()
        const businessContext = this.registry.getContextByIdentifier(operationContext.name)
        businessContext.postConditions.forEach(postCondition => {
            switch (postCondition.type) {
                case PostConditionType.FILTER_OBJECT_FIELD:
                    BusinessContextWeaver.filterObjectField(postCondition, operationContext)
                    break
                case PostConditionType.FILTER_LIST_OF_OBJECTS:
                    BusinessContextWeaver.filterListOfObjects(postCondition, operationContext)
                    break
                case PostConditionType.FILTER_LIST_OF_OBJECTS_FIELD:
                    BusinessContextWeaver.filterListOfObjectsField(postCondition, operationContext)
                    break
                default:
                    throw "unknown post condition type: " + postCondition.type
            }
        })
    }

    static filterObjectField(postCondition, operationContext) {
        if (!postCondition.condition.interpret(operationContext)) {
            return
        }
        operationContext.getOutput()[postCondition.referenceName] = null
    }

    static filterListOfObjects(postCondition, operationContext) {
        const resultOutput = []
        operationContext.getOutput().forEach(item => {
            operationContext.setInputParameter('item', item)
            if (postCondition.condition.interpret(operationContext)) {
                return
            }
            resultOutput.push(item)
        })
        operationContext.setOutput(resultOutput)
    }

    static filterListOfObjectsField(postCondition, operationContext) {
        operationContext.getOutput().forEach(item => {
            operationContext.setInputParameter('item', item)
            if (!postCondition.condition.interpret(operationContext)) {
                return
            }
            item[postCondition.referenceName] = null
        })
    }
}
