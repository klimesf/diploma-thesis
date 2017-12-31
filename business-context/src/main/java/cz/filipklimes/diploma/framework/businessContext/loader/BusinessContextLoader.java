package cz.filipklimes.diploma.framework.businessContext.loader;

import cz.filipklimes.diploma.framework.businessContext.BusinessRule;

import java.util.*;

public interface BusinessContextLoader
{

    Set<BusinessRule> load();

}
