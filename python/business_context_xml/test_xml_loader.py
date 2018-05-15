import unittest, os
from business_context_xml.xml_loader import load_xml, XmlBusinessContextLoader
from business_context.identifier import Identifier

document = """\
<?xml version="1.0" encoding="UTF-8"?>
<businessContext prefix="user" name="createEmployee">
  <includedContexts>
    <includedContext prefix="user" name="validEmail" />
    <includedContext prefix="auth" name="loggedIn" />
  </includedContexts>
  <preconditions>
    <precondition name="true">
      <condition>
        <isNotNull>
          <argument>
            <variableReference name="email" type="string" />
          </argument>
        </isNotNull>
      </condition>
    </precondition>
  </preconditions>
  <postConditions>
    <postCondition name="hideUserEmail">
      <type>FILTER_OBJECT_FIELD</type>
      <referenceName>email</referenceName>
      <condition>
        <constant type="bool" value="true" />
      </condition>
    </postCondition>
  </postConditions>
</businessContext>
"""


class XmlLoaderTest(unittest.TestCase):
    def test(self):
        business_context = load_xml(document)

        # Identifier
        self.assertEquals("user", business_context.identifier.prefix)
        self.assertEquals("createEmployee", business_context.identifier.name)

        # Included contexts
        self.assertEquals(2, len(business_context.included_contexts))
        self.assertTrue(business_context.included_contexts.__contains__(Identifier("auth", "loggedIn")))
        self.assertTrue(business_context.included_contexts.__contains__(Identifier("user", "validEmail")))

        # Preconditions
        self.assertEquals(1, len(business_context.preconditions))

        # Post-conditions
        self.assertEquals(1, len(business_context.post_conditions))

    def test_load_file(self):
        file_path = os.path.dirname(__file__)
        loader = XmlBusinessContextLoader([file_path + '/contexts/test.xml'])
        contexts = loader.load()
        self.assertEquals(1, len(contexts))
