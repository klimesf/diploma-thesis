# Node.js library for the business context framework

## Building Docker image

First, make sure you have [Docker](https://www.docker.com/) installed on your computer.
Navigate to the **project root folder** and run the following command. (this is because we need to link files from `proto/` folder)

```bash
docker build -f nodejs/Dockerfile -t filipklimes-diploma/nodejs .
```

You can now use and extend `filipklimes-diploma/nodejs` Docker image.

## Usage

First, you need to setup your `BusinessContextRegistry`.
You must provide input streams of the XML files where your business contexts are stored (you can for example store them in your classpath):

```javascript
const registry = new BusinessContextRegistry(
    new XmlReader([
        'path/to/your/business-context.xml',
    ]),
    {
        loadContextsByIdentifier: (identifiers) => {
            return {}
        }
    }
)
```

Then, you need to inirialize the weaver and create decorator for your business operations.
You can use the decorator from the example user service.

```javascript
const BusinessContextWeaver = require('business-context-framework/dist/weaver/BusinessContextWeaver').default,
    BusinessOperationContext = require('business-context-framework/dist/weaver/BusinessOperationContext').default,
    weaver = new BusinessContextWeaver(registry)

const wrapCall = (context, func) => {
    return new Promise(async (resolve, reject) => {
        try {
            await weaver.evaluatePreconditions(context)
            console.log("# Evaluated preconditions of " + context.name)
            resolve()
        } catch (error) {
            console.error(error.getMessage())
            reject(error.getMessage())
        }
    })
        .then(_ => func())
        .then(async result => {
            context.setOutput(result)
            await weaver.applyPostConditions(context)
            console.log("# Applied post-conditions of " + context.name)
            return new Promise((resolve, reject) => resolve(context.getOutput()))
        })
}
```

Now, you can decorate your business operations:

```javascript
exports.register = (name, email) => {
    const context = new BusinessOperationContext('user.register')
    context.setInputParameter('name', name)
    context.setInputParameter('email', email)
    return wrapCall(context, () => register(name, email))
}
```

Finally, you can describe your business contexts in the XML and place them wherever you wish, as long as you provide the paths to the registry:

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
