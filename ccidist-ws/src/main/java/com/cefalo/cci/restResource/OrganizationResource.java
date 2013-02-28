package com.cefalo.cci.restResource;

import com.cefalo.cci.mapping.JerseyResourceLocator;
import com.cefalo.cci.mapping.ResourceLocator;
import com.cefalo.cci.model.Organization;
import com.cefalo.cci.model.Publication;
import com.cefalo.cci.service.OrganizationService;
import com.cefalo.cci.utils.Utils;
import com.google.inject.Inject;
import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.Responses;
import com.sun.jersey.api.view.Viewable;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Path("/")
public class OrganizationResource {
    @Inject
    private OrganizationService organizationService;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_XHTML_XML)
    public Response getOrganizationList() {
        List<Organization> organizations = organizationService.getAllOrganizations();
        if (organizations.isEmpty()) {
            throw new NotFoundException();
        }

        ResourceLocator resourceLocator = JerseyResourceLocator.from(uriInfo);
        Map<Organization, URI> orgNameUriMap = new LinkedHashMap<>();

        for (Organization organization : organizations) {
            orgNameUriMap.put(organization, resourceLocator.getOrganizationURI(organization.getId()));
        }

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("orgMap", orgNameUriMap);

        //TODO: we have to add version here

        return Response.ok(new Viewable("/orgList", model)).build();
    }

    @GET
    @Path("/{organization}")
    @Produces(MediaType.APPLICATION_XHTML_XML)
    public Response getOrganizationDetail(@PathParam("organization") final String organization, @HeaderParam("If-None-Match") @DefaultValue("-1") final long ifNoneMatchVersion) {
        if (Utils.isBlank(organization)) {
            return Responses.clientError().entity("Organization name may not be empty").build();
        }
        Organization org = organizationService.getOrganization(organization.toLowerCase());
        if (org == null) {
            throw  new NotFoundException();
        }

        if (org.getVersion() == ifNoneMatchVersion) {
            return Response.notModified().build();
        }

        ResourceLocator resourceLocator = JerseyResourceLocator.from(uriInfo);
        Map<String, URI> publicationNameUriMap = new HashMap<String, URI>();

        Iterator<Publication> publicationIterator = org.getPublications().iterator();

        while (publicationIterator.hasNext()) {
            Publication tempPublication = publicationIterator.next();
            publicationNameUriMap.put(tempPublication.getName(), resourceLocator.getPublicationURI(org.getId(), tempPublication.getId()));
        }


        Map<String, Object> model = new HashMap<String, Object>();
        model.put("organizationName", org.getName());
        model.put("publicationMap", publicationNameUriMap);

        ResponseBuilder responseBuilder = Response.ok(new Viewable("/organization", model));
        responseBuilder = responseBuilder.tag(String.valueOf(org.getVersion()));
        return responseBuilder.build();
    }
}
