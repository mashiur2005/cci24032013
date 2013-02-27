package com.cefalo.cci.restResource;

import com.cefalo.cci.model.Publication;
import com.cefalo.cci.service.PublicationService;
import com.sun.jersey.api.view.Viewable;

import javax.inject.Inject;
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

    @Inject
    private PublicationService publicationService;

    @GET
    @Produces(MediaType.APPLICATION_XHTML_XML)
    public Response getPublicationDetail(@PathParam("organization") String organization, @PathParam("publication") String publication) {
        Publication pub =  publicationService.getPublication(publication.toLowerCase());

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("publication", pub);
        model.put("contextPath", uriInfo.getBaseUri().getPath());
        return Response.ok(new Viewable("/publication", model)).build();
    }
}
