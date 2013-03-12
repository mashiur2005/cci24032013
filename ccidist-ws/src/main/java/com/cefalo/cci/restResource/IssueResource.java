package com.cefalo.cci.restResource;

import com.cefalo.cci.mapping.JerseyResourceLocator;
import com.cefalo.cci.mapping.ResourceLocator;
import com.cefalo.cci.model.Issue;
import com.cefalo.cci.model.Publication;
import com.cefalo.cci.service.CciService;
import com.cefalo.cci.service.IssueService;
import com.cefalo.cci.service.PublicationService;
import com.cefalo.cci.storage.Storage;
import com.cefalo.cci.utils.Utils;
import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.Responses;
import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import com.sun.syndication.feed.synd.SyndFeed;
import org.joda.time.DateMidnight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import java.io.*;
import java.net.URI;
import java.util.*;

import static com.cefalo.cci.utils.Utils.isBlank;


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

    @Inject
    @Named("cacheDirFullPath")
    private String cacheDirFullPath;

    @Inject
    @Named("fileSystemSeperator")
    private String fileSystemSeperator;

    @GET
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Response getIssueList(
            @PathParam("organization") @DefaultValue("") final String organizationName,
            @PathParam("publication") @DefaultValue("") final String publicationName,
            @QueryParam("start") @DefaultValue("1") final int start,
            @QueryParam("limit") @DefaultValue("1") final int limit,
            @QueryParam("device-type") @DefaultValue("") final String deviceType,
            @QueryParam("from") @DefaultValue("") final String from,
            @QueryParam("order") @DefaultValue("desc") final String order) {
        if (Utils.isBlank(publicationName) || Utils.isBlank(organizationName)) {
            return Responses.clientError().entity("Organization or publication name may not be blank.").build();
        }
        if (Utils.isBlank(deviceType)) {
            return Responses.clientError().entity("Device Type can not be blank").build();
        }

        if (start <= 0 || limit <= 0) {
            return Responses.clientError().entity("Start & limit params should have positive non-zero values.").build();
        }

        Date fromDate = null;
        if (Utils.isBlank(from)) {
            fromDate = new DateMidnight().toDate();
        } else {
            ///parse dateStr to date
        }

        Publication publication = publicationService.getPublication(publicationName);
        if (publication == null || !Objects.equals(publication.getOrganization().getId(), organizationName)) {
            return Responses.notFound().build();
        }

        List<Issue> issueList = issueService.getIssueListByPublicationId(publication.getId());

        Date lastModifiedIssueDate = issueList.get(0).getUpdated();
        ResponseBuilder unmodifiedResponseBuilder = request.evaluatePreconditions(lastModifiedIssueDate, EntityTag.valueOf(Utils
                .createETagHeaderValue(lastModifiedIssueDate.getTime())));
        if (unmodifiedResponseBuilder != null) {
            return unmodifiedResponseBuilder.tag(String.valueOf(lastModifiedIssueDate.getTime())).lastModified(lastModifiedIssueDate).build();
        }

        SyndFeed feed = issueService.getIssuesAsAtomFeed(publication.getOrganization(), publication, start, limit,
                deviceType, fromDate, order, JerseyResourceLocator.from(uriInfo));

        return Response.ok(feed).tag(String.valueOf(lastModifiedIssueDate.getTime())).lastModified(lastModifiedIssueDate).build();
    }

    @GET
    @Path("/{issue}")
    @Produces(MediaType.APPLICATION_XHTML_XML)
    public Response getIssueDetail(
            @PathParam("organization") @DefaultValue("") final String organizationId,
            @PathParam("publication") @DefaultValue("") final String publicationId,
            @PathParam("issue") @DefaultValue("") final String issueId) {

        Issue issue = retrieveIssue(organizationId, publicationId, issueId);

        ResponseBuilder notModifiedResponseBuilder = request.evaluatePreconditions(issue.getUpdated(), EntityTag.valueOf(Utils
                .createETagHeaderValue(issue.getVersion())));
        if (notModifiedResponseBuilder != null) {
            return notModifiedResponseBuilder.tag(String.valueOf(issue.getVersion())).lastModified(issue.getUpdated()).build();
        }

        Map<String, Object> model = new HashMap<>();
        model.put("organization", issue.getPublication().getOrganization());
        model.put("publication", issue.getPublication());
        model.put("issue", issue);

        ResourceLocator locator = JerseyResourceLocator.from(uriInfo);
        model.put("binaryUri", locator.getEpubBinaryURI(organizationId, publicationId, issueId));
        model.put("containerUri",
                locator.getEpubContentURI(organizationId, publicationId, issueId, "META-INF/container.xml"));

        return Response.ok(new Viewable("/issueDetail", model)).tag(String.valueOf(issue.getVersion())).lastModified(issue.getUpdated()).build();
    }

    @Path("/{issue}/{contentLocInEpub: .+}")
    @GET
    public Response getEpubContent(@PathParam("organization") @DefaultValue("") final String organizationId,
            @PathParam("publication") @DefaultValue("") final String publicationId,
            @PathParam("issue") @DefaultValue("") final String issueId,
            @PathParam("contentLocInEpub") @DefaultValue("") final String contentLocInEpub) throws IOException {

        Issue issue = retrieveIssue(organizationId, publicationId, issueId);
        if (isBlank(contentLocInEpub)) {
            return Responses.clientError().entity("Content location may not be blank.").build();
        }

        String fileId = Long.toString(issue.getEpubFile().getId());
        URI resourceUri = URI.create(fileId);
        URI fragmentPath = URI.create(contentLocInEpub);

        String fileName = fragmentPath.getPath();
        String fileLocationPath = cacheDirFullPath + fileSystemSeperator + fileId + fileSystemSeperator + fileName;
        File resourceFile = new File(fileLocationPath);

        if (resourceFile.exists()) {
            ResponseBuilder notModifiedResponseBuilder = request.evaluatePreconditions(new Date(resourceFile.lastModified()), EntityTag.valueOf(Utils
                    .createETagHeaderValue(resourceFile.lastModified())));
            if (notModifiedResponseBuilder != null) {
                return notModifiedResponseBuilder.tag(String.valueOf(resourceFile.lastModified())).lastModified(new Date(resourceFile.lastModified())).build();
            }
        }

        boolean exceptionHappened = false;
        InputStream binaryStream = null;
        try {
           binaryStream = storage.getFragment(resourceUri, fragmentPath);

            final InputStream finalVarBinaryStream = binaryStream;
            StreamingOutput sout = new StreamingOutput() {
                public void write(OutputStream outputStream) throws IOException {
                    try (InputStream resource = finalVarBinaryStream) {
                        ByteStreams.copy(resource, outputStream);
                    } catch (Throwable t) {
                        log.error(String.format("Error sending file: %s/%s", issueId, contentLocInEpub), t);
                        throw t;
                    }
                }
            };

            String mediaType = cciService.getMediaType(contentLocInEpub);
            if (mediaType == null) {
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
            }
            return Response.ok(sout, mediaType).tag(String.valueOf(resourceFile.lastModified())).lastModified(new Date(resourceFile.lastModified())).build();
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

    //testing purpose
    @GET
    @Path("/{deviceIds}/upload")
    @Produces(MediaType.APPLICATION_XHTML_XML)
    public Response getUploadForm(@PathParam("organization") @DefaultValue("") final String organizationId,
                                 @PathParam("publication") @DefaultValue("") final String publicationId,
                                 @PathParam("deviceIds") @DefaultValue("") final String deviceIds) {
        Map<String, Object> model = new HashMap<>();
        URI uri = uriInfo.getBaseUriBuilder().path(IssueResource.class).path(IssueResource.class, "uploadEpub")
                .build(organizationId, publicationId, deviceIds);
        model.put("binaryUri", uri);
        return Response.ok(new Viewable("/upload", model)).build();
    }

    @POST
    @Path("/{deviceIds}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadEpub(
            @PathParam("organization") @DefaultValue("") final String organizationId,
            @PathParam("publication") @DefaultValue("") final String publicationId,
            @PathParam("deviceIds") @DefaultValue("") final String deviceIds,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) throws IOException {

        Set<String> deviceSet = Sets.newHashSet(deviceIds.split(","));
        if (deviceSet == null || deviceIds.isEmpty()) {
            return Responses.clientError().entity("Device Type must be required").build();
        }

        try {
            checkForValidPublication(organizationId, publicationId);
            checkValidFileContent(fileDetail);

            if (uploadedInputStream == null) {
                throw new NotFoundException();
            }
            issueService.uploadEpubFile(publicationId, fileDetail.getFileName(), deviceSet, uploadedInputStream);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new NotFoundException();
        } finally {
            Closeables.close(uploadedInputStream, true);
        }
        return Response.status(200).entity("File Successfully Uploaded").build();
    }

    @PUT
    @Path("/{deviceIds}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uodateEpub(@PathParam("organization") @DefaultValue("") final String organizationId,
                               @PathParam("publication") @DefaultValue("") final String publicationId,
                               @PathParam("deviceIds") @DefaultValue("") final String deviceIds,
                               @FormDataParam("epubFile") InputStream fileInputStream,
                               @FormDataParam("epubFile") FormDataContentDisposition epubDetail) throws Exception{

        Set<String> deviceSet = Sets.newHashSet(deviceIds.split(","));
        if (deviceSet == null || deviceIds.isEmpty() || fileInputStream == null) {
            return Responses.clientError().entity("Device Type and epub attachment must be required").build();
        }

        long epubId;
        try {
            checkForValidPublication(organizationId, publicationId);
            checkValidFileContent(epubDetail);
            Issue epubIssue = issueService.getIssueByPublicationAndDeviceIdAndIssue(publicationId, deviceSet.iterator().next(), epubDetail.getFileName());
            epubId = epubIssue.getEpubFile().getId();
            issueService.updateEpub(epubId, fileInputStream);
        } catch (NotFoundException ne) {
            throw ne;
        } catch (Exception e) {
            throw new NotFoundException("Epub updating problem");
        }
        finally {
            Closeables.close(fileInputStream, true);
        }

        return Response.ok("Epub Successfully Updated and id is :" + epubId).build();
    }


    @GET
    @Path("/{file: [^/]+.epub?}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response downloadEpub(
            @PathParam("organization") @DefaultValue("") final String organizationId,
            @PathParam("publication") @DefaultValue("") final String publicationId,
            @PathParam("file") @DefaultValue("") final String binaryFileName) throws IOException {

        final String issueId = Files.getNameWithoutExtension(binaryFileName);
        Issue issue = retrieveIssue(organizationId, publicationId, issueId);

        if (issue.getEpubFile() == null || issue.getEpubFile().getId() == 0) {
            throw new FileNotFoundException(String.format("No binary file for: %s", binaryFileName));
        }

        long binaryVersion = issue.getEpubFile().getVersion();
        ResponseBuilder unmodifiedResponseBuilder = request.evaluatePreconditions(EntityTag.valueOf(Utils
                .createETagHeaderValue(binaryVersion)));
        if (unmodifiedResponseBuilder != null) {
            return unmodifiedResponseBuilder.build();
        }

        InputStream binaryStream = null;
        boolean exceptionHappened = false;

        try {
            binaryStream = storage.get(URI.create(Long.toString(issue.getEpubFile().getId())));
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

    private void checkValidFileContent(FormDataContentDisposition fileDetail) {
        if (fileDetail == null || isBlank(fileDetail.getFileName())) {
            throw new NotFoundException("File Name is empty");
        }
        if (!fileDetail.getFileName().matches(".+.epub")) {
            throw new NotFoundException("Only epub type file can be uploaded");
        }

    }
    private void checkForValidPublication(String organizationId, String publicationId) throws WebApplicationException {
        if (isBlank(organizationId) || isBlank(publicationId)) {
            throw new WebApplicationException(Status.BAD_REQUEST);
        }

        Publication publication = issueService.getPublication(publicationId);
        if (publication == null ||
                !Objects.equals(publicationId, publication.getId()) ||
                !Objects.equals(organizationId, publication.getOrganization().getId())) {
            throw new NotFoundException();
        }
    }

}
