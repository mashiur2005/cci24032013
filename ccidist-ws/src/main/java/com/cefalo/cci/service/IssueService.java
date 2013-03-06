package com.cefalo.cci.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.cefalo.cci.mapping.ResourceLocator;
import com.cefalo.cci.model.Issue;
import com.cefalo.cci.model.Organization;
import com.cefalo.cci.model.Publication;
import com.sun.syndication.feed.synd.SyndFeed;

import java.util.Date;
import java.util.Set;

public interface IssueService {
    Issue getIssue(String issueId);

    Publication getPublication(String publicationId);

    List<Issue> getIssueListByPublicationId(String publicationId);

    List<Issue> getOldIssueList(Date date);

    SyndFeed getIssuesAsAtomFeed(Organization organization, Publication publication, long start, long limit,
            String deviceType, ResourceLocator resourceLocator);

    void uploadEpubFile(String publicationId, String fileName, Set<String> deviceSet, InputStream inputStream) throws IOException;
}
