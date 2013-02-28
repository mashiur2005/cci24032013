package com.cefalo.cci.service;

import java.util.List;

import com.cefalo.cci.mapping.ResourceLocator;
import com.cefalo.cci.model.Issue;
import com.cefalo.cci.model.Organization;
import com.cefalo.cci.model.Publication;
import com.sun.syndication.feed.synd.SyndFeed;

public interface IssueService {
    Issue getIssue(String issueId);

    List<Issue> getIssueListByPublicationId(String publicationId);

    SyndFeed getIssuesAsAtomFeed(Organization organization, Publication publication, int start, int limit,
            ResourceLocator resourceLocator);
}
