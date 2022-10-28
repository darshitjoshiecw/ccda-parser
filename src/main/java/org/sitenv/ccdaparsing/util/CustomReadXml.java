package org.sitenv.ccdaparsing.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Stack;

public class CustomReadXml {
    public static Document readXML(String xml, final String[] templateIds) throws IOException, SAXException {
        final Document doc;
        SAXParser parser;
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            parser = factory.newSAXParser();
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            doc = docBuilder.newDocument();
        } catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException("Can't create SAX parser / DOM builder.", e);
        }

        Reader reader = new StringReader(xml);
        InputSource inputsource = new InputSource(reader);
        inputsource.setEncoding("UTF-8");
        final Stack<Element> elementStack = new Stack();
        final StringBuilder textBuffer = new StringBuilder();
        DefaultHandler handler = new DefaultHandler() {
            private Locator locator;
            boolean processSection = true;

            @Override
            public void setDocumentLocator(Locator locator) {
                this.locator = locator;
            }

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) {
                if (processSectionBasedOnTemplateId(elementStack)) {
                    this.addTextIfNeeded();
                    Element el = doc.createElement(qName);

                    for (int i = 0; i < attributes.getLength(); ++i) {
                        el.setAttribute(attributes.getQName(i), attributes.getValue(i));
                    }

                    el.setUserData("lineNumber", String.valueOf(this.locator.getLineNumber()), null);
                    elementStack.push(el);
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) {
                if(processSectionBasedOnTemplateId(elementStack)){
                    this.addTextIfNeeded();
                    Element closedEl = elementStack.pop();
                    if (elementStack.isEmpty()) {
                        doc.appendChild(closedEl);
                    } else {
                        Element parentEl = elementStack.peek();
                        parentEl.appendChild(closedEl);
                    }
                    closedEl.setUserData("endLineNumber", String.valueOf(this.locator.getLineNumber()), null);

                } else if("section".equalsIgnoreCase(qName)) {
                    processSection = true;
                    textBuffer.delete(0, textBuffer.length());
                    Element closedEl = elementStack.pop();
                    Element parentEl = elementStack.peek();
                    parentEl.appendChild(closedEl);
                }
            }

            private boolean processSectionBasedOnTemplateId(Stack<Element> elementStack) {
                int elementSize = elementStack.size()-1;
                if (!elementStack.isEmpty() &&
                        "templateId".equalsIgnoreCase(elementStack.get(elementSize).getTagName()) &&
                        "section".equalsIgnoreCase(elementStack.get(elementSize-1).getTagName())) {
                    processSection = (templateIds[0].equalsIgnoreCase(elementStack.get(elementSize).getAttribute("root")) ||
                            templateIds[1].equalsIgnoreCase(elementStack.get(elementSize).getAttribute("root")));
                    if(!processSection) {
                        elementStack.pop();
                    }
                }
                return processSection;
            }

            @Override
            public void characters(char[] ch, int start, int length) {
                textBuffer.append(ch, start, length);
            }

            private void addTextIfNeeded() {
                if (textBuffer.length() > 0) {
                    Element el = elementStack.peek();
                    Node textNode = doc.createTextNode(textBuffer.toString());
                    el.appendChild(textNode);
                    textBuffer.delete(0, textBuffer.length());
                }

            }
        };
        parser.parse(inputsource, handler);
        return doc;
    }
}
