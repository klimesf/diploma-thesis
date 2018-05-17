"use strict"

const fs = require('fs')
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

export default class XmlReader {

    constructor(files) {
        this.files = files
    }

    load() {
        return this.files.map(file => {
            let xml = fs.readFileSync(file, "utf8")
            return XmlReader.read(xml)
        })
    }

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
                this.extractExpression(this.findChildrenWithTagName(node, "condition"))
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
                this.extractExpression(this.findChildrenWithTagName(node, "condition"))
            ))
        }

        return result;
    }

    static extractExpression(parent) {
        const conditionElements = parent.childNodes
        for (let key in conditionElements) {
            if (!conditionElements.hasOwnProperty(key)) continue
            let node = conditionElements[key]
            if (node.nodeType !== 1) continue // skip text
            return this.buildExpression(conditionElements[key])
        }

        return null
    }

    static findChildrenWithTagName(parent, tagName) {
        const children = parent.childNodes
        for (let key in children) {
            if (!children.hasOwnProperty(key)) continue
            let node = children[key]
            if (node.nodeType !== 1) continue // skip text
            if (node.tagName !== tagName) continue

            return node
        }

        return null
    }

    static buildExpression(element) {
        let type = undefined
        switch (element.tagName) {
            case 'constant':
                type = this.convertExpressionType(element.getAttribute('type'))
                return new Constant(type.deserialize(element.getAttribute('value')), type)
            case 'isNotNull':
                return new IsNotNull(this.extractExpression(this.findChildrenWithTagName(element, 'argument')))
            case 'isNotBlank':
                return new IsNotBlank(this.extractExpression(this.findChildrenWithTagName(element, 'argument')))
            case 'functionCall':
                type = this.convertExpressionType(element.getAttribute('type'))
                const args = this.findChildrenWithTagName(element, "argument")
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
                    this.extractExpression(this.findChildrenWithTagName(element, 'left')),
                    this.extractExpression(this.findChildrenWithTagName(element, 'right'))
                )
            case 'logicalEquals':
                return new LogicalEquals(
                    this.extractExpression(this.findChildrenWithTagName(element, 'left')),
                    this.extractExpression(this.findChildrenWithTagName(element, 'right'))
                )
            case 'logicalNegate':
                return new LogicalNegate(this.extractExpression(this.findChildrenWithTagName(element, 'argument')))
            case 'logicalOr':
                return new LogicalOr(
                    this.extractExpression(this.findChildrenWithTagName(element, 'left')),
                    this.extractExpression(this.findChildrenWithTagName(element, 'right'))
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

}
