package com.cefalo.cci.service;

import com.cefalo.cci.dao.IssueDao;
import com.cefalo.cci.mapping.ResourceLocator;
import com.cefalo.cci.model.Issue;
import com.cefalo.cci.model.Organization;
import com.cefalo.cci.model.Publication;
import com.cefalo.cci.storage.CacheStorage;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.syndication.feed.synd.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.joda.time.DateMidnight;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.google.common.base.Preconditions.checkArgument;

@Singleton
public class IssueServiceImpl implements IssueService {
    private  final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    private IssueDao issueDao;

    @Inject
    private CacheStorage cacheStorage;

    public Issue getIssue(String issueId) {
        // TODO: Maybe throw an exception from here if issue not found????
        return issueDao.getIssue(issueId);
    }

    public Publication getPublication(String publicationId) {
        return  issueDao.getPublication(publicationId);
    }


    @Override
    public List<Issue> getIssueListByPublicationId(String publicationId) {
        List<Issue> issueList = issueDao.getIssueListByPublicationId(publicationId);
        if (issueList == null) {
            new ArrayList<Issue>();
        }
        return issueList;
    }

    @Override
    public List<Issue> getOldIssueList(Date date) {
        List<Issue> issueList = issueDao.getOldIssueList(date);
        if (issueList == null) {
            return new ArrayList<Issue>();
        }
        return issueList;
    }

    @Override
    public SyndFeed getIssuesAsAtomFeed(
            Organization organization,
            Publication publication,
            long start,
            long limit,
            String deviceType,
            Date fromDate,
            String sortOrder,
            ResourceLocator resourceLocator) {
        checkArgument(start > 0 && limit > 0);

        // Remember that the DB layer expects 0 based indexing while we use 1 based indexing in the resource layer.
        return getIssueAsAtomFeed(
                issueDao.getIssueListByPublicationAndDeviceId(publication.getId(), start - 1, limit, deviceType, fromDate, sortOrder),
                organization,
                publication,
                start,
                limit,
                deviceType,
                fromDate,
                sortOrder,
                (int)issueDao.getIssueCountByPublicationAndDeviceId(publication.getId(), deviceType, fromDate),
                resourceLocator);
    }

    @SuppressWarnings("unchecked")
    SyndFeed getIssueAsAtomFeed(List<Issue> issues, Organization organization, Publication publication,
            long start, long limit, String deviceType, Date fromDate, String sortOrder, long total, ResourceLocator resourceLocator) {
        String publicationName = publication.getName();
        String organizationName = organization.getName();

        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("atom_1.0");
        feed.setTitle(String.format("%s issues", publicationName));
        feed.setUri(resourceLocator.getIssueListURI(organizationName, publicationName).toString());
        feed.setPublishedDate(new Date());

        SyndPerson syndPerson = new SyndPersonImpl();
        syndPerson.setName(publicationName);
        feed.getAuthors().add(syndPerson);

        List<SyndLink> links = getLinks(start, limit, deviceType, fromDate, sortOrder, total,
                resourceLocator.getIssueListURI(organization.getId(), publication.getId()).toString());
        feed.setLinks(links);

        List<SyndEntry> entries = new ArrayList<SyndEntry>();
        for (Issue issue : issues) {
            SyndEntry syndEntry = new SyndEntryImpl();
            syndEntry.setUri("urn:uuid:".concat(issue.getId()));
            syndEntry.setUpdatedDate(issue.getUpdated());
            syndEntry.setTitle(issue.getName());
            syndEntry.setAuthor(publicationName);
            syndEntry.setLink(resourceLocator.getIssueURI(organization.getId(), publication.getId(), issue.getId())
                    .toString());
            entries.add(syndEntry);
        }
        feed.setEntries(entries);

        return feed;
    }

    List<SyndLink> getLinks(long start, long limit, String deviceType, Date fromDate, String sortOrder, long total, String issueListUri) {
        List<SyndLink> links = new ArrayList<SyndLink>();
        links.add(createAtomLink("self", start, limit, deviceType, fromDate, sortOrder, issueListUri));

        if (start > 1) {
            // There is a prev link
            links.add(createAtomLink("prev", Math.max(1, start - limit), limit, deviceType, fromDate, sortOrder, issueListUri));
        }
        if ((start + limit) < (total + 1)) {
            // There is a next link
            links.add(createAtomLink("next", Math.min(start + limit, total), limit, deviceType, fromDate, sortOrder, issueListUri));
        }

        return links;
    }

    private SyndLink createAtomLink(String relation, long start, long limit, String deviceType, Date fromDate, String sortOrder, String baseIssueListUri) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        String fromDateStr = fmt.print(new DateMidnight(fromDate));

