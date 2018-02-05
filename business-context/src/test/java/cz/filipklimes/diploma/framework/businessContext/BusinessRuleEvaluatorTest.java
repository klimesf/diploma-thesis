package cz.filipklimes.diploma.framework.businessContext;

import cz.filipklimes.diploma.framework.businessContext.expression.Constant;
import cz.filipklimes.diploma.framework.businessContext.expression.ExpressionType;
import cz.filipklimes.diploma.framework.businessContext.expression.ObjectPropertyAssignment;
import cz.filipklimes.diploma.framework.businessContext.expression.ObjectPropertyReference;
import cz.filipklimes.diploma.framework.businessContext.expression.numeric.GreaterOrEqualTo;
import cz.filipklimes.diploma.framework.businessContext.expression.numeric.Multiply;
import lombok.Getter;
import lombok.Setter;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

public class BusinessRuleEvaluatorTest
{

    @Test
    public void testApplicableRule()
    {
        Set<BusinessRule> ruleSet = new HashSet<>();

        BusinessRule discountForElders = new BusinessRule(
            "discount for elders",
            "order.create",
            // if user.age >= 70
            new GreaterOrEqualTo(new ObjectPropertyReference<>("user", "age", ExpressionType.NUMBER), new Constant<>(new BigDecimal(70))),
            // then order.sum = order.sum * 80 %
            new ObjectPropertyAssignment<>("order", "sum", new Multiply(
                new ObjectPropertyReference<>("order", "sum", ExpressionType.NUMBER),
                new Constant<>(new BigDecimal("0.8"))
            ))
        );
        ruleSet.add(discountForElders);

        BusinessRuleEvaluator evaluator = new BusinessRuleEvaluator(ruleSet);

        BusinessContext context = new BusinessContext("order.create");
        context.setVariable("order", new Order(BigDecimal.valueOf(1000)));
        context.setVariable("user", new User("John Doe", BigDecimal.valueOf(70)));

        evaluator.evaluate(context);

        Assert.assertEquals(
            0,
            BigDecimal.valueOf(800)
                .compareTo(((Order) context.getVariable("order")).getSum())
        );
    }

    @Test
    public void testNoApplicableRule()
    {
        Set<BusinessRule> ruleSet = new HashSet<>();

        BusinessRule discountForElders = new BusinessRule(
            "nasty rule",
            "invalid",
            // if true
            new Constant<>(true),
            // then order.sum = 500
            new ObjectPropertyAssignment<>("order", "sum", new Constant<>(500))
        );
        ruleSet.add(discountForElders);

        BusinessRuleEvaluator evaluator = new BusinessRuleEvaluator(ruleSet);

        BusinessContext context = new BusinessContext("order.create");
        context.setVariable("order", new Order(BigDecimal.valueOf(1000)));

        evaluator.evaluate(context);

        Assert.assertEquals(
            0,
            BigDecimal.valueOf(1000)
                .compareTo(((Order) context.getVariable("order")).getSum())
        );
    }

    @Test
    public void testUnmetConditionRule()
    {
        Set<BusinessRule> ruleSet = new HashSet<>();

        BusinessRule discountForElders = new BusinessRule(
            "unsatisfiable rule",
            "order.create",
            // if false
            new Constant<>(false),
            // then order.sum = 500
            new ObjectPropertyAssignment<>("order", "sum", new Constant<>(500))
        );
        ruleSet.add(discountForElders);

        BusinessRuleEvaluator evaluator = new BusinessRuleEvaluator(ruleSet);

        BusinessContext context = new BusinessContext("order.create");
        context.setVariable("order", new Order(BigDecimal.valueOf(1000)));

        evaluator.evaluate(context);

        Assert.assertEquals(
            0,
            BigDecimal.valueOf(1000)
                .compareTo(((Order) context.getVariable("order")).getSum())
        );
    }

    public static class Order
    {

        @Getter
        @Setter
        private BigDecimal sum;

        public Order(final BigDecimal sum)
        {
            this.sum = sum;
        }

    }

    public static class User
    {

        @Getter
        private final String name;

        @Getter
        private final BigDecimal age;

        public User(final String name, final BigDecimal age)
        {
            this.name = name;
            this.age = age;
        }

    }

}
