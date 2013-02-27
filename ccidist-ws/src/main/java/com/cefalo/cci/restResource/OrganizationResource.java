package com.cefalo.cci.restResource;

import com.cefalo.cci.model.Organization;
import com.cefalo.cci.service.OrganizationService;
import com.cefalo.cci.utils.Utils;
import com.google.inject.Inject;
import com.sun.jersey.api.view.Viewable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/")
public class OrganizationResource {
    @Inject
    private OrganizationService organizationService;

    @GET
    @Produces(MediaType.APPLICATION_XHTML_XML)
    public Response getOrganizationList() {
        Map<String, Object> model = new HashMap<String, Object>();

        List<Organization> organizationList = organizationService.getAllOrganizations();
        model.put("organizations", organizationList);
        return Response.ok(new Viewable("/orgList", model)).build();
    }

    @GET
    @Path("/{organization}")
    @Produces(MediaType.APPLICATION_XHTML_XML)
    public Response getOrganizationDetail(@PathParam("organization") String organization) {
        if (!Utils.ORGANIZATION_DETAILS.containsKey(organization)) {
            return Response.status(404).build();
        }
        Organization org = organizationService.getOrganization(organization.toLowerCase());
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("organization", org);
       // model.put("publications", Utils.ORGANIZATION_DETAILS.get(organization));
        return Response.ok(new Viewable("/organization", model)).build();
    }
}
