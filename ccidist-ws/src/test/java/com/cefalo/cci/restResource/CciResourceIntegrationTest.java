package com.cefalo.cci.restResource;

import com.cefalo.cci.utils.XpathUtils;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.junit.Test;
import org.w3c.dom.NodeList;

import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
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

        /*TODO: here integration test is done properly but commented as "If-Modified-Since" header is checked against
         TODO: updated field in database which is dependent on local machine that's why integration test may fail dependent on database*/
        /*ws = resource().path(BASE_URL).path("/");
        DateTime date = new DateTime();
        date.minusYears(1);
        clientResponse = ws.header("If-Modified-Since", date).header("If-None-Match", "\"" + date.toDate().getTime() + "\"").accept(MediaType.APPLICATION_XHTML_XML).get(ClientResponse.class);
        assertEquals("Conditional Get Based on If-Modified-Since and If-None-Match: ", 200, clientResponse.getStatus());

        ws = resource().path(BASE_URL).path("/");
        DateTime modifiedDay = new DateTime(2013, 2, 27, 15, 23, 50);
        clientResponse = ws.header("If-Modified-Since", modifiedDay.toDate()).header("If-None-Match", "\"" + modifiedDay.toDate().getTime() + "\"").accept(MediaType.APPLICATION_XHTML_XML).get(ClientResponse.class);
        assertEquals("Conditional Get Based on Last-Modified: ", 304, clientResponse.getStatus());*/
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

        /*TODO: here integration test is done properly but commented as "If-Modified-Since" header is checked against
         TODO: updated field in database which is dependent on local machine that's why integration test may fail dependent on database*/
        /*ws = resource().path(BASE_URL).path("/polaris");
        DateTime modifiedDay = new DateTime(2013, 2, 27, 15, 23, 50);
        clientResponse = ws.header("If-Modified-Since", modifiedDay.toDate()).accept(MediaType.APPLICATION_XHTML_XML).get(ClientResponse.class);
        assertEquals("If-Modified-Since matched ", 304, clientResponse.getStatus());

        ws = resource().path(BASE_URL).path("/polaris");
        clientResponse = ws.header("If-Modified-Since", modifiedDay.toDate()).header("If-None-Match", "\"" + "1" + "\"").accept(MediaType.APPLICATION_XHTML_XML).get(ClientResponse.class);
        assertEquals("If-Modified-Since matched and If-None-Match mismatched ", 200, clientResponse.getStatus());
        ws = resource().path(BASE_URL).path("/polaris");
        DateTime dateBefore = new DateTime(2013, 2, 28, 15, 23, 50);
        clientResponse = ws.header("If-None-Match", "\"" + "0" + "\"").header("If-Modified-Since", dateBefore.toDate()).accept(MediaType.APPLICATION_XHTML_XML).get(ClientResponse.class);
        assertEquals("If-None-Match matched and If-Modified-Since greater......." + clientResponse.getLastModified() + " test...." + dateBefore.toDate(), 304, clientResponse.getStatus());

        ws = resource().path(BASE_URL).path("/polaris");
        clientResponse = ws.header("If-None-Match", "\"" + "1" + "\"").header("If-Modified-Since", dateBefore.toDate()).accept(MediaType.APPLICATION_XHTML_XML).get(ClientResponse.class);
        assertEquals("If-None-Match mismatched and If-Modified-Since greater......." + clientResponse.getLastModified() + " test...." + dateBefore.toDate(), 200, clientResponse.getStatus());*/

        ws = resource().path(BASE_URL).path("/polaris");
        clientResponse = ws.header("If-None-Match", "\"" + "1" + "\"").accept(MediaType.APPLICATION_XHTML_XML).get(ClientResponse.class);
        assertEquals("If-None-Match mismatched.......", 200, clientResponse.getStatus());

        ws = resource().path(BASE_URL).path("/polaris");
        clientResponse = ws.header("If-None-Match", "\"" + "0" + "\"").accept(MediaType.APPLICATION_XHTML_XML).get(ClientResponse.class);
        assertEquals("If-None-Match matched.......", 304, clientResponse.getStatus());

    }

    @Test
    public void getPublicationDetailTest() {
        ws = resource().path(BASE_URL).path("/polaris/addressa");
        ClientResponse clientResponse;

        clientResponse = ws.accept(MediaType.APPLICATION_XHTML_XML).get(ClientResponse.class);
        String responseString = ws.accept(MediaType.APPLICATION_XHTML_XML).get(String.class);
        assertEquals("status found: ", 200, clientResponse.getStatus());
        assertNotNull(responseString);

        ws = resource().path(BASE_URL).path("polaris").path("addressa");
        clientResponse = ws.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertEquals(406, clientResponse.getStatus());

        ws = resource().path(BASE_URL).path("polaris").path("addressa");
        clientResponse = ws.header("If-None-Match", "\"" + "0" + "\"").accept(MediaType.APPLICATION_XHTML_XML).get(ClientResponse.class);
        assertEquals("If-None-Match matched", 304, clientResponse.getStatus());

        ws = resource().path(BASE_URL).path("polaris").path("addressa");
        clientResponse = ws.header("If-None-Match", "\"" + "1" + "\"").accept(MediaType.APPLICATION_XHTML_XML).get(ClientResponse.class);
        assertEquals("If-None-Match mismatched", 200, clientResponse.getStatus());

        /*TODO: here integration test is done properly but commented as "If-Modified-Since" header is checked against
         TODO: updated field in database which is dependent on local machine that's why integration test may fail dependent on database*/
        /*ws = resource().path(BASE_URL).path("polaris").path("addressa");
        DateTime dateTime = new DateTime(2013, 2, 27, 15, 23, 50);
        clientResponse = ws.header("If-None-Match", "\"" + "0" + "\"").header("If-Modified-Since", dateTime.toDate()).accept(MediaType.APPLICATION_XHTML_XML).get(ClientResponse.class);
        assertEquals("If-None-Match matched", 304, clientResponse.getStatus());*/
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

        ws = resource().path(BASE_URL).path("/polaris/addressa/issue/accessible_epub_3-20121024");
        clientResponse = ws.header("If-None-Match", "\"" + "0" + "\"").accept(MediaType.APPLICATION_XHTML_XML).get(ClientResponse.class);
        assertEquals("If-None-Match Header matched....", 304, clientResponse.getStatus());

        ws = resource().path(BASE_URL).path("/polaris/addressa/issue/accessible_epub_3-20121024");
        clientResponse = ws.header("If-None-Match", "\"" + "1" + "\"").accept(MediaType.APPLICATION_XHTML_XML).get(ClientResponse.class);
        assertEquals("If-None-Match Header mismatched....", 200, clientResponse.getStatus());

        /*TODO: here integration test is done properly but commented as "If-Modified-Since" header is checked against
         TODO: updated field in database which is dependent on local machine that's why integration test may fail dependent on database*/
        /*ws = resource().path(BASE_URL).path("/polaris/addressa/issue/accessible_epub_3-20121024");
        DateTime dateTime = new DateTime();
        clientResponse = ws.header("If-None-Match", "\"" + "0" + "\"").header("If-Modified-Since", dateTime.toDate()).accept(MediaType.APPLICATION_XHTML_XML).get(ClientResponse.class);
        assertEquals("If-None-Match Header matched and If-Modified-Since header matched....", 304, clientResponse.getStatus());

        ws = resource().path(BASE_URL).path("/polaris/addressa/issue/accessible_epub_3-20121024");
        dateTime = new DateTime(2013, 2, 27, 15, 23, 50);
        clientResponse = ws.header("If-None-Match", "\"" + "0" + "\"").header("If-Modified-Since", dateTime.toDate()).accept(MediaType.APPLICATION_XHTML_XML).get(ClientResponse.class);
        assertEquals("If-None-Match Header matched and If-Modified-Since header matched....", 304, clientResponse.getStatus());*/
    }

    @Test
    public void downloadEpubTest() {
        ws = resource().path(BASE_URL).path("/polaris/addressa/issue/accessible_epub_3-20121024_345.epub");
        ClientResponse clientResponse = ws.accept(MediaType.TEXT_PLAIN).get(ClientResponse.class);
        assertEquals("Content found error code: ", 404, clientResponse.getStatus());

        ws = resource().path(BASE_URL).path("/polaris/  /issue/accessible_epub_3-20121024.epub");
        clientResponse = ws.accept(MediaType.TEXT_PLAIN).get(ClientResponse.class);
        assertEquals("Bad request error code: ", 400, clientResponse.getStatus());

        ws = resource().path(BASE_URL).path("/polaris/testing/issue/accessible_epub_3-20121024.epub");
        clientResponse = ws.accept(MediaType.TEXT_PLAIN).get(ClientResponse.class);
        assertEquals("Content found error code: ", 404, clientResponse.getStatus());

        ws = resource().path(BASE_URL).path("/polaris/testing/issue/accessible_epub_3-20121024.epub");
        clientResponse = ws.accept(MediaType.APPLICATION_OCTET_STREAM).get(ClientResponse.class);
        assertEquals("Mime Type found error code: ", 406, clientResponse.getStatus());


        ws = resource().path(BASE_URL).path("/polaris/addressa/issue/accessible_epub_3-20121024.epub");
        clientResponse = ws.accept(MediaType.TEXT_PLAIN).get(ClientResponse.class);
        assertEquals("content found error code: ", 200, clientResponse.getStatus());
        assertNotNull(clientResponse);

    }

    @Test
    public void getEpubContentTest() {
        ws = resource().path(BASE_URL).path("/polaris/addressa/issue/xyz.epub/META-INF/container.xml");
        ClientResponse clientResponse = ws.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
        assertEquals("Content found error code: ", 404, clientResponse.getStatus());

        ws = resource().path(BASE_URL).path("/ /addressa/issue/ /META-INF/container.xml");
        clientResponse = ws.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
        assertEquals("Bad request found error code: ", 400, clientResponse.getStatus());

        ws = resource().path(BASE_URL).path("/polaris/addressa/issue/accessible_epub_3-20121024/META-INF/con.xml");
        clientResponse = ws.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
        assertEquals("Content found error code: ", 404, clientResponse.getStatus());

        ws = resource().path(BASE_URL).path("/polaris/addressa/issue/accessible_epub_3-20121024/META-INF/");
        clientResponse = ws.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
        assertEquals("Content found error code: ", 404, clientResponse.getStatus());

        ws = resource().path(BASE_URL).path("/polaris/addressa/issue/accessible_epub_3-20121024/META-INF/container.xml");
        clientResponse = ws.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
        assertEquals("Content found error code: ", 200, clientResponse.getStatus());
        assertNotNull(clientResponse);

        /*TODO: here integration test is done properly but commented as "If-Modified-Since" header is checked against
         TODO: updated field in database which is dependent on local machine that's why integration test may fail dependent on database*/
        /*ws = resource().path(BASE_URL).path("/polaris/addressa/issue/accessible_epub_3-20121024/META-INF/container.xml");
        DateTime dateTime = new DateTime();
        clientResponse = ws.header("If-Modified-Since", dateTime.toDate()).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
        assertEquals("If-Modified-Since greater than stored data in fileSystem ", 304, clientResponse.getStatus());*/
    }

    @Test
    public void uploadEpubTest() {
        Path directoryPath = Paths.get("src", "test", "resources", "epubs");
        File fileToUpload = new File(directoryPath.toAbsolutePath().toString() + "/widget-figure-gallery-20121024.epub");
        final FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(new FileDataBodyPart("file", fileToUpload,
                MediaType.APPLICATION_OCTET_STREAM_TYPE));

        ws = resource().path(BASE_URL).path("/polaris/addre/issue/ipad");
        ClientResponse clientResponse = ws.type(MediaType.MULTIPART_FORM_DATA).post(ClientResponse.class, multiPart);
        assertEquals("Content not found error", 404, clientResponse.getStatus());

        ws = resource().path(BASE_URL).path("/pol/addressa/issue/ipad");
        clientResponse = ws.type(MediaType.MULTIPART_FORM_DATA).post(ClientResponse.class, multiPart);
        assertEquals("Content not found error", 404, clientResponse.getStatus());

        ws = resource().path(BASE_URL).path("/pol/addressa/issue");
        clientResponse = ws.type(MediaType.MULTIPART_FORM_DATA).post(ClientResponse.class, multiPart);
        assertEquals("Content not found error", 405, clientResponse.getStatus());

        ws = resource().path(BASE_URL).path("/polaris/addressa/issue/ipad");
        fileToUpload = new File(directoryPath.toAbsolutePath().toString() + "/fileTest.txt");
        multiPart.bodyPart(new FileDataBodyPart("file", fileToUpload,
                MediaType.APPLICATION_OCTET_STREAM_TYPE));
        clientResponse = ws.type(MediaType.MULTIPART_FORM_DATA).post(ClientResponse.class, multiPart);
        assertEquals("Content not found error", 404, clientResponse.getStatus());

        ws = resource().path(BASE_URL).path("/polaris/addressa/issue/ipad");
        multiPart.bodyPart(new FormDataBodyPart("file", new ByteArrayInputStream("".getBytes()), MediaType.APPLICATION_OCTET_STREAM_TYPE));
        clientResponse = ws.type(MediaType.MULTIPART_FORM_DATA).post(ClientResponse.class, multiPart);
        assertEquals("Content not found error", 404, clientResponse.getStatus());

/*
        ws = resource().path(BASE_URL).path("/polaris/addressa/issue/ipad,mini-ipad");
        fileToUpload = new File(directoryPath.toAbsolutePath().toString() + "/widget-figure-gallery-20121024.epub");
        multiPart.bodyPart(new FileDataBodyPart("file", fileToUpload, MediaType.APPLICATION_OCTET_STREAM_TYPE));
        clientResponse = ws.type(MediaType.MULTIPART_FORM_DATA).post(ClientResponse.class, multiPart);
        assertEquals("Content not found error", 200, clientResponse.getStatus());
        assertNotNull(clientResponse);
*/

    }

    @Test
    public void getIssueListTest() {
        String organizationName = "polaris";
        String publicationName = "addressa";
        String deviceType = "ipad";
        ws = resource().queryParam("device-type", "ipad").path(BASE_URL).path(organizationName).path(publicationName).path("issue");
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

        ws = resource().path(BASE_URL).path(organizationName).path(publicationName).path("issue");
        clientResponse = ws.accept(MediaType.APPLICATION_ATOM_XML).get(ClientResponse.class);
        assertEquals("Device Type not found error: ", 400, clientResponse.getStatus());


        ws = resource().queryParam("start", "2").queryParam("limit", "8").queryParam("device-type", deviceType).path(BASE_URL).path("polaris").path("addressa").path("issue");
        responseString= ws.accept(MediaType.APPLICATION_ATOM_XML).get(String.class);
        assertNotNull(responseString);

        nodeList = (NodeList) xpathUtils.getNodeListFromHtml("feed/entry", responseString);
        assertEquals("number of entry start 2: ", 8, nodeList.getLength());

        ws = resource().queryParam("start", "2").queryParam("device-type", deviceType).path(BASE_URL).path("polaris").path("addressa").path("issue");
        responseString= ws.accept(MediaType.APPLICATION_ATOM_XML).get(String.class);
        assertNotNull(responseString);

        nodeList = (NodeList) xpathUtils.getNodeListFromHtml("feed/entry", responseString);
        assertEquals("number of entry limit default: ", 1, nodeList.getLength());

        ws = resource().queryParam("start", "2").queryParam("limit", "-8").queryParam("device-type", deviceType).path(BASE_URL).path("polaris").path("addressa").path("issue");
        clientResponse = ws.accept(MediaType.APPLICATION_ATOM_XML).get(ClientResponse.class);
        assertEquals("Bad request 400: ", 400, clientResponse.getStatus());

        ws = resource().queryParam("start", "40").queryParam("device-type", deviceType).path(BASE_URL).path("polaris").path("addressa").path("issue");
        responseString= ws.accept(MediaType.APPLICATION_ATOM_XML).get(String.class);
        assertNotNull(responseString);

        nodeList = (NodeList) xpathUtils.getNodeListFromHtml("feed/entry", responseString);
        assertEquals("number of entry for problematic start and limit: ", 0, nodeList.getLength());
        nodeList = (NodeList) xpathUtils.getNodeListFromHtml("feed/link", responseString);
        assertEquals("number f links for start = 40 exceeding total number of files: ", 2, nodeList.getLength());

        /*TODO: here integration test is done properly but commented as "If-Modified-Since" header is checked against
         TODO: updated field in database which is dependent on local machine that's why integration test may fail dependent on database*/
        /*ws = resource().queryParam("start", "1").queryParam("device-type", deviceType).path(BASE_URL).path("polaris").path("addressa").path("issue");
        DateTime dateTime = new DateTime(2013, 3, 6, 15, 29, 59);
        clientResponse = ws.header("If-Modified-Since", dateTime.toDate()).accept(MediaType.APPLICATION_ATOM_XML).get(ClientResponse.class);
        assertEquals("If-Modified-Since matched to stored data", 304, clientResponse.getStatus());

        ws = resource().queryParam("start", "1").queryParam("device-type", deviceType).path(BASE_URL).path("polaris").path("addressa").path("issue");
        dateTime = new DateTime(2013, 3, 10, 15, 29, 59);
        clientResponse = ws.header("If-Modified-Since", dateTime.toDate()).accept(MediaType.APPLICATION_ATOM_XML).get(ClientResponse.class);
        assertEquals("If-Modified-Since greater than stored data", 304, clientResponse.getStatus());

        ws = resource().queryParam("start", "1").queryParam("device-type", deviceType).path(BASE_URL).path("polaris").path("addressa").path("issue");
        dateTime = new DateTime(2013, 3, 3, 15, 29, 59);
        clientResponse = ws.header("If-Modified-Since", dateTime.toDate()).accept(MediaType.APPLICATION_ATOM_XML).get(ClientResponse.class);
        assertEquals("If-Modified-Since less than stored data ", 200, clientResponse.getStatus());*/
    }

}
