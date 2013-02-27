package com.cefalo.cci.restResource;

import com.sun.jersey.api.view.Viewable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.Map;

@Path("/{organization}/{publication}")
public class PublicationDetailResource {
    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_XHTML_XML)
    public Response getPublicationDetail(@PathParam("organization") String organization, @PathParam("publication") String publication) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("organization", organization);
        model.put("publication", publication);
        model.put("contextPath", uriInfo.getBaseUri().getPath());
        return Response.ok(new Viewable("/publication", model)).build();
    }
}
