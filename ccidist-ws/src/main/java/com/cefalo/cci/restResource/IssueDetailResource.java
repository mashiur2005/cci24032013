package com.cefalo.cci.restResource;

import com.cefalo.cci.service.CciService;
import com.cefalo.cci.utils.Utils;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.view.Viewable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.Map;

@Path("/{organization}/{publication}/{issue}")
public class IssueDetailResource {
    private  final Logger log = LoggerFactory.getLogger(IssueDetailResource.class);

    @Inject
    private CciService cciService;

    @Context
    private UriInfo uriInfo;

    @Inject @Named("epubFileDirPath")
    private String epubFileDirPath;

    @GET
    @Produces(MediaType.APPLICATION_XHTML_XML)
    public Response getIssueDetail(@PathParam("organization") String organization, @PathParam("publication") String publication,
                                   @PathParam("issue") String issue) {

        String issueLocation = epubFileDirPath + Utils.FILE_SEPARATOR + organization + Utils.FILE_SEPARATOR + publication;
        log.info("issue location is " + issueLocation);

        //TODO: Checking database for issue and this method is used to read file from directory as temporary basis
        if (!cciService.getAllFileNamesInDirectory(issueLocation).contains(issue + ".epub")) {
            throw new NotFoundException("Issue " + issue + " is not found");
        }

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("organization", organization);
        model.put("publication", publication);
        model.put("issue", issue);
        model.put("contextPath", uriInfo.getBaseUri().getPath());
        return Response.ok(new Viewable("/issueDetail", model)).build();
    }
}
