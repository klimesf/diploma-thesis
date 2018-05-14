"use strict";

const chai = require('chai')

const BusinessContextRegistry = require('business-context-framework/dist/BusinessContextRegistry').default
const BusinessContext = require('business-context-framework/dist/BusinessContext').default
const BusinessContextIdentifier = require('business-context-framework/dist/BusinessContextIdentifier').default
const Precondition = require('business-context-framework/dist/Precondition').default
const PostCondition = require('business-context-framework/dist/PostCondition').default
const PostConditionType = require('business-context-framework/dist/PostConditionType').default
const IsNotNull = require('business-context-framework/dist/expression/IsNotNull').default
const ExpressionType = require('business-context-framework/dist/expression/ExpressionType').default
const VariableReference = require('business-context-framework/dist/expression/VariableReference').default
const Constant = require('business-context-framework/dist/expression/Constant').default

import XmlReader from './../dist/XmlReader'

chai.should()

const xml = `
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
</businessContext>`


describe('XmlReader', () => {
    it('reads business context from xml', () => {
        const context = XmlReader.read(xml)
        context.identifier.prefix.should.equal("user")
        context.identifier.name.should.equal("createEmployee")

        context.includedContexts.size.should.equal(2)
    })
})
