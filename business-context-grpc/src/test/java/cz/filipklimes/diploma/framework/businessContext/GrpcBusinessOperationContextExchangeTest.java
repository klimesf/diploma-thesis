package cz.filipklimes.diploma.framework.businessContext;

import cz.filipklimes.diploma.framework.businessContext.expression.Constant;
import cz.filipklimes.diploma.framework.businessContext.expression.ExpressionType;
import cz.filipklimes.diploma.framework.businessContext.expression.IsNotNull;
import cz.filipklimes.diploma.framework.businessContext.expression.ObjectPropertyReference;
import cz.filipklimes.diploma.framework.businessContext.expression.VariableReference;
import cz.filipklimes.diploma.framework.businessContext.expression.logical.Equals;
import cz.filipklimes.diploma.framework.businessContext.loader.LocalBusinessContextLoader;
import cz.filipklimes.diploma.framework.businessContext.loader.RemoteBusinessContextLoader;
import cz.filipklimes.diploma.framework.businessContext.loader.remote.RemoteLoader;
import cz.filipklimes.diploma.framework.businessContext.loader.remote.RemoteServiceAddress;
import cz.filipklimes.diploma.framework.businessContext.loader.remote.grpc.GrpcRemoteLoader;
import cz.filipklimes.diploma.framework.businessContext.provider.server.grpc.GrpcBusinessContextServer;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GrpcBusinessOperationContextExchangeTest
{

    @Test
    public void test()
    {
        final BusinessContextIdentifier authNotLoggedIn = new BusinessContextIdentifier("auth", "notLoggedIn");
        final BusinessContextIdentifier userValidEmail = new BusinessContextIdentifier("user", "validEmail");

        BusinessContextRegistry.Builder builder = BusinessContextRegistry.builder();
        builder.withLocalLoader(new LocalBusinessContextLoader()
        {
            @Override
            public Set<BusinessContext> load()
            {
                return Collections.singleton(
                    BusinessContext.builder()
                        .withIdentifier(userValidEmail)
                        .withPrecondition(
                            BusinessRule.builder()
                                .withName("userEmailNotEmpty")
                                .withType(BusinessRuleType.PRECONDITION)
                                .withCondition(new IsNotNull<>(new ObjectPropertyReference<>("user", "email", ExpressionType.STRING)))
                                .build()
                        )
                        .build()
                );
            }
        });

        Map<String, RemoteLoader> remoteLoaders = new HashMap<>();
        remoteLoaders.put("auth", identifiers -> {
            Map<BusinessContextIdentifier, BusinessContext> contexts = new HashMap<>();
            contexts.put(
                authNotLoggedIn,
                BusinessContext.builder()
                    .withIdentifier(authNotLoggedIn)
                    .withPrecondition(
                        BusinessRule.builder()
                            .withName("userNotLoggedIn")
                            .withType(BusinessRuleType.PRECONDITION)
                            .withCondition(new Equals<>(
                                new VariableReference<>("loggedIn", ExpressionType.BOOL),
                                new Constant<>(false, ExpressionType.BOOL)
                            ))
                            .build()
                    )
                    .build()
            );

            return identifiers.stream()
                .map(contexts::get)
                .collect(Collectors.toSet());
        });
        builder.withRemoteLoader(new RemoteBusinessContextLoader(remoteLoaders));

        BusinessContextRegistry registry = builder.build();

        GrpcBusinessContextServer server = new GrpcBusinessContextServer(registry, 5555);
        Thread t = new Thread(server);
        t.start();

        Set<BusinessContextIdentifier> identifiers = new HashSet<>();
        identifiers.add(userValidEmail);

        GrpcRemoteLoader client = new GrpcRemoteLoader(new RemoteServiceAddress("user", "localhost", 5555));
        Set<BusinessContext> contexts = client.loadContexts(identifiers);

        t.interrupt();
        Assert.assertEquals(1, contexts.size());

        BusinessContext context = contexts.iterator().next();
        Assert.assertEquals(2, context.getPreconditions().size());

        Map<String, BusinessRule> preConditionMap = context.getPreconditions().stream()
            .collect(Collectors.toMap(
                BusinessRule::getName,
                Function.identity()
            ));

        BusinessRule rule1 = preConditionMap.get("userEmailNotEmpty");
        Assert.assertTrue(rule1.getCondition() instanceof IsNotNull);
        IsNotNull condition1 = (IsNotNull) rule1.getCondition();
        Assert.assertTrue(condition1.getArgument() instanceof ObjectPropertyReference);

        BusinessRule rule2 = preConditionMap.get("userNotLoggedIn");
        Assert.assertTrue(rule2.getCondition() instanceof Equals);
    }

}
