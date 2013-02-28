package com.cefalo.cci.dao;

import com.cefalo.cci.model.EpubFile;
import com.cefalo.cci.model.Issue;

import java.util.List;

public interface IssueDao {
    long getIssueCountByPublicationId(String publicationId);

    List<Issue> getIssueListByPublicationId(String publicationId);

    List<Issue> getIssueListByPublicationId(String publicationId, int start, int maxResult);

    EpubFile getEpubFile(long id);

    Issue getIssue(String id);
}
