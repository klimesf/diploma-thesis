package cz.filipklimes.diploma.framework.businessContext.loader;

import cz.filipklimes.diploma.framework.businessContext.BusinessRule;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.kie.api.KieServices;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieContainer;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Loads business rules from local Drools resources.
 */
public class LocalDroolsBusinessContextLoader implements BusinessContextLoader
{

    private final KieContainer kieContainer;

    public LocalDroolsBusinessContextLoader()
    {
        kieContainer = KieServices.Factory.get().getKieClasspathContainer(KieContainer.class.getClassLoader());
    }

    public Set<BusinessRule> load()
    {
        return kieContainer.newStatelessKieSession("rules-session") // TODO: generalize
            .getKieBase().getKiePackages().stream()
            .map(KiePackage::getRules)
            .flatMap(Collection::stream)
            .collect(Collectors.toList())
            .stream()
            .map(LocalDroolsBusinessContextLoader::buildBusinessRule)
            .collect(Collectors.toSet());
    }

    private static BusinessRule buildBusinessRule(final Rule rule)
    {
        RuleImpl ruleImpl = RuleImpl.class.cast(rule);

        BusinessRule.Builder builder = BusinessRule.builder();
        builder.setName(ruleImpl.getName())
            .setBusinessContextName(ruleImpl.getPackageName());

//        ruleImpl.getDeclarations().entrySet().stream()
//            .map(declaration -> new BusinessRuleVariable(declaration.getKey(), declaration.getValue()));


        return builder.build();
    }

}
