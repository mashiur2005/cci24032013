package com.cefalo.cci.service;

import com.cefalo.cci.dao.IssueDao;
import com.cefalo.cci.model.Issue;
import com.google.inject.Inject;

import java.util.List;

public class IssueServiceImpl implements IssueService {
    @Inject
    private IssueDao issueDao;

    @Override
    public List<Issue> getIssueListByPublicationName(String publicationName) {
        return issueDao.getIssueListByPublicationName(publicationName);
    }

    @Override
    public List<String> getIssueNameAsList(String publicationName) {
        return issueDao.getIssueNameAsList(publicationName);
    }
}
