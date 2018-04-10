package cz.filipklimes.diploma.framework.businessContext.xml;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.BusinessContextIdentifier;
import cz.filipklimes.diploma.framework.businessContext.PostCondition;
import cz.filipklimes.diploma.framework.businessContext.PostConditionType;
import cz.filipklimes.diploma.framework.businessContext.Precondition;
import cz.filipklimes.diploma.framework.businessContext.expression.Constant;
import cz.filipklimes.diploma.framework.businessContext.expression.Expression;
import cz.filipklimes.diploma.framework.businessContext.expression.ExpressionType;
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
import cz.filipklimes.diploma.framework.businessContext.loader.LocalBusinessContextLoader;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.SAXEngine;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaderXSDFactory;

import java.io.*;
import java.math.BigDecimal;
import java.net.*;
import java.util.*;

public class BusinessContextXmlLoader implements LocalBusinessContextLoader
{

    public static final String BUSINESS_CONTEXT_XSD_PATH = "/businessContext.xsd";

    @Override
    public Set<BusinessContext> load()
    {
        // TODO: implement loading files from resources
        throw new UnsupportedOperationException("not implemented yet");
    }

    public BusinessContext loadFromFile(final File file) throws JDOMException, IOException
    {
        SAXEngine saxBuilder = createSaxEngine();
        Document doc = saxBuilder.build(file);
        return parse(doc);
    }

    public BusinessContext loadFromString(final String string) throws JDOMException, IOException
    {
        SAXEngine saxBuilder = createSaxEngine();
        Document doc = saxBuilder.build(new StringReader(string));
        return parse(doc);
    }

    private SAXEngine createSaxEngine() throws JDOMException
    {
        URL xsd = BusinessContextXmlLoader.class.getResource(BUSINESS_CONTEXT_XSD_PATH);
        XMLReaderJDOMFactory factory = new XMLReaderXSDFactory(xsd);
        return new SAXBuilder(factory);
    }

    @SuppressWarnings("unchecked")
    private BusinessContext parse(final Document doc)
    {
        Element contextEl = doc.getRootElement();

        // Context Identifier
        BusinessContext.Builder contextBuilder = BusinessContext.builder()
            .withIdentifier(new BusinessContextIdentifier(
                contextEl.getAttributeValue("prefix"),
                contextEl.getAttributeValue("name")
            ));

        // Included contexts
        contextEl.getChild("includedContexts")
            .getChildren("includedContext")
            .forEach(included -> contextBuilder.withIncludedContext(new BusinessContextIdentifier(
                included.getAttributeValue("prefix"),
                included.getAttributeValue("name")
            )));

        // Preconditions
        contextEl.getChild("preconditions")
            .getChildren("precondition")
            .forEach(preconditionEl -> contextBuilder.withPrecondition(
                Precondition.builder()
                    .withName(preconditionEl.getAttributeValue("name"))
                    .withCondition((Expression<Boolean>) buildExpression(preconditionEl.getChild("condition").getChildren().get(0)))
                    .build()
            ));

        // Postconditions
        contextEl.getChild("postConditions")
            .getChildren("postCondition")
            .forEach(postConditionEl -> contextBuilder.withPostCondition(
                PostCondition.builder()
                    .withName(postConditionEl.getAttributeValue("name"))
                    .withReferenceName(postConditionEl.getChildText("referenceName"))
                    .withType(PostConditionType.valueOf(postConditionEl.getChildText("type").toUpperCase()))
                    .withCondition((Expression<Boolean>) buildExpression(postConditionEl.getChild("condition").getChildren().get(0)))
                    .build()
            ));

        return contextBuilder.build();
    }

