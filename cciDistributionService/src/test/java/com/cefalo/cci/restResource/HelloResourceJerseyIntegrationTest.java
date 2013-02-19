package com.cefalo.cci.restResource;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HelloResourceJerseyIntegrationTest extends JerseyTest {
  private static final String BASE_URL = "/cciService/resource";
  private static final String PACKAGE_NAME = "com.cefalo.cci.restResource";

  private WebResource ws;

  public HelloResourceJerseyIntegrationTest() {
    super(new WebAppDescriptor.Builder(PACKAGE_NAME).build());
  }

  @Test
  public void getPlainTextResponseStatus() {
    String userName = "Any User Name";
    ws = resource().path(BASE_URL + "/plainText");
    ClientResponse response = ws.queryParam("user", userName).accept(MediaType.TEXT_PLAIN).get(ClientResponse.class);
    assertEquals(200, response.getStatus());
  }

  @Test
  public void getPlainText() {
    String userName = "Any User Name";
    ws = resource().path(BASE_URL + "/plainText");
    String getResult = ws.queryParam("user", userName).accept(MediaType.TEXT_PLAIN).get(String.class);
    assertTrue(getResult.contains("Hello: " + userName));
  }
}
