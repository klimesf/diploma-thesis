package cz.filipklimes.diploma.framework.businessContext.expression;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.Function;

import java.util.*;

public class FunctionCall<T> implements Expression<T>
{

    private final String methodName;
    private final Class<T> type;
    private final Expression<?>[] arguments;

    public FunctionCall(final String methodName, final Class<T> type, final Expression<?>... arguments)
    {
        this.methodName = methodName;
        this.type = type;
        this.arguments = arguments.clone();
    }

    @Override
    public T interpret(final BusinessContext context)
    {
        Function function = context.getFunction(methodName);

        Object[] calculatedArguments = Arrays.stream(arguments)
            .map(argument -> argument.interpret(context))
            .toArray();

        return type.cast(function.execute(calculatedArguments));
    }

}
