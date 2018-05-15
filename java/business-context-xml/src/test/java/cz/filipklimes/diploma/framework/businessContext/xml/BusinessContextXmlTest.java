package cz.filipklimes.diploma.framework.businessContext.xml;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.BusinessContextIdentifier;
import cz.filipklimes.diploma.framework.businessContext.PostCondition;
import cz.filipklimes.diploma.framework.businessContext.PostConditionType;
import cz.filipklimes.diploma.framework.businessContext.Precondition;
import cz.filipklimes.diploma.framework.businessContext.expression.Constant;
import cz.filipklimes.diploma.framework.businessContext.expression.ExpressionType;
import cz.filipklimes.diploma.framework.businessContext.expression.IsNotNull;
import cz.filipklimes.diploma.framework.businessContext.expression.VariableReference;
import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class BusinessContextXmlTest
{

    @Test
    public void testSerializeAndDeserialize() throws Exception
    {
        BusinessContext context = BusinessContext.builder()
            .withIdentifier(BusinessContextIdentifier.parse("user.createEmployee"))
            .withIncludedContext(BusinessContextIdentifier.parse("auth.loggedIn"))
            .withIncludedContext(BusinessContextIdentifier.parse("user.validEmail"))
            .withPrecondition(
                Precondition.builder()
                    .withName("true")
                    .withCondition(new IsNotNull<>(new VariableReference<>("email", ExpressionType.STRING)))
                    .build()
            )
            .withPostCondition(
                PostCondition.builder()
                    .withName("hideUserEmail")
                    .withReferenceName("email")
                    .withType(PostConditionType.FILTER_OBJECT_FIELD)
                    .withCondition(new Constant<>(true, ExpressionType.BOOL))
                    .build()
            )
            .build();

        File file = File.createTempFile("user_createEmployee", "xml");
        file.deleteOnExit();

        BusinessContextXmlWriter xmlWriter = new BusinessContextXmlWriter();
        Document document = xmlWriter.createDocument(context);

        try (FileOutputStream out = new FileOutputStream(file)) {
            XMLOutputter xmlOutput = new XMLOutputter();
            xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(document, new OutputStreamWriter(out));
        }

        List<InputStream> streams = new ArrayList<>();
        BusinessContextXmlLoader loader = new BusinessContextXmlLoader(streams);
        BusinessContext loadedContext = loader.loadFromFile(file);

        Assert.assertEquals(context.getIdentifier(), loadedContext.getIdentifier());
        Assert.assertTrue(loadedContext.getIncludedContexts().contains(BusinessContextIdentifier.parse("auth.loggedIn")));
        Assert.assertTrue(loadedContext.getIncludedContexts().contains(BusinessContextIdentifier.parse("user.validEmail")));
        Assert.assertEquals(context.getPreconditions().size(), loadedContext.getPreconditions().size());
        Assert.assertEquals(context.getPostConditions().size(), loadedContext.getPostConditions().size());
    }

    @Test
    public void testLoadFromResources() throws Exception
    {
        List<InputStream> streams = new ArrayList<>();
        streams.add(getClass().getResourceAsStream("/business-contexts/adminLoggedIn.xml"));
        streams.add(getClass().getResourceAsStream("/business-contexts/correctAddress.xml"));
        BusinessContextXmlLoader loader = new BusinessContextXmlLoader(streams);
        Set<BusinessContext> contexts = loader.load();

        Assert.assertEquals(2, contexts.size());
        Set<BusinessContextIdentifier> identifiers = contexts.stream().map(BusinessContext::getIdentifier).collect(Collectors.toSet());
        Assert.assertTrue(identifiers.contains(new BusinessContextIdentifier("auth", "adminLoggedIn")));
        Assert.assertTrue(identifiers.contains(new BusinessContextIdentifier("billing", "correctAddress")));
    }

}