    @SuppressWarnings("unchecked")
    private Expression<?> buildExpression(final Element expressionEl)
    {
        ExpressionType type;

        switch (expressionEl.getName()) {
            case "numericAdd":
                return new Add(
                    (Expression<BigDecimal>) buildExpression(expressionEl.getChild("left").getChildren().get(0)),
                    (Expression<BigDecimal>) buildExpression(expressionEl.getChild("right").getChildren().get(0))
                );
            case "numericDivide":
                return new Divide(
                    (Expression<BigDecimal>) buildExpression(expressionEl.getChild("left").getChildren().get(0)),
                    (Expression<BigDecimal>) buildExpression(expressionEl.getChild("right").getChildren().get(0))
                );
            case "numericGreaterThan":
                return new GreaterThan(
                    (Expression<BigDecimal>) buildExpression(expressionEl.getChild("left").getChildren().get(0)),
                    (Expression<BigDecimal>) buildExpression(expressionEl.getChild("right").getChildren().get(0))
                );
            case "numericGreaterOrEqualTo":
                return new GreaterOrEqualTo(
                    (Expression<BigDecimal>) buildExpression(expressionEl.getChild("left").getChildren().get(0)),
                    (Expression<BigDecimal>) buildExpression(expressionEl.getChild("right").getChildren().get(0))
                );
            case "numericLessThan":
                return new LessThan(
                    (Expression<BigDecimal>) buildExpression(expressionEl.getChild("left").getChildren().get(0)),
                    (Expression<BigDecimal>) buildExpression(expressionEl.getChild("right").getChildren().get(0))
                );
            case "numericLessOrEqualTo":
                return new LessOrEqualTo(
                    (Expression<BigDecimal>) buildExpression(expressionEl.getChild("left").getChildren().get(0)),
                    (Expression<BigDecimal>) buildExpression(expressionEl.getChild("right").getChildren().get(0))
                );
            case "numericMultiply":
                return new Multiply(
                    (Expression<BigDecimal>) buildExpression(expressionEl.getChild("left").getChildren().get(0)),
                    (Expression<BigDecimal>) buildExpression(expressionEl.getChild("right").getChildren().get(0))
                );
            case "numericSubtract":
                return new Subtract(
                    (Expression<BigDecimal>) buildExpression(expressionEl.getChild("left").getChildren().get(0)),
                    (Expression<BigDecimal>) buildExpression(expressionEl.getChild("right").getChildren().get(0))
                );
            case "logicalAnd":
                return new And(
                    (Expression<Boolean>) buildExpression(expressionEl.getChild("left").getChildren().get(0)),
                    (Expression<Boolean>) buildExpression(expressionEl.getChild("right").getChildren().get(0))
                );
            case "logicalEquals":
                return new Equals<>(
                    (Expression<Boolean>) buildExpression(expressionEl.getChild("left").getChildren().get(0)),
                    (Expression<Boolean>) buildExpression(expressionEl.getChild("right").getChildren().get(0))
                );
            case "logicalNegate":
                return new Negate((Expression<Boolean>) buildExpression(expressionEl.getChild("argument").getChildren().get(0)));
            case "logicalOr":
                return new Or(
                    (Expression<Boolean>) buildExpression(expressionEl.getChild("left").getChildren().get(0)),
                    (Expression<Boolean>) buildExpression(expressionEl.getChild("right").getChildren().get(0))
                );
            case "constant":
                type = ExpressionType.valueOf(expressionEl.getAttributeValue("type").toUpperCase());
                return new Constant<>(
                    type.deserialize(expressionEl.getAttributeValue("value")),
                    type
                );
            case "variableReference":
                type = ExpressionType.valueOf(expressionEl.getAttributeValue("type").toUpperCase());
                return new VariableReference<>(
                    expressionEl.getAttributeValue("name"),
                    type
                );
            case "objectPropertyReference":
                type = ExpressionType.valueOf(expressionEl.getAttributeValue("type").toUpperCase());
                return new ObjectPropertyReference<>(
                    expressionEl.getAttributeValue("objectName"),
                    expressionEl.getAttributeValue("propertyName"),
                    type
                );
            case "isNotNull":
                return new IsNotNull<>(buildExpression(expressionEl.getChild("argument").getChildren().get(0)));
            default:
                throw new RuntimeException(String.format("Unsupported expression type: %s", expressionEl.getName()));
        }
    }

}
