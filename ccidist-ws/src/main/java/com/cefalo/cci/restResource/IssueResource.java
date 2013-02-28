package com.cefalo.cci.restResource;

import com.cefalo.cci.service.CciService;
import com.cefalo.cci.service.IssueService;
import com.cefalo.cci.storage.Storage;
import com.cefalo.cci.utils.Utils;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.view.Viewable;
import com.sun.syndication.feed.synd.SyndFeed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Path("/{organization}/{publication}/issue/")
public class IssueResource {
    private  final Logger log = LoggerFactory.getLogger(IssueResource.class);

    @Inject
    private CciService cciService;

    @Inject @Named("epubFileDirPath")
    private String epubFileDirPath;

    @Inject
    private IssueService issueService;

    @Inject
    @Named("cacheStorage")
    private Storage cacheStorage;

    @Inject
    @Named("databaseStorage")
    private Storage databaseStorage;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Response getIssueList(@PathParam("organization") String organizationName, @PathParam("publication") String publicationName,
                                 @QueryParam("start") @DefaultValue("1") String start, @QueryParam("limit") @DefaultValue("10") String limit) {
        //TODO: we have to check 404 error here
        if (start.isEmpty()) {
            start = "1";
        }
        if (limit.isEmpty()) {
            limit = "10";
        }

        int startAsInt = Integer.valueOf(start);
        int limitAsInt = Integer.valueOf(limit);

        SyndFeed feed = issueService.getIssueAsAtomFeed(uriInfo.getBaseUri().getPath(), organizationName, publicationName, startAsInt, limitAsInt);

        return Response.ok(feed).build();
    }

    @GET
    @Path("/{issue}")
    @Produces(MediaType.APPLICATION_XHTML_XML)
    public Response getIssueDetail(@PathParam("organization") String organization, @PathParam("publication") String publication,
                                   @PathParam("issue") String issue) {

        if (!issueService.getIssueNameAsList(publication).contains(issue + ".epub")) {
            throw new NotFoundException("Issue " + issue + " is not found");
        }

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("organization", organization);
        model.put("publication", publication);
        model.put("issue", issue);
        model.put("contextPath", uriInfo.getBaseUri().getPath());
        return Response.ok(new Viewable("/issueDetail", model)).build();
    }

    @Path("/{issue}/{contentLocInEpub: .+}")
    @GET
    public Response getEpubContent(@PathParam("organization") String organization, @PathParam("publication") String publication,
                                   @PathParam("issue") String issue, @PathParam("contentLocInEpub") final String contentLocInEpub) throws IOException {

        Preconditions.checkNotNull(contentLocInEpub, "Content Location of Epub File can not be null");

        URI resourceUri = URI.create(issue);
        URI fragmentPath = URI.create(contentLocInEpub);

        try {
            InputStream in = cacheStorage.getFragment(resourceUri, fragmentPath);
            if (in == null) {
                throw new NotFoundException("Resource is not found");
            }

            final InputStream finalIn = in;
            StreamingOutput sout = new StreamingOutput() {
                public void write(OutputStream outputStream) throws IOException {
                    ByteStreams.copy(finalIn, outputStream);
                    Closeables.close(finalIn, true);
                }
            };

            String mediaType = cciService.getMediaType(contentLocInEpub);
            if (mediaType == null) {
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
            }
            return Response.ok(sout, mediaType).build();
        } catch (IOException e) {
            Throwables.propagateIfPossible(e.getCause(), IOException.class);
            throw new IllegalStateException(e);
        }
    }

    @GET
    @Path("/{file: [^/]+.epub?}")
    @Produces(MediaType.TEXT_PLAIN)
    public StreamingOutput downloadEpub(@PathParam("organization") String organization, @PathParam("publication") String publication,
                                        @PathParam("file") String file) throws IOException {
        InputStream in = null;

        try {
            String issueId = file.split("[.]")[0];
            in = databaseStorage.get(URI.create(issueId));

            final InputStream finalIn = in;
            return new StreamingOutput() {
                public void write(OutputStream outputStream) throws IOException {
                    ByteStreams.copy(finalIn, outputStream);
                    Closeables.close(finalIn, true);
                }
            };
        } catch (FileNotFoundException fnfe) {
            throw new NotFoundException("File not found");
        } catch (IOException e) {
            Closeables.close(in, true);
            Throwables.propagateIfPossible(e.getCause(), IOException.class);
            throw new IllegalStateException(e);
        }
    }
}
