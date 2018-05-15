# Python library for the business context framework

## Building Docker image

First, make sure you have [Docker](https://www.docker.com/) installed on your computer.
Navigate to the folder and run the following command.

```bash
docker build -t filipklimes-diploma/python .
```

You can now use and extend `filipklimes-diploma/python` Docker image.


## Usage

First, you need to setup your `BusinessContextRegistry`.
You must provide input streams of the XML files where your business contexts are stored (you can for example store them in your classpath):

```python
from business_context_grpc.business_context_client import GrpcRemoteLoader
from business_context_xml.xml_loader import XmlBusinessContextLoader

registry = Registry(
    XmlBusinessContextLoader([
        'path/to/your/business-context.xml',
    ]),
    RemoteBusinessContextLoader(GrpcRemoteLoader({
        'auth': {'host': '0.0.0.0', 'port': 5553}
    }))
)
```

Then, you need to inirialize the weaver and decorate your business operations.
You can use the decorator used in example user service.

```python
from business_context.weaver import *
weaver = Weaver(registry)

class ProductRepository:

    # ...

    @business_operation("product.listAll", weaver)
    def get_all(self) -> List[Product]:
        return list(self.products.values())

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

## Testing

You can test with python's unittest module from this folder

```bash
pip install -r requirements
python -m pytest .
```
