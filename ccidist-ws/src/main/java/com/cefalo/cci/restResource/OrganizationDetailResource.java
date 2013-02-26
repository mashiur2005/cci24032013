package com.cefalo.cci.restResource;

import com.cefalo.cci.utils.Utils;
import com.sun.jersey.api.view.Viewable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/{organization}")
public class OrganizationDetailResource {
    @GET
    @Produces(MediaType.APPLICATION_XHTML_XML)
    public Response getOrganizationDetail(@PathParam("organization") String organization) {
        if (!Utils.ORGANIZATION_DETAILS.containsKey(organization)) {
            return Response.status(404).build();
        }
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("organization", organization);
        model.put("publications", Utils.ORGANIZATION_DETAILS.get(organization));
        return Response.ok(new Viewable("/organization", model)).build();
    }
}
