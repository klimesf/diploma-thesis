package cz.filipklimes.diploma.centralAdministration.businessContext;

import cz.filipklimes.diploma.centralAdministration.businessContext.exception.CyclicDependencyException;
import cz.filipklimes.diploma.centralAdministration.businessContext.exception.MissingIncludedBusinessContextsException;
import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.BusinessContextIdentifier;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.*;

@RunWith(Enclosed.class)
public class BusinessContextAnalyzerTest
{

    public static class CheckMissingContexts
    {

        @Test
        public void testOk() throws Exception
        {
            Map<BusinessContextIdentifier, BusinessContext> contexts = new HashMap<>();

            BusinessContextIdentifier authLoggedIn = BusinessContextIdentifier.parse("auth.loggedIn");
            contexts.put(
                authLoggedIn,
                BusinessContext.builder()
                    .withIdentifier(authLoggedIn)
                    .build()
            );

            BusinessContextIdentifier userValidEmail = BusinessContextIdentifier.parse("user.validEmail");
            contexts.put(
                userValidEmail,
                BusinessContext.builder()
                    .withIdentifier(userValidEmail)
                    .build()
            );

            BusinessContextIdentifier userCreateEmployee = BusinessContextIdentifier.parse("user.createEmployee");
            contexts.put(
                userCreateEmployee,
                BusinessContext.builder()
                    .withIdentifier(userCreateEmployee)
                    .withIncludedContext(authLoggedIn)
                    .withIncludedContext(userValidEmail)
                    .build()
            );

            BusinessContextAnalyzer.checkMissingContexts(contexts);
        }

        @Test(expected = MissingIncludedBusinessContextsException.class)
        public void testMissing() throws Exception
        {
            Map<BusinessContextIdentifier, BusinessContext> contexts = new HashMap<>();

            BusinessContextIdentifier authLoggedIn = BusinessContextIdentifier.parse("auth.loggedIn");
            BusinessContextIdentifier userValidEmail = BusinessContextIdentifier.parse("user.ValidEmail");
            BusinessContextIdentifier userCreateEmployee = BusinessContextIdentifier.parse("user.CreateEmployee");
            contexts.put(
                userCreateEmployee,
                BusinessContext.builder()
                    .withIncludedContext(authLoggedIn)
                    .withIncludedContext(userValidEmail)
                    .withIdentifier(userCreateEmployee)
                    .build()
            );

            try {
                BusinessContextAnalyzer.checkMissingContexts(contexts);

            } catch (MissingIncludedBusinessContextsException e) {
                Assert.assertEquals(2, e.getIdentifiers().size());
                Assert.assertTrue(e.getIdentifiers().contains(authLoggedIn));
                Assert.assertTrue(e.getIdentifiers().contains(userValidEmail));
                throw e;
            }

        }

    }

    public static class CheckCyclicDependency
    {

        @Test
        public void testOk() throws Exception
        {
            Map<BusinessContextIdentifier, BusinessContext> contexts = new HashMap<>();

            BusinessContextIdentifier authLoggedIn = BusinessContextIdentifier.parse("auth.loggedIn");
            contexts.put(
                authLoggedIn,
                BusinessContext.builder()
                    .withIdentifier(authLoggedIn)
                    .build()
            );

            BusinessContextIdentifier userValidEmail = BusinessContextIdentifier.parse("user.validEmail");
            contexts.put(
                userValidEmail,
                BusinessContext.builder()
                    .withIdentifier(userValidEmail)
                    .build()
            );

            BusinessContextIdentifier userCreateEmployee = BusinessContextIdentifier.parse("user.createEmployee");
            contexts.put(
                userCreateEmployee,
                BusinessContext.builder()
                    .withIdentifier(userCreateEmployee)
                    .withIncludedContext(authLoggedIn)
                    .withIncludedContext(userValidEmail)
                    .build()
            );

            BusinessContextAnalyzer.checkCyclicDependency(contexts);
        }

        @Test(expected = CyclicDependencyException.class)
        public void testFail() throws Exception
        {
            Map<BusinessContextIdentifier, BusinessContext> contexts = new HashMap<>();

            BusinessContextIdentifier authLoggedIn = BusinessContextIdentifier.parse("auth.loggedIn");
            BusinessContextIdentifier userValidEmail = BusinessContextIdentifier.parse("user.validEmail");
            BusinessContextIdentifier userCreateEmployee = BusinessContextIdentifier.parse("user.createEmployee");

            contexts.put(
                authLoggedIn,
                BusinessContext.builder()
                    .withIdentifier(authLoggedIn)
                    .withIncludedContext(userCreateEmployee)
                    .build()
            );

            contexts.put(
                userValidEmail,
                BusinessContext.builder()
                    .withIdentifier(userValidEmail)
                    .withIncludedContext(authLoggedIn)
                    .build()
            );

            contexts.put(
                userCreateEmployee,
                BusinessContext.builder()
                    .withIdentifier(userCreateEmployee)
                    .withIncludedContext(userValidEmail)
                    .build()
            );

            BusinessContextAnalyzer.checkCyclicDependency(contexts);
        }

    }

}
