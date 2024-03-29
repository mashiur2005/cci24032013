package com.cefalo.cci.service;

import com.cefalo.cci.mapping.ResourceLocator;
import com.cefalo.cci.model.Events;
import com.cefalo.cci.model.Issue;
import com.cefalo.cci.model.Organization;
import com.cefalo.cci.model.Publication;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndLink;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface IssueService {
    Issue getIssue(String issueId);

    Publication getPublication(String publicationId);

    List<Issue> getIssueListByPublicationId(String publicationId);

    List<Issue> getOldIssueList(Date date);

    SyndFeed getIssuesAsAtomFeed(Organization organization, Publication publication, long start, long limit,
            String deviceType, Date fromDate, String sortOrder, ResourceLocator resourceLocator);

    List<SyndLink> getLinks(long start, long limit, String deviceType, Date fromDate, String sortOrder, long total, String issueListUri);

    void writeAndUploadEpubFile(String organizationId, String publicationId, String fileName, Set<String> deviceSet, InputStream inputStream) throws IOException;

    Issue getIssueByPublicationAndDeviceIdAndIssue(String publicationId, String deviceId, String issueName);

    void updateEpub(long id, InputStream updateInputStream) throws IOException;

    void findDifferenceAndSaveToDb(URI uploadedFileUri, URI existingFileUri, long fileId, String fileName) throws Exception;


}
