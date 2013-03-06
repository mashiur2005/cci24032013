package com.cefalo.cci.dao;

import com.cefalo.cci.model.EpubFile;
import com.cefalo.cci.model.Issue;
import com.cefalo.cci.model.Publication;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

public interface IssueDao {
    long getIssueCountByPublicationId(String publicationId);

    long getIssueCountByPublicationAndDeviceId(String publicationId, String deviceType);

    List<Issue> getIssueListByPublicationId(String publicationId);

    List<Issue> getIssueListByPublicationId(String publicationId, long start, long maxResult);

    List<Issue> getIssueListByPublicationAndDeviceId(String publicationId, long start, long maxResult, String deviceType);

    EpubFile getEpubFile(long id);

    Issue getIssue(String id);

    Publication getPublication(String id);

    void uploadEpubFile(String publicationId, String fileName, InputStream inputStream) throws IOException;

    List<Issue> getOldIssueList(Date date);

}
