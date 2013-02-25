package com.cefalo.cci.restResource;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.cefalo.cci.storage.Storage;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cefalo.cci.service.CciService;
import com.cefalo.cci.utils.Utils;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.view.Viewable;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.URI;
import java.net.URLConnection;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
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

    @Inject
    private Storage storage;

    @GET
    @Produces(MediaType.APPLICATION_XHTML_XML)
    public Response getOrganizationList() {
        Map<String, Object> model = new HashMap<String, Object>();
        Set<String> organizations = Utils.ORGANIZATION_DETAILS.keySet();
        model.put("organizations", organizations);
        return Response.ok(new Viewable("/organizationList", model)).build();
    }

    @GET
    @Path("/{organization}")
    @Produces(MediaType.APPLICATION_XHTML_XML)
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
    @Produces(MediaType.APPLICATION_XHTML_XML)
    public Response getPublicationDetail(@PathParam("organization") String organization, @PathParam("publication") String publication) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("organization", organization);
        model.put("publication", publication);
        return Response.ok(new Viewable("/publication", model)).build();
    }

    @GET
    @Path("/{organization}/{publication}/{issue}")
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
        return Response.ok(new Viewable("/issueDetail", model)).build();
    }

    @GET
    @Path("/{organization}/{publication}/issues")
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

    @GET
    @Path("/{organization}/{publication}/{issue}/{contentLocInEpub: .+}")
    public Response getEpubContent(@PathParam("organization") String organization, @PathParam("publication") String publication,
                                   @PathParam("issue") String issue, @PathParam("contentLocInEpub") final String contentLocInEpub) throws IOException {

        String issueLocation = epubFileDirPath + Utils.FILE_SEPARATOR + organization + Utils.FILE_SEPARATOR + publication;
        String epubFileLoc = issueLocation + Utils.FILE_SEPARATOR + issue + ".epub";
        log.info("Epub File Loc... " + epubFileLoc);

        epubFileLoc = epubFileLoc.replace("\\", "/");
        URI uri = URI.create("jar:file:/" + epubFileLoc);
        URI fragmentPath = URI.create("/" + contentLocInEpub);
        final InputStream in = storage.getFragment(uri, fragmentPath);

        if (in == null) {
            throw new NotFoundException("Resource is not found");
        }
        StreamingOutput sout = new StreamingOutput() {
            public void write(OutputStream outputStream) throws IOException {
                IOUtils.copy(in, outputStream);

            }
        };
        String mediaType = cciService.getMediaType(epubFileLoc, contentLocInEpub);
        if (mediaType == null) {
              mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }
        return Response.ok(sout, mediaType).build();
    }
}
