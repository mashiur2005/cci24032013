package com.cefalo.cci.restResource;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
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
import com.cefalo.cci.model.Publication;
import com.cefalo.cci.service.PublicationService;
import com.cefalo.cci.utils.Utils;
import com.google.common.base.Objects;
import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.Responses;
import com.sun.jersey.api.view.Viewable;

@Path("/{organization}/{publication}")
public class PublicationDetailResource {
    @Context
    private Request request;

    @Context
    private UriInfo uriInfo;

    @Inject
    private PublicationService publicationService;

    // NOTE: Use @DefaulValue. That makes us immune to NULL de-referencing issues.
    // NOTE: Prefer final parameters.
    // NOTE: Jersey can work with primitive types. So method parameters can be "int" or "long".
    // NOTE: If multiple resource methods are used, consider moving the params to member variables.
    @GET
    @Produces(MediaType.APPLICATION_XHTML_XML)
    public Response getPublicationDetail(
            @PathParam("organization") @DefaultValue("") final String organizationName,
            @PathParam("publication") @DefaultValue("") final String publicationName) {

        // Fail fast. No point in proceeding if our arguments are obviously wrong.
        if (Utils.isBlank(publicationName) || Utils.isBlank(organizationName)) {
            return Responses.clientError().entity("Organization or publication name may not be empty.").build();
        }

        Publication publication = publicationService.getPublication(publicationName);
        // We should not allow people to trick us, the requested URI should be accurate.
        // TODO: There is a slight performance hit for this. Caching may solve this.
        if (publication == null || !Objects.equal(publication.getOrganization().getId(), organizationName)) {
            throw new NotFoundException();
        }

        // Support conditional GET requests
        ResponseBuilder unmodifiedResponseBuilder = request.evaluatePreconditions(EntityTag.valueOf(Utils
                .createETagHeaderValue(publication.getVersion())));
        if (unmodifiedResponseBuilder != null) {
            return unmodifiedResponseBuilder.build();
        }

        // FIXME: It would be hard to test this :-(. One option is to create a base class for all resources and return
        // the locator from a method that we can override for testing. Best would be if we can inject this via Guice.
        ResourceLocator resourceLocator = JerseyResourceLocator.from(uriInfo);

        // NOTE: Use JDK 7 diamond operator :-)
        Map<String, Object> model = new HashMap<>();
        model.put("publication", publication);
        // All URIs must come from the resource locator. Otherwise, we'll have a hard time to maintain this.
        model.put("issueSearchURI", resourceLocator.getIssueListURI(organizationName, publicationName));

        ResponseBuilder responseBuilder = Response.ok(new Viewable("/publication", model));
        // We should add the version string in the ETag header.
        responseBuilder = responseBuilder.tag(String.valueOf(publication.getVersion()));
        return responseBuilder.build();
    }
}
