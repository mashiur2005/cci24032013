package com.cefalo.cci.service;

import com.cefalo.cci.model.Issue;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndLink;

import java.util.List;

public interface IssueService {
    List<Issue> getIssueListByPublicationName(String publicationName);
    List<String> getIssueNameAsList(String publicationName);
    SyndFeed getIssueAsAtomFeed(String contextPath, String organizationName, String publicationName, int start, int limit);
}
