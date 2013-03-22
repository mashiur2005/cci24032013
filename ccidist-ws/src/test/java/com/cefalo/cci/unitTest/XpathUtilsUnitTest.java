package com.cefalo.cci.unitTest;

import com.cefalo.cci.utils.XpathUtils;
import com.google.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.NodeList;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;

@RunWith(GuiceJUnitRunner.class)
@GuiceJUnitRunner.GuiceModules({ServicesTestModule.class })
public class XpathUtilsUnitTest {

    @Test
    public void getNodeListFromHtmlTest() {
        String fileStr = readFileAsString("/test.html");
        XpathUtils xpathUtils = new XpathUtils(fileStr);
        String expression = "html/body/table[@id='table']/tr[@id='tr1']/td/ul[@class='organizations']/li";
        NodeList nodeList = (NodeList) xpathUtils.getNodeListFromHtml(expression);
        assertEquals(3, nodeList.getLength());

        String linkExpression = "html/body/table[@id='table']/tr[@id='tr2']/td/ul[@class='organizations']/li";
        NodeList linkNodeList = (NodeList) xpathUtils.getNodeListFromHtml(linkExpression);
        assertEquals(4, linkNodeList.getLength());

        String emptyCheck = "html/body/table[@id='table']/tr[@id='tr3']/td/ul[@class='organizations']/li";
        NodeList emptyList = (NodeList) xpathUtils.getNodeListFromHtml(emptyCheck);
        assertEquals(0, emptyList.getLength());

        String exceptNameSpcStr = readFileAsString("/testWithoutNameSpaceTest.html");
        String exceptNameSpcExp = "html/body/table[@id='table']/tr[@id='tr1']/td/ul[@class='organizations']/li";
        xpathUtils = new XpathUtils(exceptNameSpcStr);
        NodeList exceptNameSpcNodeList = (NodeList) xpathUtils.getNodeListFromHtml(exceptNameSpcExp);
        assertEquals(3, exceptNameSpcNodeList.getLength());

        String atomFileStr = readFileAsString("/atom.xml");
        String atomFileLinkExp = "feed/link";
        xpathUtils = new XpathUtils(atomFileStr);
        NodeList atomLinkNodeList = (NodeList) xpathUtils.getNodeListFromHtml(atomFileLinkExp);
        assertEquals(4, atomLinkNodeList.getLength());

        atomFileLinkExp = "feed/link/@rel";
        atomLinkNodeList = (NodeList) xpathUtils.getNodeListFromHtml(atomFileLinkExp);
        assertEquals(4, atomLinkNodeList.getLength());

        String atomFileEntryExp = "feed/entry";
        NodeList atomEntryNodeList = (NodeList) xpathUtils.getNodeListFromHtml(atomFileEntryExp);
        assertEquals(3, atomEntryNodeList.getLength());

        String publicationXhtml = readFileAsString("/publication.xhtml");
        String deviceTypeExp = "html/body/dl/dd/ul/li";
        xpathUtils = new XpathUtils(publicationXhtml);
        NodeList deviceNodeList= (NodeList) xpathUtils.getNodeListFromHtml(deviceTypeExp);
        assertEquals(3, deviceNodeList.getLength());
        String linkTemplateExp = "html/body/dl/dd/link-template";
        NodeList linkTemplateNodeList= (NodeList) xpathUtils.getNodeListFromHtml(linkTemplateExp);
        assertEquals(1, linkTemplateNodeList.getLength());

        String packageXhtml = readFileAsString("/package.xhtml");
        String itemXpathExp = "package/manifest/item";
        xpathUtils = new XpathUtils(packageXhtml);
        NodeList itemNodeList = (NodeList) xpathUtils.getNodeListFromHtml(itemXpathExp);
        assertEquals(9, itemNodeList.getLength());

        itemXpathExp = "package/spine[@toc='ncx']/itemref/@idref";
        itemNodeList = (NodeList) xpathUtils.getNodeListFromHtml(itemXpathExp);
        assertEquals(4, itemNodeList.getLength());
    }

    public String readFileAsString(String fileName) {
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
