package cz.filipklimes.diploma.framework.businessContext;

import cz.filipklimes.diploma.framework.businessContext.expression.Constant;
import cz.filipklimes.diploma.framework.businessContext.expression.ExpressionType;
import org.junit.Test;

public class BusinessContextRegistryTest
{

    @Test
    public void test()
    {
        BusinessRule localPrecondition = BusinessRule.builder()
            .withName("local pre")
            .withType(BusinessRuleType.PRECONDITION)
            .withCondition(new Constant<>(true, ExpressionType.BOOL))
            .build();

        BusinessRule localPostCondition = BusinessRule.builder()
            .withName("local post")
            .withType(BusinessRuleType.POST_CONDITION)
            .withCondition(new Constant<>(true, ExpressionType.BOOL))
            .build();

        BusinessRule remotePrecondition = BusinessRule.builder()
            .withName("remote pre")
            .withType(BusinessRuleType.PRECONDITION)
            .withCondition(new Constant<>(true, ExpressionType.BOOL))
            .build();

        BusinessRule remotePostCondition = BusinessRule.builder()
            .withName("remote post")
            .withType(BusinessRuleType.POST_CONDITION)
            .withCondition(new Constant<>(true, ExpressionType.BOOL))
            .build();

        // TODO: fixme
//        BusinessContextRegistry registry = BusinessContextRegistry.builder()
//            .setLocalLoader(() -> new HashSet<>(Arrays.asList(localPrecondition, localPostCondition)))
//            .addRemoteLoader("remote", () -> new HashSet<>(Arrays.asList(remotePrecondition, remotePostCondition)))
//            .build();
//
//        registry.initialize();
//
//        Set<BusinessRule> localRules = registry.getLocalRules();
//
//        Assert.assertEquals(2, localRules.size());
//        Assert.assertTrue(localRules.contains(localPrecondition));
//        Assert.assertTrue(localRules.contains(localPostCondition));
//
//        Set<BusinessRule> context1Preconditions = registry.findPreconditions("context1");
//        Assert.assertEquals(1, context1Preconditions.size());
//        Assert.assertTrue(context1Preconditions.contains(localPrecondition));
//
//        Set<BusinessRule> context1PostConditions = registry.findPostConditions("context1");
//        Assert.assertEquals(2, context1PostConditions.size());
//        Assert.assertTrue(context1PostConditions.contains(localPostCondition));
//        Assert.assertTrue(context1PostConditions.contains(remotePostCondition));
//
//        Set<BusinessRule> context2Preconditions = registry.findPreconditions("context2");
//        Assert.assertEquals(1, context2Preconditions.size());
//        Assert.assertTrue(context2Preconditions.contains(remotePrecondition));
//
//        Set<BusinessRule> context2PostConditions = registry.findPostConditions("context2");
//        Assert.assertTrue(context2PostConditions.isEmpty());
    }

}
