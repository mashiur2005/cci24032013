package com.cefalo.cci.unitTest;

import com.cefalo.cci.utils.XpathUtils;
import com.google.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(GuiceJUnitRunner.class)
@GuiceJUnitRunner.GuiceModules({ServicesTestModule.class })
public class XpathUtilsUnitTest {
    @Inject
    private XpathUtils xpathUtils;

    @Test
    public void getNodeListFromHtmlTest() {
        String testHtml = fileRead("/test.html");
        Map<String, String> nameSpaceMap = new HashMap<String, String>();
        nameSpaceMap.put("h", "http://www.w3.org/1999/xhtml");

        String expression = "h:html/h:body/h:table[@id='table']/h:tr[@id='tr1']/h:ul[@class='organizations']/h:li";
        NodeList nodeList = (NodeList) xpathUtils.getNodeListFromHtml(expression, testHtml, nameSpaceMap);
        assertEquals(3, nodeList.getLength());

        String linkExpression = "h:html/h:body/h:table[@id='table']/h:tr[@id='tr2']/h:ul[@class='organizations']/h:li";
        NodeList linkNodeList = (NodeList) xpathUtils.getNodeListFromHtml(linkExpression, testHtml, nameSpaceMap);
        assertEquals(4, linkNodeList.getLength());

        String emptyCheck = "h:html/h:body/h:table[@id='table1']/h:tr[@id='tr2']/h:ul[@class='organizations']/h:li";
        NodeList emptyList = (NodeList) xpathUtils.getNodeListFromHtml(emptyCheck, testHtml, nameSpaceMap);
        assertEquals(0, emptyList.getLength());

        String withoutNameSpaceHtml = fileRead("/testWithoutNameSpaceTest.html");

        String exp = "html/h:body/h:table[@id='table']/h:tr[@id='tr1']/h:ul[@class='organizations']/h:li";
        NodeList nList = (NodeList) xpathUtils.getNodeListFromHtml(exp, withoutNameSpaceHtml, nameSpaceMap);
        assertEquals(3, nList.getLength());

    }

    public String fileRead(String fileName) {
        int ch;
        StringBuffer strContent = new StringBuffer("");
        try {
            InputStream inputStream = this.getClass().getResourceAsStream(fileName);
            while ((ch = inputStream.read()) != -1) {
                strContent.append((char) ch);
            }
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strContent.toString();
    }
}
