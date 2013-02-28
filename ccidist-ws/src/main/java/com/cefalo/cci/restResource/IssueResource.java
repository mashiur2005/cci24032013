package com.cefalo.cci.restResource;

import static com.cefalo.cci.utils.Utils.isBlank;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cefalo.cci.mapping.JerseyResourceLocator;
import com.cefalo.cci.mapping.ResourceLocator;
import com.cefalo.cci.model.Issue;
import com.cefalo.cci.model.Publication;
import com.cefalo.cci.service.CciService;
import com.cefalo.cci.service.IssueService;
import com.cefalo.cci.service.PublicationService;
import com.cefalo.cci.storage.Storage;
import com.cefalo.cci.utils.Utils;
import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.Responses;
import com.sun.jersey.api.view.Viewable;
import com.sun.syndication.feed.synd.SyndFeed;

@Path("/{organization}/{publication}/issue/")
public class IssueResource {
    private  final Logger log = LoggerFactory.getLogger(IssueResource.class);

    @Inject
    private IssueService issueService;

    @Inject
    private PublicationService publicationService;

    @Inject
    private CciService cciService;

    @Inject
    private Storage storage;

    @Context
    private Request request;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Response getIssueList(
            @PathParam("organization") @DefaultValue("") String organizationName,
            @PathParam("publication") @DefaultValue("") String publicationName,
            @QueryParam("start") @DefaultValue("1") int start,
            @QueryParam("limit") @DefaultValue("1") int limit) {
        if (Utils.isBlank(publicationName) || Utils.isBlank(organizationName)) {
            return Responses.clientError().build();
        }

        Publication publication = publicationService.getPublication(publicationName);
        if (publication == null || !Objects.equals(publication.getOrganization().getId(), organizationName)) {
            return Responses.notFound().build();
        }

        SyndFeed feed = issueService.getIssuesAsAtomFeed(publication.getOrganization(), publication, start, limit,
                JerseyResourceLocator.from(uriInfo));

        return Response.ok(feed).build();
    }

    @GET
    @Path("/{issue}")
    @Produces(MediaType.APPLICATION_XHTML_XML)
    public Response getIssueDetail(@PathParam("organization") @DefaultValue("") String organizationId,
            @PathParam("publication") @DefaultValue("") String publicationId,
            @PathParam("issue") @DefaultValue("") String issueId) {

        Issue issue = retrieveIssue(organizationId, publicationId, issueId);

        ResponseBuilder notModifiedResponseBuilder = request.evaluatePreconditions(EntityTag.valueOf(Utils
                .createETagHeaderValue(issue.getVersion())));
        if (notModifiedResponseBuilder != null) {
            return notModifiedResponseBuilder.build();
        }

        Map<String, Object> model = new HashMap<>();
        model.put("organization", issue.getPublication().getOrganization());
        model.put("publication", issue.getPublication());
        model.put("issue", issue);

        ResourceLocator locator = JerseyResourceLocator.from(uriInfo);
        model.put("binaryUri", locator.getEpubBinaryURI(organizationId, publicationId, issueId));
        model.put("containerUri",
                locator.getEpubContentURI(organizationId, publicationId, issueId, "META-INF/container.xml"));

        return Response.ok(new Viewable("/issueDetail", model)).build();
    }

    @Path("/{issue}/{contentLocInEpub: .+}")
    @GET
    public Response getEpubContent(@PathParam("organization") String organization, @PathParam("publication") String publication,
                                   @PathParam("issue") String issue, @PathParam("contentLocInEpub") final String contentLocInEpub) throws IOException {

        checkNotNull(contentLocInEpub, "Content Location of Epub File can not be null");

        URI resourceUri = URI.create(issue);
        URI fragmentPath = URI.create(contentLocInEpub);

        try {
            InputStream in = storage.getFragment(resourceUri, fragmentPath);
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
    public Response downloadEpub(
            @PathParam("organization") @DefaultValue("") String organizationId,
            @PathParam("publication") @DefaultValue("") String publicationId,
            @PathParam("file") @DefaultValue("") String binaryFileName) throws IOException {

        final String issueId = Files.getNameWithoutExtension(binaryFileName);
        Issue issue = retrieveIssue(organizationId, publicationId, issueId);

        long binaryVersion = issue.getEpubFile().getVersion();
        ResponseBuilder unmodifiedResponseBuilder = request.evaluatePreconditions(EntityTag.valueOf(Utils
                .createETagHeaderValue(binaryVersion)));
        if (unmodifiedResponseBuilder != null) {
            return unmodifiedResponseBuilder.build();
        }

        InputStream binaryStream = null;
        boolean exceptionHappened = false;
        try {
            binaryStream = storage.get(URI.create(issue.getId()));
            final InputStream finalVarBinaryStream = binaryStream;

            StreamingOutput streamingOutput = new StreamingOutput() {
                public void write(OutputStream outputStream) throws IOException {
                    try (InputStream resource = finalVarBinaryStream) {
                        ByteStreams.copy(resource, outputStream);
                    } catch (Throwable t) {
                        log.error(String.format("Error sending EPub file: %s", issueId), t);
                        throw t;
                    }
                }
            };
            return Response.ok().tag(String.valueOf(binaryVersion)).entity(streamingOutput).build();
        } catch (FileNotFoundException fnfe) {
            exceptionHappened = true;
            throw new NotFoundException();
        } catch (Throwable t) {
            exceptionHappened = true;
            throw t;
        } finally {
            if (exceptionHappened) {
                // We only close when exception happened. Otherwise, the StreamingOutput.write will close it.
                Closeables.close(binaryStream, exceptionHappened);
            }
        }
    }

    private Issue retrieveIssue(String organizationId, String publicationId, String issueId) throws WebApplicationException {
        if (isBlank(organizationId) || isBlank(publicationId) || isBlank(issueId)) {
            throw new WebApplicationException(Status.BAD_REQUEST);
        }

        Issue issue = issueService.getIssue(issueId);
        if (issue == null ||
                !Objects.equals(publicationId, issue.getPublication().getId()) ||
                !Objects.equals(organizationId, issue.getPublication().getOrganization().getId())) {
            throw new NotFoundException();
        }

        return issue;
    }
}
