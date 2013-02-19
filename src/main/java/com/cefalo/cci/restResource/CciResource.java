package com.cefalo.cci.restResource;

import com.cefalo.cci.utility.Utils;
import com.sun.jersey.api.view.Viewable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Path("/")
public class CciResource {
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getOrganizationList() {
        Map<String, Object> model = new HashMap<String, Object>();
        Set<String> organizations =  Utils.ORGANIZATION_DETAILS.keySet();
        model.put("organizations", organizations);
        return Response.ok(new Viewable("/organizationList", model)).build();
    }
}
