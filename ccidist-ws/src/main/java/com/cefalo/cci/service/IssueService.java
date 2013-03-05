package com.cefalo.cci.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.cefalo.cci.mapping.ResourceLocator;
import com.cefalo.cci.model.Issue;
import com.cefalo.cci.model.Organization;
import com.cefalo.cci.model.Publication;
import com.sun.syndication.feed.synd.SyndFeed;

public interface IssueService {
    Issue getIssue(String issueId);

    Publication getPublication(String publicationId);

    List<Issue> getIssueListByPublicationId(String publicationId);

    SyndFeed getIssuesAsAtomFeed(Organization organization, Publication publication, long start, long limit,
            ResourceLocator resourceLocator);

    void uploadEpubFile(String publicationId, String fileName, InputStream inputStream) throws IOException;
}
