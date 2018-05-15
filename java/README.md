# Java library for the business context framework

## Installing locally with Maven

First, make sure you have [Maven](https://maven.apache.org/) instaled on your computer.
Then, navigate to this folder and run the following command.

```bash
mvn clean install
```

You can build projects with dependency on the library.

## Usage

First, you need to setup your `BusinessContextRegistry`.
You must provide input streams of the XML files where your business contexts are stored (you can for example store them in your classpath):

```java
List<InputStream> streams = new ArrayList<>();
streams.add(Application.class.getResourceAsStream("/business-contexts/create.xml"));
// ...

BusinessContextRegistry registry = BusinessContextRegistry.builder()
    .withLocalLoader(new BusinessContextXmlLoader(streams))
    .withRemoteLoader(new RemoteBusinessContextLoader(remoteLoaders))
    .build();
```

Then, you need to register AspectJ with your DI container. This step is specific to the framework you use.

Now, you can annotate your business operations and their parameters:

```java
package com.example;

import cz.filipklimes.diploma.framework.businessContext.annotation.BusinessOperation;
import cz.filipklimes.diploma.framework.businessContext.annotation.BusinessOperationParameter;
import cz.filipklimes.diploma.framework.example.order.business.Address;
import cz.filipklimes.diploma.framework.example.order.business.Order;
import cz.filipklimes.diploma.framework.example.order.business.User;

import java.util.*;

public class OrderService
{

    @BusinessOperation("order.create")
    public Order create(
        @BusinessOperationParameter("user") final User user,
        @BusinessOperationParameter("email") final String email,
        @BusinessOperationParameter("shippingAddress") final Address shippingAddress,
        @BusinessOperationParameter("billingAddress") final Address billingAddress
    )
    {
        return new Order(user, billingAddress, shippingAddress);
    }

}

```

Finally, describe your business contexts in the XML and place them in `src/main/resources/business-contexts/` folder:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<businessContext prefix="order" name="create"
                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 xsi:noNamespaceSchemaLocation="businessContext.xsd">
    <includedContexts />
    <preconditions>
        <precondition name="Email is not empty">
            <condition>
                <isNotBlank>
                    <argument>
                        <variableReference propertyName="email" type="string" />
                    </argument>
                </isNotBlank>
            </condition>
        </precondition>
    </preconditions>
    <postConditions />
</businessContext>
```
