package com.cefalo.cci.restResource;

import com.cefalo.cci.service.CciService;
import com.cefalo.cci.utils.Utils;
import com.google.inject.Inject;
import com.sun.jersey.api.view.Viewable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("/")
public class CciResource {
    private static final Logger log = LoggerFactory.getLogger(CciResource.class);

    @Inject
    private CciService cciService;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getOrganizationList() {
        Map<String, Object> model = new HashMap<String, Object>();
        Set<String> organizations =  Utils.ORGANIZATION_DETAILS.keySet();
        model.put("organizations", organizations);
        return Response.ok(new Viewable("/organizationList", model)).build();
    }

    @GET
    @Path("/{organization}")
    @Produces(MediaType.TEXT_HTML)
    public Response getOrganizationDetail(@PathParam("organization") String organization) {
        log.info("Log Working.................");
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("organization", organization);
        model.put("publications", Utils.ORGANIZATION_DETAILS.get(organization));
        return Response.ok(new Viewable("/organization", model)).build();
    }

    @GET
    @Path("/{organization}/{publication}")
    @Produces(MediaType.TEXT_HTML)
    public Response getPublicationDetail(@PathParam("organization") String organization, @PathParam("publication") String publication) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("organization", organization);
        model.put("publication", publication);
        return Response.ok(new Viewable("/publication", model)).build();
    }
}
