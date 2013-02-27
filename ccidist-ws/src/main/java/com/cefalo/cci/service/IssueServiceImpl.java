package com.cefalo.cci.service;

import com.cefalo.cci.dao.IssueDao;
import com.cefalo.cci.model.Issue;
import com.google.inject.Inject;
import com.sun.syndication.feed.synd.SyndFeed;

import java.util.List;

public class IssueServiceImpl implements IssueService {
    @Inject
    private IssueDao issueDao;
    @Inject
    private CciService cciService;

    @Override
    public List<Issue> getIssueListByPublicationName(String publicationName) {
        return issueDao.getIssueListByPublicationName(publicationName);
    }

    @Override
    public List<String> getIssueNameAsList(String publicationName) {
        return issueDao.getIssueNameAsList(publicationName);
    }


    @Override
    public SyndFeed getIssueAsAtomFeed(String contextPath, String organizationName, String publicationName, int start, int limit) {
        return cciService.getIssueAsAtomFeed(getIssueNameAsList(publicationName), contextPath, organizationName, publicationName, start, limit);
    }
}
