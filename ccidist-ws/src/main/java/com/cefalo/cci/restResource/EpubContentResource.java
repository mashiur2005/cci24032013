package com.cefalo.cci.restResource;

import com.cefalo.cci.service.CciService;
import com.cefalo.cci.storage.Storage;
import com.cefalo.cci.utils.Utils;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.jersey.api.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

@Path("/{organization}/{publication}/{issue}/{contentLocInEpub: .+}")
public class EpubContentResource {
    private  final Logger log = LoggerFactory.getLogger(EpubContentResource.class);

    @Inject
    private CciService cciService;

    @Inject @Named("epubFileDirPath")
    private String epubFileDirPath;

    @Inject
    private Storage storage;

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
}
