package com.cefalo.cci.mapping;

import java.net.URI;

/**
 * FIXME: This is awkward! To get the URI of something, we need to know the ID
 * of all its parent resources. We need to revisit our URI scheme and resource
 * content.
 * 
 * @author partha
 * 
 */
public interface ResourceLocator {
    URI getOrganizationURI(final String organizationID);

    URI getPublicationURI(final String organizationID, final String publicationID);

    URI getIssueListURI(final String organizationID, final String publicationID);

    URI getIssueURI(final String organizationID, final String publicationID, final String issueID);

    URI getEpubBinaryURI(final String organizationID, final String publicationID, final String issueID);

    URI getEpubContentURI(final String organizationID, final String publicationID, final String issueID,
            final String contentLocation);
}
