from xml.dom.minidom import getDOMImplementation, Document
from business_context.context import BusinessContext
from business_context.identifier import Identifier


def write_xml(context: BusinessContext) -> Document:
    impl = getDOMImplementation()
    doc = impl.createDocument(None, "some_tag", None)
    top_element = doc.documentElement
    text = doc.createTextNode('Some textual content.')
    top_element.appendChild(text)
