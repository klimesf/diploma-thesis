package cz.filipklimes.diploma.framework.businessContext.weaver;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.BusinessContextIdentifier;
import cz.filipklimes.diploma.framework.businessContext.BusinessContextRegistry;
import cz.filipklimes.diploma.framework.businessContext.BusinessRule;
import cz.filipklimes.diploma.framework.businessContext.BusinessRuleType;
import cz.filipklimes.diploma.framework.businessContext.exception.BusinessRulesCheckFailedException;
import cz.filipklimes.diploma.framework.businessContext.exception.UndefinedBusinessContextException;
import cz.filipklimes.diploma.framework.businessContext.expression.Constant;
import cz.filipklimes.diploma.framework.businessContext.expression.IsNotNull;
import cz.filipklimes.diploma.framework.businessContext.expression.VariableReference;
import cz.filipklimes.diploma.framework.businessContext.loader.LocalBusinessContextLoader;
import cz.filipklimes.diploma.framework.businessContext.loader.RemoteBusinessContextLoader;
import lombok.Getter;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static cz.filipklimes.diploma.framework.businessContext.expression.ExpressionType.BOOL;
import static cz.filipklimes.diploma.framework.businessContext.expression.ExpressionType.STRING;

public class BusinessContextWeaverTest
{

    @Test
    public void testPreconditionsOk() throws BusinessRulesCheckFailedException
    {
        BusinessContextWeaver evaluator = new BusinessContextWeaver(createRegistry());

        BusinessOperationContext context = new BusinessOperationContext("user.create");
        context.setVariable("name", "John Doe");
        context.setVariable("email", "john.doe@example.com");

        evaluator.evaluatePreconditions(context);
    }

    @Test(expected = BusinessRulesCheckFailedException.class)
    public void testPreconditionsNotMet() throws BusinessRulesCheckFailedException
    {
        BusinessContextWeaver evaluator = new BusinessContextWeaver(createRegistry());

        BusinessOperationContext context = new BusinessOperationContext("user.create");
        context.setVariable("name", "John Doe");
        context.setVariable("email", null);

        evaluator.evaluatePreconditions(context);
    }

    @Test
    public void testPostConditions()
    {
        BusinessContextWeaver evaluator = new BusinessContextWeaver(createRegistry());

        BusinessOperationContext context = new BusinessOperationContext("user.create");
        context.setVariable("name", "John Doe");
        context.setVariable("email", "john.doe@example.com");
        context.setVariable("user", new User("John Doe", "john.doe@example.com"));

        evaluator.applyPostConditions(context);

        Assert.assertEquals("John Doe", ((User) context.getVariable("user")).getName());
        // Assert.assertNull(((User) context.getVariable("user")).getEmail());
    }

    @Test(expected = UndefinedBusinessContextException.class)
    public void testNonExistingContext() throws BusinessRulesCheckFailedException
    {
        BusinessContextWeaver evaluator = new BusinessContextWeaver(createRegistry());

        BusinessOperationContext context = new BusinessOperationContext("order.create");
        evaluator.evaluatePreconditions(context);
    }

    private static BusinessContextRegistry createRegistry()
    {
        BusinessRule emailNotNull = BusinessRule.builder()
            .withName("emailIsNotNull")
            .withType(BusinessRuleType.PRECONDITION)
            .withCondition(new IsNotNull<>(new VariableReference<>("email", STRING)))
            .build();

        BusinessRule nameNotNull = BusinessRule.builder()
            .withName("nameIsNotNull")
            .withType(BusinessRuleType.PRECONDITION)
            .withCondition(new IsNotNull<>(new VariableReference<>("name", STRING)))
            .build();

        BusinessRule hideUserEmail = BusinessRule.builder()
            .withName("hideUserEmail")
            .withType(BusinessRuleType.POST_CONDITION)
            .withCondition(new Constant<>(true, BOOL))
            .build();

        BusinessContextIdentifier userCreateContextIdentifier = BusinessContextIdentifier.parse("user.create");
        BusinessContext userCreateContext = BusinessContext.builder()
            .withIdentifier(userCreateContextIdentifier)
            .withPrecondition(emailNotNull)
            .withPrecondition(nameNotNull)
            .withPostCondition(hideUserEmail)
            .build();

        return BusinessContextRegistry.builder()
            .withLocalLoader(new LocalBusinessContextLoader()
            {
                @Override
                public Set<BusinessContext> load()
                {
                    return Collections.singleton(userCreateContext);
                }
            })
            .withRemoteLoader(new RemoteBusinessContextLoader(new HashMap<>()))
            .build();
    }

    public static class User
    {

        @Getter
        private final String name;

        @Getter
        private final String email;

        public User(final String name, final String email)
        {
            this.name = name;
            this.email = email;
        }

    }

}
