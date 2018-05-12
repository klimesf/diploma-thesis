package cz.filipklimes.diploma.framework.businessContext.expression;

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

public interface ExpressionVisitor
{

    void visit(Add add);

    void visit(GreaterOrEqualTo greaterOrEqualTo);

    void visit(Divide divide);

    void visit(GreaterThan greaterThan);

    void visit(LessOrEqualTo lessOrEqualTo);

    void visit(LessThan lessThan);

    void visit(Multiply multiply);

    void visit(Subtract subtract);

    void visit(And and);

    void visit(Equals<?, ?> equals);

    void visit(Negate negate);

    void visit(Or or);

    void visit(Constant<?> constant);

    void visit(FunctionCall<?> functionCall);

    void visit(IsNotNull<?> isNotNull);

    void visit(IsNotBlank isNotBlank);

    void visit(ObjectPropertyReference<?> objectPropertyReference);

    void visit(VariableReference<?> variableReference);

}
