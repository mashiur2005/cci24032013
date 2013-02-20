package com.cefalo.cci.utils;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;

public class XpathUtils {

    public NodeList getNodeListFromHtml(String expression, String html, Map<String, String> nameSpaces) {
        NodeList nodeList = null;
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new MyNamespaceContext(nameSpaces));
        try {
            nodeList = (NodeList) xpath.evaluate(expression, new InputSource(new StringReader(html)), XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            System.out.println("Xpath parsing error!!!");
        }
        return nodeList;
    }
    private static class MyNamespaceContext implements NamespaceContext {

        private Map<String, String> nameSpaces;
        public MyNamespaceContext(Map<String, String> nameSpaces) {
            this.nameSpaces = nameSpaces;
        }

        @Override
        public String getNamespaceURI(String prefix) {
            if (nameSpaces.containsKey(prefix)) {
                return nameSpaces.get(prefix);
            } else {
                return XMLConstants.NULL_NS_URI;
            }
        }

        @Override
        public String getPrefix(String namespaceURI) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Iterator getPrefixes(String namespaceURI) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

}
