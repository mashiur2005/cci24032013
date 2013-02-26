package com.cefalo.cci.restResource;

import com.cefalo.cci.storage.Storage;
import com.cefalo.cci.utils.Utils;
import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.jersey.api.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

@Path("/{organization}/{publication}/{file: [^/]+.epub?}")
public class EpubDownloadResource {
    private  final Logger log = LoggerFactory.getLogger(EpubDownloadResource.class);

    @Inject @Named("epubFileDirPath")
    private String epubFileDirPath;

    @Inject
    private Storage storage;

    @GET
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
