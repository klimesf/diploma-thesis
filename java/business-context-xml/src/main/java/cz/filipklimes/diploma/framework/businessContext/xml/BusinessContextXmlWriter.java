package cz.filipklimes.diploma.framework.businessContext.xml;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.expression.Constant;
import cz.filipklimes.diploma.framework.businessContext.expression.Expression;
import cz.filipklimes.diploma.framework.businessContext.expression.ExpressionVisitor;
import cz.filipklimes.diploma.framework.businessContext.expression.FunctionCall;
import cz.filipklimes.diploma.framework.businessContext.expression.IsNotNull;
import cz.filipklimes.diploma.framework.businessContext.expression.ObjectPropertyReference;
import cz.filipklimes.diploma.framework.businessContext.expression.VariableReference;
import cz.filipklimes.diploma.framework.businessContext.expression.logical.And;
import cz.filipklimes.diploma.framework.businessContext.expression.logical.Equals;
import cz.filipklimes.diploma.framework.businessContext.expression.logical.Negate;
import cz.filipklimes.diploma.framework.businessContext.expression.logical.Or;
import cz.filipklimes.diploma.framework.businessContext.expression.numeric.Add;
import cz.filipklimes.diploma.framework.businessContext.expression.numeric.Divide;
import cz.filipklimes.diploma.framework.businessContext.expression.numeric.GreaterOrEqualTo;
import cz.filipklimes.diploma.framework.businessContext.expression.numeric.GreaterThan;
import cz.filipklimes.diploma.framework.businessContext.expression.numeric.LessOrEqualTo;
import cz.filipklimes.diploma.framework.businessContext.expression.numeric.LessThan;
import cz.filipklimes.diploma.framework.businessContext.expression.numeric.Multiply;
import cz.filipklimes.diploma.framework.businessContext.expression.numeric.Subtract;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.*;

public class BusinessContextXmlWriter
{

    public Document createDocument(final BusinessContext businessContext)
    {
        Objects.requireNonNull(businessContext);

        Element businessContextEl = new Element("businessContext");
        businessContextEl.setAttribute("prefix", businessContext.getIdentifier().getPrefix());
        businessContextEl.setAttribute("name", businessContext.getIdentifier().getName());
        Document doc = new Document(businessContextEl);

        // Included contexts
        Element includedContextsEl = new Element("includedContexts");
        businessContext.getIncludedContexts().forEach(included -> {
            Element includedContextEl = new Element("includedContext");
            includedContextEl.setAttribute("prefix", included.getPrefix());
            includedContextEl.setAttribute("name", included.getName());
            includedContextsEl.addContent(includedContextEl);
        });
        doc.getRootElement().addContent(includedContextsEl);

        // Preconditions
        Element preconditionsEl = new Element("preconditions");
        businessContext.getPreconditions().forEach(precondition -> {
            Element preconditionEl = new Element("precondition");
            preconditionEl.setAttribute("name", precondition.getName());
            Element conditionEl = createConditionElement(precondition.getCondition());
            preconditionEl.addContent(conditionEl);
            preconditionsEl.addContent(preconditionEl);
        });
        doc.getRootElement().addContent(preconditionsEl);

        // Post-conditions
        Element postConditionsEl = new Element("postConditions");
        businessContext.getPostConditions().forEach(postCondition -> {
            Element postConditionEl = new Element("postCondition");
            postConditionEl.setAttribute("name", postCondition.getName());

            Element typeEl = new Element("type");
            typeEl.addContent(postCondition.getType().name());
            postConditionEl.addContent(typeEl);

            Element referenceNameEl = new Element("referenceName");
            referenceNameEl.addContent(postCondition.getReferenceName());
            postConditionEl.addContent(referenceNameEl);

            Element conditionEl = createConditionElement(postCondition.getCondition());
            postConditionEl.addContent(conditionEl);

            postConditionsEl.addContent(postConditionEl);
        });
        doc.getRootElement().addContent(postConditionsEl);

        return doc;
    }

    private Element createConditionElement(final Expression<?> condition)
    {
        Element conditionEl = new Element("condition");
        XmlExpressionVisitor visitor = new XmlExpressionVisitor();
        condition.accept(visitor);
        conditionEl.addContent(visitor.getXml());
        return conditionEl;
    }

