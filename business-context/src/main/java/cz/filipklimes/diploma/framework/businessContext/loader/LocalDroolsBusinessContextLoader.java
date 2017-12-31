package cz.filipklimes.diploma.framework.businessContext.loader;

import cz.filipklimes.diploma.framework.businessContext.BusinessRule;
import org.kie.api.KieServices;
import org.kie.api.definition.KiePackage;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;

import java.util.*;
import java.util.stream.Collectors;

public class LocalDroolsBusinessContextLoader implements BusinessContextLoader
{

    private final KieContainer kieContainer;

    public LocalDroolsBusinessContextLoader()
    {
        kieContainer = KieServices.Factory.get().getKieClasspathContainer(KieContainer.class.getClassLoader());
    }

    public Set<BusinessRule> load()
    {
        Set<BusinessRule> rules = new HashSet<>();
        StatelessKieSession session = kieContainer.newStatelessKieSession("rules-session");// TODO: generalize
        Collection<KiePackage> packages = session.getKieBase().getKiePackages();

        return packages.stream()
            .map(KiePackage::getRules)
            .flatMap(Collection::stream)
            .collect(Collectors.toList())
            .stream()
            .map(rule -> new BusinessRule(rule.getName(), rule.getPackageName()))
            .collect(Collectors.toSet());
    }

}
