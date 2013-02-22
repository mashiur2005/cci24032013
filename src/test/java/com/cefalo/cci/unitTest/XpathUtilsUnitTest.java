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
    @Inject
    private XpathUtils xpathUtils;

    @Test
    public void getNodeListFromHtmlTest() {
        String fileStr = readFileAsString("/test.html");

        String expression = "html/body/table[@id='table']/tr[@id='tr1']/ul[@class='organizations']/li";
        NodeList nodeList = (NodeList) xpathUtils.getNodeListFromHtml(expression, fileStr);
        assertEquals(3, nodeList.getLength());

        String linkExpression = "html/body/table[@id='table']/tr[@id='tr2']/ul[@class='organizations']/li";
        NodeList linkNodeList = (NodeList) xpathUtils.getNodeListFromHtml(linkExpression, fileStr);
        assertEquals(4, linkNodeList.getLength());

        String emptyCheck = "html/body/table[@id='table']/tr[@id='tr3']/ul[@class='organizations']/li";
        NodeList emptyList = (NodeList) xpathUtils.getNodeListFromHtml(emptyCheck, fileStr);
        assertEquals(0, emptyList.getLength());

        String exceptNameSpcStr = readFileAsString("/testWithoutNameSpaceTest.html");
        String exceptNameSpcExp = "html/body/table[@id='table']/tr[@id='tr1']/ul[@class='organizations']/li";
        NodeList exceptNameSpcNodeList = (NodeList) xpathUtils.getNodeListFromHtml(exceptNameSpcExp, exceptNameSpcStr);
        assertEquals(3, exceptNameSpcNodeList.getLength());

        String atomFileStr = readFileAsString("/atom.xml");
        String atomFileLinkExp = "feed/link";
        NodeList atomLinkNodeList = (NodeList) xpathUtils.getNodeListFromHtml(atomFileLinkExp, atomFileStr);
        assertEquals(4, atomLinkNodeList.getLength());

        atomFileLinkExp = "feed/link/@rel";
        atomLinkNodeList = (NodeList) xpathUtils.getNodeListFromHtml(atomFileLinkExp, atomFileStr);
        assertEquals(4, atomLinkNodeList.getLength());

        String atomFileEntryExp = "feed/entry";
        NodeList atomEntryNodeList = (NodeList) xpathUtils.getNodeListFromHtml(atomFileEntryExp, atomFileStr);
        assertEquals(3, atomEntryNodeList.getLength());

        String publicationXhtml = readFileAsString("/publication.xhtml");
        String deviceTypeExp = "html/body/dl/dd/ul/li";
        NodeList deviceNodeList= (NodeList) xpathUtils.getNodeListFromHtml(deviceTypeExp, publicationXhtml);
        assertEquals(3, deviceNodeList.getLength());
        String linkTemplateExp = "html/body/dl/dd/link-template";
        NodeList linkTemplateNodeList= (NodeList) xpathUtils.getNodeListFromHtml(linkTemplateExp, publicationXhtml);
        assertEquals(1, linkTemplateNodeList.getLength());

        String packageXhtml = readFileAsString("/package.xhtml");
        String itemXpathExp = "package/manifest/item";
        NodeList itemNodeList = (NodeList) xpathUtils.getNodeListFromHtml(itemXpathExp, packageXhtml);
        assertEquals(9, itemNodeList.getLength());

        itemXpathExp = "package/spine[@toc='ncx']/itemref/@idref";
        itemNodeList = (NodeList) xpathUtils.getNodeListFromHtml(itemXpathExp, packageXhtml);
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
