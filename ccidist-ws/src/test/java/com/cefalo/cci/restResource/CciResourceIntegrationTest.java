package com.cefalo.cci.restResource;

import com.cefalo.cci.utils.XpathUtils;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.junit.Test;
import org.w3c.dom.NodeList;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        ClientResponse clientResponse = ws.accept(MediaType.APPLICATION_XHTML_XML).get(ClientResponse.class);
        String responseHtml = ws.accept(MediaType.APPLICATION_XHTML_XML).get(String.class);
        NodeList nodeList = (NodeList) xpathUtils.getNodeListFromHtml("html/body/ul/li", responseHtml);

        assertEquals(200, clientResponse.getStatus());
        assertNotNull(responseHtml);
        assertEquals(3, nodeList.getLength());

        List<String> actualList= new ArrayList<String>();
        actualList.add("AxelSpringer");
        actualList.add("NHST");
        actualList.add("Polaris");

        List<String> expectedList = new ArrayList<String>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            expectedList.add(nodeList.item(i).getTextContent());
        }

        Collections.sort(expectedList);

        assertEquals(actualList, expectedList);

        ws = resource().path(BASE_URL).path("@#&*//*");
        clientResponse = ws.accept(MediaType.APPLICATION_XHTML_XML).get(ClientResponse.class);

        assertEquals(404, clientResponse.getStatus());

        ws = resource().path(BASE_URL).path("/");
        clientResponse = ws.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
        assertEquals(406, clientResponse.getStatus());
    }

    @Test
    public void getOrganizationDetailTest() {
        ws = resource().path(BASE_URL).path("/polaris$#@");
        ClientResponse notFoundClientResponse = ws.accept(MediaType.APPLICATION_XHTML_XML).get(ClientResponse.class);

        assertEquals(404, notFoundClientResponse.getStatus());

        ws = resource().path(BASE_URL).path("/polaris");
        ClientResponse clientResponse = ws.accept(MediaType.APPLICATION_XHTML_XML).get(ClientResponse.class);
        String responseString = ws.accept(MediaType.APPLICATION_XHTML_XML).get(String.class);
        NodeList nodeList = (NodeList) xpathUtils.getNodeListFromHtml("html/body/ul/li", responseString);

        assertEquals("status found: ", 200, clientResponse.getStatus());
        assertNotNull(responseString);
        assertEquals(2, nodeList.getLength());

        List<String> actualList= Arrays.asList("Addressa", "Harstadtidende");
        List<String> expectedList = new ArrayList<String>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            expectedList.add(nodeList.item(i).getTextContent());
        }
        Collections.sort(expectedList);
        assertEquals(actualList, expectedList);

        ws = resource().path(BASE_URL).path("/polaris");
        clientResponse = ws.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);

        assertEquals("Unsupported MediaType error code: ", 406, clientResponse.getStatus());
    }

    @Test
    public void getPublicationDetailTest() {
        ws = resource().path(BASE_URL).path("/polaris/Addressa");
        ClientResponse clientResponse;

        clientResponse = ws.accept(MediaType.APPLICATION_XHTML_XML).get(ClientResponse.class);
        String responseString = ws.accept(MediaType.APPLICATION_XHTML_XML).get(String.class);
        assertEquals("status found: ", 200, clientResponse.getStatus());
        assertNotNull(responseString);

        ws = resource().path(BASE_URL).path("polaris").path("Addressa");
        clientResponse = ws.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertEquals(406, clientResponse.getStatus());

    }

    @Test
    public void getIssueDetailTest() {
/*
        ws = resource().path(BASE_URL).path("/Polaris/Addressa/accessible_epub_3-20121024/#@$");
        ClientResponse notFoundResponse;
        notFoundResponse = ws.accept(MediaType.APPLICATION_XHTML_XML).get(ClientResponse.class);
        assertEquals(404, notFoundResponse.getStatus());

        ws = resource().path(BASE_URL).path("/Polaris/Alex/accessible_epub_3-20121024/#@$");
        notFoundResponse = ws.accept(MediaType.APPLICATION_XHTML_XML).get(ClientResponse.class);
        assertEquals(404, notFoundResponse.getStatus());
*/

        ws = resource().path(BASE_URL).path("/polaris/addressa/issue/accessible_epub_3-20121024");
        String responseString = ws.accept(MediaType.APPLICATION_XHTML_XML).get(String.class);
        ClientResponse clientResponse = ws.accept(MediaType.APPLICATION_XHTML_XML).get(ClientResponse.class);
        NodeList nodeList;
        nodeList = (NodeList) xpathUtils.getNodeListFromHtml("html/body/ul/li", responseString);

        assertEquals(200, clientResponse.getStatus());
        assertNotNull(responseString);
        assertEquals(2, nodeList.getLength());

        nodeList = (NodeList) xpathUtils.getNodeListFromHtml("html/body/ul/li/a/@href", responseString);
        assertEquals("http://localhost:9998/cciService/polaris/addressa/issue/accessible_epub_3-20121024.epub", nodeList.item(0).getTextContent());
        assertEquals("http://localhost:9998/cciService/polaris/addressa/issue/accessible_epub_3-20121024/META-INF/container.xml", nodeList.item(1).getTextContent());

        ws = resource().path(BASE_URL).path("/polaris/addressa/issue/accessible_epub_3-20121024");
        clientResponse = ws.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertEquals(406, clientResponse.getStatus());
    }

    @Test
    public void getIssueListTest() {
        String organizationName = "polaris";
        String publicationName = "addressa";
        ws = resource().path(BASE_URL).path(organizationName).path(publicationName).path("issue");
        ClientResponse clientResponse = ws.accept(MediaType.APPLICATION_ATOM_XML).get(ClientResponse.class);
        assertEquals("content found error code: ", 200, clientResponse.getStatus());

        clientResponse = ws.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
        assertEquals("Unsupported MediaType error code: ", 406, clientResponse.getStatus());

        String responseString = ws.accept(MediaType.APPLICATION_ATOM_XML).get(String.class);
        assertNotNull(responseString);

        NodeList nodeList = (NodeList) xpathUtils.getNodeListFromHtml("feed/entry", responseString);
        assertEquals("Default number of entry: ", 1, nodeList.getLength());

        //TODO: 404 error should be checked
        /*ws = resource().path(BASE_URL).path("polaris").path("addressa").path("issue");
        clientResponse = ws.accept(MediaType.APPLICATION_ATOM_XML).get(ClientResponse.class);
        assertEquals("Html code not found: ", 404, clientResponse.getStatus());*/

        ws = resource().queryParam("start", "2").queryParam("limit", "8").path(BASE_URL).path("polaris").path("addressa").path("issue");
        responseString= ws.accept(MediaType.APPLICATION_ATOM_XML).get(String.class);
        assertNotNull(responseString);

        nodeList = (NodeList) xpathUtils.getNodeListFromHtml("feed/entry", responseString);
        assertEquals("number of entry start 2: ", 8, nodeList.getLength());

        ws = resource().queryParam("start", "2").path(BASE_URL).path("polaris").path("addressa").path("issue");
        responseString= ws.accept(MediaType.APPLICATION_ATOM_XML).get(String.class);
        assertNotNull(responseString);

        nodeList = (NodeList) xpathUtils.getNodeListFromHtml("feed/entry", responseString);
        assertEquals("number of entry limit default: ", 1, nodeList.getLength());

        ws = resource().queryParam("start", "2").queryParam("limit", "-8").path(BASE_URL).path("polaris").path("addressa").path("issue");
        clientResponse = ws.accept(MediaType.APPLICATION_ATOM_XML).get(ClientResponse.class);
        assertEquals("Bad request 400: ", 400, clientResponse.getStatus());

        ws = resource().queryParam("start", "40").path(BASE_URL).path("polaris").path("addressa").path("issue");
        responseString= ws.accept(MediaType.APPLICATION_ATOM_XML).get(String.class);
        assertNotNull(responseString);
        System.out.println(responseString);

        nodeList = (NodeList) xpathUtils.getNodeListFromHtml("feed/entry", responseString);
        assertEquals("number of entry for problematic start and limit: ", 0, nodeList.getLength());
        nodeList = (NodeList) xpathUtils.getNodeListFromHtml("feed/link", responseString);
        assertEquals("number f links for start = 40 exceeding total number of files: ", 1, nodeList.getLength());
    }

}
