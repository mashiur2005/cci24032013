package com.cefalo.cci.restResource;

import com.cefalo.cci.dao.ResourceDao;
import com.cefalo.cci.domain.User;
import com.cefalo.cci.service.HelloService;
import com.google.inject.Inject;
import com.sun.jersey.api.view.Viewable;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;

@Path("/resource")
public class HelloResource {

    @Inject
    private HelloService helloService;

    @Inject
    private ResourceDao resourceDao;

    @GET
    @Path("/plainText")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAsPlainText(@QueryParam("user") String userName) {
        return "Hello: " + userName;
    }

    @GET
    @Path("/jspTest")
    @Produces(MediaType.TEXT_HTML)
    public Response getFromJsp(@DefaultValue("Anonymous") @QueryParam("user") String userName) {
        String message = helloService.getHelloMessage();

        //save message
        User userObj = new User();
        userObj.setName(userName);
        resourceDao.saveUserInfo(userObj);

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("user", userName);
        model.put("message", message);
        return Response.ok(new Viewable("/hello", model)).build();
    }
}
