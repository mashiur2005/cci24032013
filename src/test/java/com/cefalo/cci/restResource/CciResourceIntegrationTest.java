package com.cefalo.cci.restResource;

import com.cefalo.cci.testUtils.TestHelper;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.junit.Test;
import org.w3c.dom.NodeList;

import javax.ws.rs.core.MediaType;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class CciResourceIntegrationTest extends JerseyTest{
    private static final String BASE_URL = "/cciService";
    private static final String PACKAGE_NAME = "com.cefalo.cci.restResource";

    private WebResource ws;

    public CciResourceIntegrationTest() {
        super(new WebAppDescriptor.Builder(PACKAGE_NAME).build());
    }

    @Test
    public void getOrganizationListTest() {
        ws = resource().path(BASE_URL).path("/");
        ClientResponse clientResponse = ws.accept(MediaType.TEXT_HTML).get(ClientResponse.class);
        String responseString = ws.accept(MediaType.TEXT_HTML).get(String.class);
        NodeList nodeList = (NodeList) TestHelper.getNodeList("//li", responseString);

        assertEquals(200, clientResponse.getStatus());
        assertNotNull(responseString);
        assertEquals(3, nodeList.getLength());
    }

    @Test
    public void getOrganizationTest() {
        ws = resource().path(BASE_URL).path("/Polaris");
        ClientResponse clientResponse = ws.accept(MediaType.TEXT_HTML).get(ClientResponse.class);
        String responseString = ws.accept(MediaType.TEXT_HTML).get(String.class);
        NodeList nodeList = (NodeList) TestHelper.getNodeList("//li", responseString);

        assertEquals(200, clientResponse.getStatus());
        assertNotNull(responseString);
        assertEquals(2, nodeList.getLength());
    }
}
