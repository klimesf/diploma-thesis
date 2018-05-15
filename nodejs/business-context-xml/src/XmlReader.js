"use strict"

const BusinessContext = require('business-context-framework/dist/BusinessContext').default
const BusinessContextIdentifier = require('business-context-framework/dist/BusinessContextIdentifier').default
const Precondition = require('business-context-framework/dist/Precondition').default
const PostCondition = require('business-context-framework/dist/PostCondition').default
const PostConditionType = require('business-context-framework/dist/PostConditionType').default
const IsNotNull = require('business-context-framework/dist/expression/IsNotNull').default
const IsNotBlank = require('business-context-framework/dist/expression/IsNotBlank').default
const ExpressionType = require('business-context-framework/dist/expression/ExpressionType').default
const VariableReference = require('business-context-framework/dist/expression/VariableReference').default
const FunctionCall = require('business-context-framework/dist/expression/FunctionCall').default
const ObjectPropertyReference = require('business-context-framework/dist/expression/ObjectPropertyReference').default
const Constant = require('business-context-framework/dist/expression/Constant').default
const LogicalAnd = require('business-context-framework/dist/expression/logical/And').default
const LogicalEquals = require('business-context-framework/dist/expression/logical/Equals').default
const LogicalNegate = require('business-context-framework/dist/expression/logical/Negate').default
const LogicalOr = require('business-context-framework/dist/expression/logical/Or').default
const DOMParser = require('xmldom').DOMParser
const Node = require('xmldom').Node

export default class XmlReader {

    static read(xml) {
        const dom = new DOMParser().parseFromString(xml, 'text/xml')
        const businessContext = dom.getElementsByTagName("businessContext")[0]

        return new BusinessContext(
            new BusinessContextIdentifier(businessContext.getAttribute("prefix"), businessContext.getAttribute("name")),
            this.readIncludedContexts(businessContext),
            this.readPreconditions(businessContext),
            this.readPostConditions(businessContext)
        )
    }

    static readIncludedContexts(root) {
        const result = new Set()
        const childNodes = root.getElementsByTagName("includedContexts")[0].childNodes
        for (let key in childNodes) {
            if (!childNodes.hasOwnProperty(key)) continue
            let node = childNodes[key]
            if (node.nodeType !== 1) continue // skip text
            result.add(new BusinessContextIdentifier(node.getAttribute("prefix"), node.getAttribute("name")))
        }

        return result;
    }

    static readPreconditions(root) {
        const result = new Set()
        const childNodes = root.getElementsByTagName("preconditions")[0].childNodes
        for (let key in childNodes) {
            if (!childNodes.hasOwnProperty(key)) continue
            let node = childNodes[key]
            if (node.nodeType !== 1) continue // skip text
            result.add(new Precondition(
                node.getAttribute("name"),
                this.extractExpression(node.getElementsByTagName("condition"))
            ))
        }

        return result;
    }

    static readPostConditions(root) {
        const result = new Set()
        const childNodes = root.getElementsByTagName("postConditions")[0].childNodes
        for (let key in childNodes) {
            if (!childNodes.hasOwnProperty(key)) continue
            let node = childNodes[key]
            if (node.nodeType !== 1) continue // skip text
            result.add(new PostCondition(
                node.getAttribute("name"),
                PostConditionType[node.getAttribute("type")],
                node.getAttribute("referenceName"),
                this.extractExpression(node.getElementsByTagName("condition"))
            ))
        }

        return result;
    }

    static extractExpression(parent) {
        const conditionElements = parent[0].childNodes
        for (let key in conditionElements) {
            if (!conditionElements.hasOwnProperty(key)) continue
            let node = conditionElements[key]
            if (node.nodeType !== 1) continue // skip text
            return this.buildExpression(conditionElements[key])
        }

        return null
    }

    static buildExpression(element) {
        let type = undefined
        switch (element.tagName) {
            case 'constant':
                type = this.convertExpressionType(element.getAttribute('type'))
                return new Constant(type.deserialize(this.findPropertyByName(element.properties, 'value')), type)
            case 'isNotNull':
                return new IsNotNull(this.extractExpression(element.getElementsByTagName("argument")))
            case 'isNotBlank':
                return new IsNotBlank(this.extractExpression(element.getElementsByTagName("argument")))
            case 'functionCall':
                type = this.convertExpressionType(element.getAttribute('type'))
                const args = element.getElementsByTagName("argument")
                const argsExpressions = []
                for (let key in args) {
                    if (!args.hasOwnProperty(key)) continue
                    argsExpressions.push(this.buildExpression(args[key]))
                }
                return new FunctionCall(
                    element.getAttribute('methodName'),
                    type,
                    argsExpressions
                )
            case 'objectPropertyReference':
                type = this.convertExpressionType(element.getAttribute('type'))
                return new ObjectPropertyReference(element.getAttribute('objectName'), element.getAttribute('propertyName'), type)
            case 'variableReference':
                type = this.convertExpressionType(element.getAttribute('type'))
                return new VariableReference(element.getAttribute('name'), type)
            case 'logicalAnd':
                return new LogicalAnd(
                    this.extractExpression(element.getElementsByTagName('left')),
                    this.extractExpression(element.getElementsByTagName('right'))
                )
            case 'logicalEquals':
                return new LogicalEquals(
                    this.extractExpression(element.getElementsByTagName('left')),
                    this.extractExpression(element.getElementsByTagName('right'))
                )
            case 'logicalNegate':
                return new LogicalNegate(this.extractExpression(element.getElementsByTagName('argument')))
            case 'logicalOr':
                return new LogicalOr(
                    this.extractExpression(element.getElementsByTagName('left')),
                    this.extractExpression(element.getElementsByTagName('right'))
                )
            default:
                throw "unknown expression " + element.tagName
        }
    }

    static convertExpressionType(type) {
        switch (type) {
            case ExpressionType.STRING.name:
                return ExpressionType.STRING;
            case ExpressionType.NUMBER.name:
                return ExpressionType.NUMBER;
            case ExpressionType.BOOL.name:
                return ExpressionType.BOOL;
            case ExpressionType.VOID.name:
                return ExpressionType.VOID;
            case ExpressionType.OBJECT.name:
                return ExpressionType.OBJECT;
            default:
                throw "unknown expression type " + type
        }
    }

    static findPropertyByName(properties, name) {

    }

}
