package com.cefalo.cci.mapping;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.ws.rs.core.UriInfo;

import com.cefalo.cci.restResource.IssueResource;
import com.cefalo.cci.restResource.OrganizationResource;
import com.cefalo.cci.restResource.PublicationDetailResource;

public class JerseyResourceLocator implements ResourceLocator {
    private final UriInfo uriInfo;

    private JerseyResourceLocator(final UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    public static ResourceLocator from(final UriInfo uriInfo) {
        checkNotNull(uriInfo, "uriInfo may not be null");

        return new JerseyResourceLocator(uriInfo);
    }

    @Override
    public URI getOrganizationURI(final String organizationID) {
        checkNotNull(organizationID);
        return uriInfo.getBaseUriBuilder().path(OrganizationResource.class)
                .path(OrganizationResource.class, "getOrganizationDetail").build(organizationID);
    }

    @Override
    public URI getPublicationURI(final String organizationID, final String publicationID) {
        checkNotNull(organizationID);
        checkNotNull(publicationID);
        return uriInfo.getBaseUriBuilder().path(PublicationDetailResource.class).build(organizationID, publicationID);
    }

    @Override
    public URI getIssueListURI(final String organizationID, final String publicationID) {
        checkNotNull(organizationID);
        checkNotNull(publicationID);
        return uriInfo.getBaseUriBuilder().path(IssueResource.class).build(organizationID, publicationID);
    }

    @Override
    public URI getIssueURI(final String organizationID, final String publicationID, final String issueID) {
        checkNotNull(organizationID);
        checkNotNull(publicationID);
        checkNotNull(issueID);

        return uriInfo.getBaseUriBuilder().path(IssueResource.class).path(IssueResource.class, "getIssueDetail")
                .build(organizationID, publicationID, issueID);
    }

    @Override
    public URI getEpubBinaryURI(final String organizationID, final String publicationID, final String issueID) {
        checkNotNull(organizationID);
        checkNotNull(publicationID);
        checkNotNull(issueID);

        return uriInfo.getBaseUriBuilder().path(IssueResource.class).path(IssueResource.class, "downloadEpub")
                .build(organizationID, publicationID, issueID.concat(".epub"));
    }

    @Override
    public URI getEpubContentURI(final String organizationID, final String publicationID, final String issueID,
            final String contentLocation) {
        checkNotNull(organizationID);
        checkNotNull(publicationID);
        checkNotNull(issueID);
        checkNotNull(contentLocation);

        return uriInfo.getBaseUriBuilder().path(IssueResource.class).path(IssueResource.class, "getEpubContent")
                .build(organizationID, publicationID, issueID, contentLocation);
    }

}
