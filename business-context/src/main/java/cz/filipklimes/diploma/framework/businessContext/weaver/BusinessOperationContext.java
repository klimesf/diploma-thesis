package cz.filipklimes.diploma.framework.businessContext.weaver;

import cz.filipklimes.diploma.framework.businessContext.Function;
import cz.filipklimes.diploma.framework.businessContext.exception.FunctionAlreadyDefinedException;
import cz.filipklimes.diploma.framework.businessContext.exception.UndefinedFunctionException;
import cz.filipklimes.diploma.framework.businessContext.exception.UndefinedVariableException;
import lombok.Getter;

import java.util.*;

public class BusinessOperationContext
{

    @Getter
    private final String name;

    /**
     * Variable input parameters defined within the business context.
     */
    private final Map<String, Object> inputParameters;

    /**
     * Output parameter of the business context
     */
    private Object output;

    /**
     * functions defined within the business context.
     */
    private final Map<String, Function> functions;

    public BusinessOperationContext(final String name)
    {
        this.name = name;
        this.inputParameters = new HashMap<>();
        this.functions = new HashMap<>();
    }

    /**
     * Returns value of variable with the given name.
     *
     * @param name Name of the variable unique within the context.
     * @return Value of the variable.
     * @throws UndefinedVariableException When name of the variable was not defined within the context.
     */
    public Object getInputParameter(final String name)
    {
        if (!inputParameters.containsKey(name)) {
            throw new UndefinedVariableException(name);
        }

        return inputParameters.get(name);
    }

    /**
     * Creates or updates variable with the given name to the given value.
     *
     * @param name Name of the variable unique within the context.
     */
    public void setInputParameter(final String name, final Object value)
    {
        inputParameters.put(name, value);
    }

    /**
     * Returns function with the given name.
     *
     * @param name Name of the function.
     * @return Function with the name.
     * @throws UndefinedFunctionException When function with such name was not defined within the context;
     */
    public Function getFunction(final String name)
    {
        if (!functions.containsKey(name)) {
            throw new UndefinedFunctionException(name);
        }

        return functions.get(name);
    }

    /**
     * Adds named function to the business context.
     *
     * @param name Name of the function.
     * @param function The function body.
     * @throws FunctionAlreadyDefinedException When function with such name was already defined within the context.
     */
    public void addFunction(final String name, final Function function)
    {
        if (functions.containsKey(name)) {
            throw new FunctionAlreadyDefinedException(name);
        }
        functions.put(name, function);
    }

    public Object getOutput()
    {
        return output;
    }

    public void setOutput(final Object output)
    {
        this.output = output;
    }

}
