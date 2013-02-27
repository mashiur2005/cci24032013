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
    private Storage storage;

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

        String issueLocation = epubFileDirPath + Utils.FILE_SEPARATOR + organization + Utils.FILE_SEPARATOR + publication;
        String epubFileLoc = issueLocation + Utils.FILE_SEPARATOR + issue + ".epub";
        log.info("Epub File Loc... " + epubFileLoc);

        Preconditions.checkNotNull(epubFileLoc, "Epub File location can not be null");
        Preconditions.checkNotNull(contentLocInEpub, "Content Location of Epub File can not be null");

        epubFileLoc = epubFileLoc.replace("\\", "/");
        URI uri = URI.create("jar:file:/" + epubFileLoc);
        URI fragmentPath = URI.create("/" + contentLocInEpub);
        final InputStream in = storage.getFragment(uri, fragmentPath);

        if (in == null) {
            throw new NotFoundException("Resource is not found");
        }
        StreamingOutput sout = new StreamingOutput() {
            public void write(OutputStream outputStream) throws IOException {
                ByteStreams.copy(in, outputStream);
            }
        };
        String mediaType = cciService.getMediaType(epubFileLoc, contentLocInEpub);
        if (mediaType == null) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }
        return Response.ok(sout, mediaType).build();
    }

    @GET
    @Path("/{file: [^/]+.epub?}")
    @Produces(MediaType.TEXT_PLAIN)
    public StreamingOutput downloadEpub(@PathParam("organization") String organization, @PathParam("publication") String publication,
                                        @PathParam("file") String file) throws IOException {
        String fileLocation = epubFileDirPath + Utils.FILE_SEPARATOR + organization + Utils.FILE_SEPARATOR + publication + Utils.FILE_SEPARATOR + file;
        fileLocation = fileLocation.replace("\\", "/");
        log.info("File location is " + file);
        InputStream in = null;

        try {
            in = storage.get(URI.create("file:/" + fileLocation));

            final InputStream finalIn = in;
            return new StreamingOutput() {
                public void write(OutputStream outputStream) throws IOException {
                    ByteStreams.copy(finalIn, outputStream);
                    Closeables.close(finalIn, true);
                }
            };
        } catch (NullPointerException e) {
            throw new NotFoundException("File not found");
        } catch (IOException e) {
            Closeables.close(in, true);
            Throwables.propagateIfPossible(e.getCause(), IOException.class);
            throw new IllegalStateException(e);
        }
    }
}