    private static final class XmlExpressionVisitor implements ExpressionVisitor
    {

        private Element content;

        @Override
        public void visit(final Add add)
        {
            content = buildBinaryExpression("numericAdd", add.getLeft(), add.getRight());
        }

        @Override
        public void visit(final GreaterOrEqualTo greaterOrEqualTo)
        {
            content = buildBinaryExpression("numericGreaterOrEqualTo", greaterOrEqualTo.getLeft(), greaterOrEqualTo.getRight());
        }

        @Override
        public void visit(final Divide divide)
        {
            content = buildBinaryExpression("numericDivide", divide.getLeft(), divide.getRight());
        }

        @Override
        public void visit(final GreaterThan greaterThan)
        {
            content = buildBinaryExpression("numericGreaterThan", greaterThan.getLeft(), greaterThan.getRight());
        }

        @Override
        public void visit(final LessOrEqualTo lessOrEqualTo)
        {
            content = buildBinaryExpression("numericLessOrEqualTo", lessOrEqualTo.getLeft(), lessOrEqualTo.getRight());
        }

        @Override
        public void visit(final LessThan lessThan)
        {
            content = buildBinaryExpression("numericLessThan", lessThan.getLeft(), lessThan.getRight());
        }

        @Override
        public void visit(final Multiply multiply)
        {
            content = buildBinaryExpression("numericMultiply", multiply.getLeft(), multiply.getRight());
        }

        @Override
        public void visit(final Subtract subtract)
        {
            content = buildBinaryExpression("numericSubtract", subtract.getLeft(), subtract.getRight());
        }

        @Override
        public void visit(final And and)
        {
            content = buildBinaryExpression("logicalAnd", and.getLeft(), and.getRight());
        }

        @Override
        public void visit(final Equals<?, ?> equals)
        {
            content = buildBinaryExpression("logicalEquals", equals.getLeft(), equals.getRight());
        }

        @Override
        public void visit(final Negate negate)
        {
            content = buildUnaryExpression("logicalNegate", negate.getArgument());
        }

        @Override
        public void visit(final Or or)
        {
            content = buildBinaryExpression("logicalOr", or.getLeft(), or.getRight());
        }

        @Override
        public void visit(final Constant<?> constant)
        {
            Element el = new Element("constant");
            constant.getProperties().forEach(el::setAttribute);
            content = el;
        }

        @Override
        public void visit(final FunctionCall<?> functionCall)
        {
            Element el = new Element("functionCall");
            functionCall.getProperties().forEach(el::setAttribute);
            content = el;
        }

        @Override
        public void visit(final IsNotNull<?> isNotNull)
        {
            content = buildUnaryExpression("isNotNull", isNotNull.getArgument());
        }

        @Override
        public void visit(final ObjectPropertyReference<?> objectPropertyReference)
        {
            Element el = new Element("variableReference");
            objectPropertyReference.getProperties().forEach(el::setAttribute);
            content = el;
        }

        @Override
        public void visit(final VariableReference<?> variableReference)
        {
            Element el = new Element("variableReference");
            variableReference.getProperties().forEach(el::setAttribute);
            content = el;
        }

        private Element getXml()
        {
            return content;
        }

        private Element buildBinaryExpression(final String elementName, final Expression<?> left, final Expression<?> right)
        {
            Element el = new Element(elementName);

            XmlExpressionVisitor leftVisitor = new XmlExpressionVisitor();
            left.accept(leftVisitor);
            Element leftEl = new Element("left");
            leftEl.addContent(leftVisitor.getXml());
            el.addContent(leftEl);

            XmlExpressionVisitor rightVisitor = new XmlExpressionVisitor();
            right.accept(rightVisitor);
            Element rightEl = new Element("right");
            rightEl.addContent(rightVisitor.getXml());
            el.addContent(rightEl);

            return el;
        }

        private Element buildUnaryExpression(final String elementName, final Expression<?> argument)
        {
            Element el = new Element(elementName);

            XmlExpressionVisitor leftVisitor = new XmlExpressionVisitor();
            argument.accept(leftVisitor);
            Element leftEl = new Element("argument");
            leftEl.addContent(leftVisitor.getXml());
            el.addContent(leftEl);

            return el;
        }

    }

}
