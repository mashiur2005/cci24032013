package com.cefalo.cci.restResource;

import com.cefalo.cci.exception.NotFoundException;
import com.cefalo.cci.service.CciService;
import com.cefalo.cci.utils.Utils;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.jersey.api.view.Viewable;
import com.sun.syndication.feed.synd.SyndFeed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Path("/")
public class CciResource {
    private  final Logger log = LoggerFactory.getLogger(CciResource.class);

    @Inject
    private CciService cciService;

    @Inject @Named("epubFileDirPath")
    private String epubFileDirPath;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getOrganizationList() {
        Map<String, Object> model = new HashMap<String, Object>();
        Set<String> organizations = Utils.ORGANIZATION_DETAILS.keySet();
        model.put("organizations", organizations);
        return Response.ok(new Viewable("/organizationList", model)).build();
    }

    @GET
    @Path("/{organization}")
    @Produces(MediaType.TEXT_HTML)
    public Response getOrganizationDetail(@PathParam("organization") String organization) {
        if (!Utils.ORGANIZATION_DETAILS.containsKey(organization)) {
            return Response.status(404).build();
        }
        log.info("Log Working.................");
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("organization", organization);
        model.put("publications", Utils.ORGANIZATION_DETAILS.get(organization));
        return Response.ok(new Viewable("/organization", model)).build();
    }

    @GET
    @Path("/{organization}/{publication}")
    @Produces(MediaType.TEXT_HTML)
    public Response getPublicationDetail(@PathParam("organization") String organization, @PathParam("publication") String publication) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("organization", organization);
        model.put("publication", publication);
        return Response.ok(new Viewable("/publication", model)).build();
    }

    @GET
    @Path("/{organization}/{publication}/{issue}")
    @Produces(MediaType.TEXT_HTML)
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
        return Response.ok(new Viewable("/issueDetail", model)).build();
    }

    @GET
    @Path("/{organization}/{publication}/issues")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Response getIssueList(@PathParam("organization") String organizationName, @PathParam("publication") String publicationName) {
        if (!Utils.ORGANIZATION_DETAILS.containsKey(organizationName) || !Utils.ORGANIZATION_DETAILS.get(organizationName).contains(publicationName)) {
            return Response.status(404).build();
        }

        String feedType = "atom_0.3";
        String fileDir = epubFileDirPath + Utils.FILE_SEPARATOR + organizationName + Utils.FILE_SEPARATOR + publicationName;

        SyndFeed feed = cciService.getIssueAsAtomFeed(feedType, organizationName, publicationName, fileDir);

        return Response.ok(feed).build();
    }

}
