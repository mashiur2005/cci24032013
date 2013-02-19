package com.cefalo.cci.testUtils;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

public class TestHelper {

    public static NodeList getNodeList(String expression, String responseString) {
        NodeList nodeList = null;
        final XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            nodeList = (NodeList) xpath.evaluate(expression, new InputSource(new StringReader(responseString)), XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            System.out.println("Xpath parsing error!!!");
        }
        return nodeList;
    }

}
