package cz.filipklimes.diploma.framework.businessContext.weaver;

public class BusinessRuleEvaluatorTest
{
//
//    @Test
//    public void testApplicableRule()
//    {
//        Set<BusinessRule> ruleSet = new HashSet<>();
//
//        BusinessRule discountForElders = new BusinessRule(
//            "discount for elders",
//            "order.create",
//            // if user.age >= 70
//            new GreaterOrEqualTo(new ObjectPropertyReference<>("user", "age", NUMBER), new Constant<>(new BigDecimal(70), NUMBER)),
//            // then order.sum = order.sum * 80 %
//            new ObjectPropertyAssignment<>("order", "sum", new Multiply(
//                new ObjectPropertyReference<>("order", "sum", NUMBER),
//                new Constant<>(new BigDecimal("0.8"), NUMBER)
//            ))
//        );
//        ruleSet.add(discountForElders);
//
//        BusinessRuleEvaluator evaluator = new BusinessRuleEvaluator(ruleSet);
//
//        BusinessOperationContext context = new BusinessOperationContext("order.create");
//        context.setVariable("order", new Order(BigDecimal.valueOf(1000)));
//        context.setVariable("user", new User("John Doe", BigDecimal.valueOf(70)));
//
//        evaluator.evaluate(context);
//
//        Assert.assertEquals(
//            0,
//            BigDecimal.valueOf(800)
//                .compareTo(((Order) context.getVariable("order")).getSum())
//        );
//    }
//
//    @Test
//    public void testNoApplicableRule()
//    {
//        Set<BusinessRule> ruleSet = new HashSet<>();
//
//        BusinessRule discountForElders = new BusinessRule(
//            "nasty rule",
//            "invalid",
//            // if true
//            new Constant<>(true, BOOL),
//            // then order.sum = 500
//            new ObjectPropertyAssignment<>("order", "sum", new Constant<>(500, NUMBER))
//        );
//        ruleSet.add(discountForElders);
//
//        BusinessRuleEvaluator evaluator = new BusinessRuleEvaluator(ruleSet);
//
//        BusinessOperationContext context = new BusinessOperationContext("order.create");
//        context.setVariable("order", new Order(BigDecimal.valueOf(1000)));
//
//        evaluator.evaluate(context);
//
//        Assert.assertEquals(
//            0,
//            BigDecimal.valueOf(1000)
//                .compareTo(((Order) context.getVariable("order")).getSum())
//        );
//    }
//
//    @Test
//    public void testUnmetConditionRule()
//    {
//        Set<BusinessRule> ruleSet = new HashSet<>();
//
//        BusinessRule discountForElders = new BusinessRule(
//            "unsatisfiable rule",
//            "order.create",
//            // if false
//            new Constant<>(false, BOOL),
//            // then order.sum = 500
//            new ObjectPropertyAssignment<>("order", "sum", new Constant<>(500, NUMBER))
//        );
//        ruleSet.add(discountForElders);
//
//        BusinessRuleEvaluator evaluator = new BusinessRuleEvaluator(ruleSet);
//
//        BusinessOperationContext context = new BusinessOperationContext("order.create");
//        context.setVariable("order", new Order(BigDecimal.valueOf(1000)));
//
//        evaluator.evaluate(context);
//
//        Assert.assertEquals(
//            0,
//            BigDecimal.valueOf(1000)
//                .compareTo(((Order) context.getVariable("order")).getSum())
//        );
//    }
//
//    public static class Order
//    {
//
//        @Getter
//        @Setter
//        private BigDecimal sum;
//
//        public Order(final BigDecimal sum)
//        {
//            this.sum = sum;
//        }
//
//    }
//
//    public static class User
//    {
//
//        @Getter
//        private final String name;
//
//        @Getter
//        private final BigDecimal age;
//
//        public User(final String name, final BigDecimal age)
//        {
//            this.name = name;
//            this.age = age;
//        }
//
//    }

}
