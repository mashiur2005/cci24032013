package com.cefalo.cci.restResource;

import com.cefalo.cci.dao.OrganizationDao;
import com.cefalo.cci.model.Organization;
import com.google.inject.Inject;
import com.sun.jersey.api.view.Viewable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/")
public class OrganizationListResource {
    @Inject
    private OrganizationDao organizationDao;

    @GET
    @Produces(MediaType.APPLICATION_XHTML_XML)
    public Response getOrganizationList() {
        Map<String, Object> model = new HashMap<String, Object>();

        List<Organization> organizationList = organizationDao.getAllOrganizations();
        model.put("organizations", organizationList);
        return Response.ok(new Viewable("/orgList", model)).build();
    }
}
