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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.net.URI;
import java.util.*;

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

        Date latestModifiedDate = organizations.get(0).getUpdated();
        ResponseBuilder unmodifiedResponseBuilder = request.evaluatePreconditions(latestModifiedDate, EntityTag.valueOf(Utils
                .createETagHeaderValue(latestModifiedDate.getTime())));
        if (unmodifiedResponseBuilder != null) {
            return unmodifiedResponseBuilder.tag(String.valueOf(latestModifiedDate.getTime())).lastModified(latestModifiedDate).build();
        }

        ResourceLocator resourceLocator = JerseyResourceLocator.from(uriInfo);
        Map<Organization, URI> orgNameUriMap = new LinkedHashMap<>();

        for (Organization organization : organizations) {
            orgNameUriMap.put(organization, resourceLocator.getOrganizationURI(organization.getId()));
        }

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("orgMap", orgNameUriMap);

        //TODO: we have to add version here

        return Response.ok(new Viewable("/orgList", model)).tag(String.valueOf(latestModifiedDate.getTime())).lastModified(latestModifiedDate).build();
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

        ResponseBuilder unmodifiedResponseBuilder = request.evaluatePreconditions(organization.getUpdated(), EntityTag.valueOf(Utils
                .createETagHeaderValue(organization.getVersion())));
        if (unmodifiedResponseBuilder != null) {
            return unmodifiedResponseBuilder.lastModified(organization.getUpdated()).tag(String.valueOf(organization.getVersion())).build();
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

        return Response.ok(new Viewable("/organization", model)).tag(String.valueOf(organization.getVersion())).lastModified(organization.getUpdated()).build();
    }
}
