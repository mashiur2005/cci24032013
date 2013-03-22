package com.cefalo.cci.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;

public class XpathUtils {
    private final Logger log = LoggerFactory.getLogger(XpathUtils.class);

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = null;
    Document document = null;

    public XpathUtils() {}
    public XpathUtils(String html) {
        try {
            builder = factory.newDocumentBuilder();
            builder.setEntityResolver(new EntityResolver() {

                @Override
                public InputSource resolveEntity(String publicId, String systemId)
                        throws SAXException, IOException {
                    log.info("Ignoring " + publicId + ", " + systemId);
                    return new InputSource(new StringReader(""));
                }
            });
            document = builder.parse(new ByteArrayInputStream(html.getBytes()));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public NodeList getNodeListFromHtml(String expression) {
        NodeList nodeList = null;
        XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            nodeList = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            log.info("Xpath parsing error!!!");
        }
        return nodeList;
    }

    public String parseNodeValue(String pattern) {
        NodeList nodeList = getNodeListFromHtml(pattern);

        if (nodeList != null) {
            Node node = nodeList.item(0);
            return node.getTextContent();
        }
        return "";
    }
}
