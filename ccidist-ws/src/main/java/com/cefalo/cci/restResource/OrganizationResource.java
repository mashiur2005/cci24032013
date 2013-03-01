package com.cefalo.cci.restResource;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

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

@Path("/")
public class OrganizationResource {
    @Inject
    private OrganizationService organizationService;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

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
    public Response getOrganizationDetail(@PathParam("organization") final String organizationId) {
        if (Utils.isBlank(organizationId)) {
            return Responses.clientError().entity("Organization name may not be empty").build();
        }

        Organization organization = organizationService.getOrganization(organizationId);
        if (organization == null) {
            throw new NotFoundException();
        }

        ResponseBuilder unmodifiedResponseBuilder = request.evaluatePreconditions(EntityTag.valueOf(Utils
                .createETagHeaderValue(organization.getVersion())));
        if (unmodifiedResponseBuilder != null) {
            return unmodifiedResponseBuilder.build();
        }

        ResourceLocator resourceLocator = JerseyResourceLocator.from(uriInfo);
        Map<String, URI> publicationNameUriMap = new TreeMap<>();

        for (Publication publication : organization.getPublications()) {
            publicationNameUriMap.put(publication.getName(),
                    resourceLocator.getPublicationURI(organization.getId(), publication.getId()));
        }

        Map<String, Object> model = new HashMap<>();
        model.put("organizationName", organization.getName());
        model.put("publicationMap", publicationNameUriMap);

        return Response.ok(new Viewable("/organization", model)).tag(String.valueOf(organization.getVersion())).build();
    }
}
