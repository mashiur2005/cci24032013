package com.cefalo.cci.restResource;

import com.cefalo.cci.utils.Utils;
import com.cefalo.cci.utils.XpathUtils;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.junit.Test;
import org.w3c.dom.NodeList;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class CciResourceIntegrationTest extends JerseyTest{
    private static final String BASE_URL = "/cciService";
    private static final String PACKAGE_NAME = "com.cefalo.cci.restResource";

    private WebResource ws;
    private XpathUtils xpathUtils;

    public CciResourceIntegrationTest() {
        super(new WebAppDescriptor.Builder(PACKAGE_NAME).build());
        xpathUtils = new XpathUtils();
    }

    @Test
    public void getOrganizationListTest() {
        ws = resource().path(BASE_URL).path("/");
        ClientResponse clientResponse = ws.accept(MediaType.TEXT_HTML).get(ClientResponse.class);
        String responseHtml = ws.accept(MediaType.TEXT_HTML).get(String.class);
        NodeList nodeList = (NodeList) xpathUtils.getNodeListFromHtml("html/body/ul/li", responseHtml);

        assertEquals(200, clientResponse.getStatus());
        assertNotNull(responseHtml);
        assertEquals(3, nodeList.getLength());

        List<String> actualList= new ArrayList<String>(Utils.ORGANIZATION_DETAILS.keySet());
        List<String> expectedList = new ArrayList<String>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            expectedList.add(nodeList.item(i).getTextContent());
        }

        assertEquals(actualList, expectedList);
    }

    @Test
    public void getOrganizationTest() {
        ws = resource().path(BASE_URL).path("/Polaris/$#@");
        ClientResponse notFoundClientResponse = ws.accept(MediaType.TEXT_HTML).get(ClientResponse.class);

        assertEquals(404, notFoundClientResponse.getStatus());

        ws = resource().path(BASE_URL).path("/Polaris");
        ClientResponse clientResponse = ws.accept(MediaType.TEXT_HTML).get(ClientResponse.class);
        String responseString = ws.accept(MediaType.TEXT_HTML).get(String.class);
        NodeList nodeList = (NodeList) xpathUtils.getNodeListFromHtml("html/body/ul/li", responseString);

        assertEquals(200, clientResponse.getStatus());
        assertNotNull(responseString);
        assertEquals(2, nodeList.getLength());

        List<String> actualList= new ArrayList<String>(Utils.ORGANIZATION_DETAILS.get("Polaris"));
        List<String> expectedList = new ArrayList<String>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            expectedList.add(nodeList.item(i).getTextContent());
        }

        assertEquals(actualList, expectedList);
    }
}