        SyndLink self = new SyndLinkImpl();
        self.setRel(relation);
        self.setHref(String.format("%s?start=%s&limit=%s&device-type=%s&sortOrder=%s&from=%s", baseIssueListUri, start, limit, deviceType, sortOrder, fromDateStr));
        return self;
    }

    public void uploadEpubFile(String publicationId, String fileName, Set<String> deviceSet, InputStream inputStream) throws IOException {
        issueDao.uploadEpubFile(publicationId, fileName, deviceSet, inputStream);
    }

    @Override
    public Issue getIssueByPublicationAndDeviceIdAndIssue(String publicationId, String deviceId, String issueName) {
        List<Issue> issueList = issueDao.getIssueByPublicationAndDeviceIdAndIssue(publicationId, deviceId, issueName, "desc");
        if (issueList == null) {
            return new Issue();
        }
        return issueList.get(0);
    }

    @Override
    public void updateEpub(long id, InputStream updateInputStream){
        issueDao.updateEpub(id, updateInputStream);
    }

    @Override
    public void findDifferenceAndSaveToDb(String newFilePath, String oldFilePath) throws Exception{
        InputStream newInputStream = null;
        InputStream oldInputStream = null;
        ZipInputStream newZipInputStream = null;
        ZipInputStream oldZipInputStream = null;
        ZipEntry newZipEntry;
        ZipEntry oldZipEntry;
        boolean isEqual;

        try{
            newInputStream = readFromTempFile(newFilePath);
            oldInputStream = readFromTempFile(oldFilePath);
            newZipInputStream = new ZipInputStream(newInputStream);
            oldZipInputStream = new ZipInputStream(oldInputStream);
            /*listOfFilesInDir(newZipInputStream, "new");
            listOfFilesInDir(oldZipInputStream, "old");*/
            newInputStream.close();
            oldInputStream.close();
            newZipInputStream.close();
            oldZipInputStream.close();

            newInputStream = readFromTempFile(newFilePath);
            oldInputStream = readFromTempFile(oldFilePath);
            newZipInputStream = new ZipInputStream(newInputStream);
            oldZipInputStream = new ZipInputStream(oldInputStream);

            newZipEntry = newZipInputStream.getNextEntry();
            oldZipEntry = oldZipInputStream.getNextEntry();
            while (newZipEntry != null || oldZipEntry != null) {

                if (newZipEntry != null && oldZipEntry != null && newZipEntry.getName().equals(oldZipEntry.getName())) {
                    /*log.info("Comparing " + newZipEntry.getName() + " and " + oldZipEntry.getName());*/
                    isEqual = ByteStreams.equal(ByteStreams.newInputStreamSupplier(ByteStreams.toByteArray(oldZipInputStream)),
                            ByteStreams.newInputStreamSupplier(ByteStreams.toByteArray(newZipInputStream)));
                    if (isEqual) {
                        /*log.info("New file : " + newZipEntry.getName() + " and Old file : " + oldZipEntry.getName() + " are equal");*/
                    } else {
                        log.info("New file : " + newZipEntry.getName() + " and Old file : " + oldZipEntry.getName() + " are different");
                    }
                } else if (newZipEntry != null && oldZipEntry == null){
                    log.info("Added File : " + newZipEntry.getName());
                } else if (oldZipEntry != null && newZipEntry == null) {
                    log.info("Deleted File : " + oldZipEntry.getName());
                } else {
                    log.info(oldZipEntry.getName() + " is replaced by " + newZipEntry.getName());
                }
                newZipEntry = newZipInputStream.getNextEntry();
                oldZipEntry = oldZipInputStream.getNextEntry();
            }
        } catch (Exception io) {
            throw io;
        } finally {
            Closeables.close(newInputStream, true);
            Closeables.close(oldInputStream, true);
            Closeables.close(newZipInputStream, true);
            Closeables.close(oldZipInputStream, true);
        }
    }

    public void writeZipFileToTmpDir(InputStream inputStream, String fileAbsolutePath) throws Exception {
        File tmpFile;
        FileOutputStream tmpFileOutputStream = null;

        tmpFile = new File(fileAbsolutePath);
        try {
            Files.createParentDirs(tmpFile);
            tmpFileOutputStream = new FileOutputStream(tmpFile);
            ByteStreams.copy(inputStream, tmpFileOutputStream);
        } catch (FileNotFoundException fnfe) {
            throw fnfe;
        } catch (IOException io) {
            throw io;
        } finally {
            Closeables.close(tmpFileOutputStream, true);
        }
    }

    public InputStream readFromTempFile(String fileAbsolutePath) throws Exception {
        File tmpFile = new File(fileAbsolutePath);
        FileInputStream tmpFileInputStream = null;

        try {
            tmpFileInputStream = new FileInputStream(tmpFile);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            throw fnfe;
        }
        return tmpFileInputStream;
    }

    /*public void listOfFilesInDir(ZipInputStream zipInputStream, String type) throws Exception{
        System.out.println(type);
        ZipEntry zipEntry;
        try {
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                System.out.println(zipEntry.getName());
                *//*fileList.add(zipEntry.getName());*//*
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }*/

}
