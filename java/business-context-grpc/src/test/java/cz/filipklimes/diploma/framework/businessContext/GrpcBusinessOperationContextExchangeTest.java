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

import static cz.filipklimes.diploma.framework.businessContext.expression.ExpressionType.BOOL;

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
                        .withIncludedContext(authNotLoggedIn)
                        .withIdentifier(userValidEmail)
                        .withPrecondition(
                            Precondition.builder()
                                .withName("userEmailNotEmpty")
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
                        Precondition.builder()
                            .withName("userNotLoggedIn")
                            .withCondition(new Equals<>(
                                new VariableReference<>("loggedIn", BOOL),
                                new Constant<>(false, BOOL)
                            ))
                            .build()
                    )
                    .withPostCondition(
                        PostCondition.builder()
                            .withName("hideUserEmail")
                            .withType(PostConditionType.FILTER_OBJECT_FIELD)
                            .withReferenceName("email")
                            .withCondition(new Constant<>(true, BOOL))
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

        Map<String, Precondition> preconditionMap = context.getPreconditions().stream()
            .collect(Collectors.toMap(
                Precondition::getName,
                Function.identity()
            ));

        Precondition rule1 = preconditionMap.get("userEmailNotEmpty");
        Assert.assertTrue(rule1.getCondition() instanceof IsNotNull);
        IsNotNull condition1 = (IsNotNull) rule1.getCondition();
        Assert.assertTrue(condition1.getArgument() instanceof ObjectPropertyReference);

        Precondition rule2 = preconditionMap.get("userNotLoggedIn");
        Assert.assertTrue(rule2.getCondition() instanceof Equals);

        Map<String, PostCondition> postConditionMap = context.getPostConditions().stream()
            .collect(Collectors.toMap(
                PostCondition::getName,
                Function.identity()
            ));

        PostCondition rule3 = postConditionMap.get("hideUserEmail");
        Assert.assertTrue(rule3.getCondition() instanceof Constant);
        Assert.assertEquals("email", rule3.getReferenceName());
    }

}
