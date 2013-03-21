package com.cefalo.cci.dao;

import com.cefalo.cci.model.EpubFile;
import com.cefalo.cci.model.Issue;
import com.cefalo.cci.model.Publication;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface IssueDao {
    long getIssueCountByPublicationId(String publicationId);

    long getIssueCountByPublicationAndDeviceId(String publicationId, String deviceType, Date fromDate);

    List<Issue> getIssueListByPublicationId(String publicationId);

    List<Issue> getIssueListByPublicationId(String publicationId, long start, long maxResult);

    List<Issue> getIssueListByPublicationAndDeviceId(String publicationId, long start, long maxResult, String deviceType, Date fromDate, String sortOrder);

    EpubFile getEpubFile(long id);

    Issue getIssue(String id);

    Publication getPublication(String id);

   // void uploadEpubFile(String publicationId, String fileName, Set<String> deviceSet, InputStream inputStream) throws IOException;

    List<Issue> getOldIssueList(Date date);

    List<Issue> getIssueByPublicationAndDeviceIdAndIssue(String publicationId, String deviceId, String issueName, String sortOrder);

    void updateEpub(long id, InputStream updateInputStream);

    URI saveEpub(InputStream epubInputStream) throws IOException;

    void saveIssue(String publicationId, String fileName, Date date, Set<String> deviceSet, long epubId);
}
