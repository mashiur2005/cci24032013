package com.cefalo.cci.restResource;

import com.cefalo.cci.service.CciService;
import com.cefalo.cci.utils.Utils;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.syndication.feed.synd.SyndFeed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/{organization}/{publication}/issues")
public class IssueListResource {
    @Inject
    private CciService cciService;

    @Inject @Named("epubFileDirPath")
    private String epubFileDirPath;

    @GET
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Response getIssueList(@PathParam("organization") String organizationName, @PathParam("publication") String publicationName,
                                 @QueryParam("start") @DefaultValue("1") String start, @QueryParam("limit") @DefaultValue("4") String limit) {
        if (!Utils.ORGANIZATION_DETAILS.containsKey(organizationName) || !Utils.ORGANIZATION_DETAILS.get(organizationName).contains(publicationName)) {
            return Response.status(404).build();
        }

        int startAsInt = Integer.valueOf(start);
        int limitAsInt = Integer.valueOf(limit);

        String fileDir = epubFileDirPath + Utils.FILE_SEPARATOR + organizationName + Utils.FILE_SEPARATOR + publicationName;

        SyndFeed feed = cciService.getIssueAsAtomFeed(organizationName, publicationName, fileDir, startAsInt, limitAsInt);

        return Response.ok(feed).build();
    }
}
