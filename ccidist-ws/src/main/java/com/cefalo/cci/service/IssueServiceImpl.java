package com.cefalo.cci.service;

import com.cefalo.cci.dao.IssueDao;
import com.cefalo.cci.mapping.ResourceLocator;
import com.cefalo.cci.model.Issue;
import com.cefalo.cci.model.Organization;
import com.cefalo.cci.model.Publication;
import com.cefalo.cci.storage.CacheStorage;
import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.persist.Transactional;
import com.sun.syndication.feed.synd.*;
import org.joda.time.DateMidnight;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

public class IssueServiceImpl implements IssueService {

    @Inject
    private IssueDao issueDao;

    @Inject
    private CacheStorage cacheStorage;

    @Inject
    @Named("cacheEpubDirFullPath")
    private String cacheEpubDirFullPath;

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

    @Transactional
    public void uploadEpubFile(String publicationId, String fileName, Set<String> deviceSet, InputStream inputStream) throws IOException {
        issueDao.uploadEpubFile(publicationId, fileName, deviceSet, inputStream);
    }

    @Transactional
    public void writeAndUploadEpubFile(String publicationId, String fileName, Set<String> deviceSet, InputStream inputStream) throws IOException {
        URI epubResource;
        try {
            epubResource = cacheStorage.create(inputStream);
        } catch (IOException io) {
            io.printStackTrace();
            throw io;
        }
        issueDao.saveIssue(publicationId, fileName, deviceSet, Long.valueOf(epubResource.getPath()));
    }

    @Override
    public Issue getIssueByPublicationAndDeviceIdAndIssue(String publicationId, String deviceId, String issueName) {
        List<Issue> issueList = issueDao.getIssueByPublicationAndDeviceIdAndIssue(publicationId, deviceId, issueName, "desc");
        if (issueList == null || issueList.size() == 0) {
            return null;
        }
        return issueList.get(0);
    }

    @Override
    @Transactional
    public void updateEpub(long id, InputStream updateInputStream) throws IOException{
        URI resourceId = URI.create(Long.toString(id));
        try {
            resourceId = cacheStorage.delete(resourceId);
            cacheStorage.update(resourceId, updateInputStream);
        } catch (IOException e) {
            throw e;
        }
    }


    @Override
    public void findDifferenceAndSaveToDb(URI uploadedFileUri, URI existingFileUri) throws IOException {

        final Map<String, String> visitedFiles = new HashMap<>();
        final Set<String> uploadedChangedSet = new HashSet<>();
        final Set<String> existingChangedSet = new HashSet<>();

        final Map<String, String> env = new HashMap<String, String>();
        env.put("create", "false");

        FileSystem uploadedFS = null;
        FileSystem existingFS = null;
        try {
            uploadedFS = FileSystems.newFileSystem(uploadedFileUri, env);
            existingFS = FileSystems.newFileSystem(existingFileUri, env);
            findDifference(existingFS, uploadedFS, visitedFiles, uploadedChangedSet);
            findDifference(uploadedFS, existingFS, visitedFiles, existingChangedSet);
            showModifiedFiles(uploadedChangedSet, existingChangedSet);
        } finally {
            Closeables.close(uploadedFS, true);
            Closeables.close(existingFS, true);
        }

    }


    public void findDifference(final FileSystem comparedFromFS, FileSystem comparedToFS, final Map<String, String> visitedFiles, final Set<String> changedSet) throws IOException {

        try {
            java.nio.file.Files.walkFileTree(comparedToFS.getPath("/"), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path comparedToPath, BasicFileAttributes attrs)
                        throws IOException {
                    Path comparedFromPath = comparedFromFS.getPath(comparedToPath.toString());

/*
                    if (visitedFiles.get(comparedToPath.toAbsolutePath().toString()) != null) {
                        return FileVisitResult.CONTINUE;
                    } else
*/
                    if (java.nio.file.Files.exists(comparedFromPath.toAbsolutePath())) {

                        visitedFiles.put(comparedToPath.toAbsolutePath().toString(), "visited");

                        byte[] comparedToByte = java.nio.file.Files.readAllBytes(comparedToPath.toAbsolutePath());
                        byte[] comparedFromByte = java.nio.file.Files.readAllBytes(comparedFromPath.toAbsolutePath());
                        boolean isEqual = ByteStreams.equal(ByteStreams.newInputStreamSupplier(comparedToByte),
                                ByteStreams.newInputStreamSupplier(comparedFromByte));

                        if (!isEqual) {
                            changedSet.add(comparedToPath.toAbsolutePath().toString());
                            //visitedFiles.put(comparedToPath.toAbsolutePath().toString(), "visited");
                        }
                    } else {
                        changedSet.add(comparedToPath.toAbsolutePath().toString());
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
           throw e;
        }


    }

    public void showModifiedFiles(Set<String> uploadedChangedSet, Set<String> existingChangedSet) {
        if (uploadedChangedSet.isEmpty() && existingChangedSet.isEmpty()) {
            System.out.println("No file modified");
        } else {
            Set<String> intersectionSet;
            intersectionSet = Sets.intersection(uploadedChangedSet, existingChangedSet);

            if (!intersectionSet.isEmpty()) {
                System.out.println("File Modified:");

                for (String link : intersectionSet) {
                    System.out.println("---> " + link);
                }
            }

            Set<String> diffSet = Sets.difference(uploadedChangedSet, intersectionSet);

            if (!diffSet.isEmpty()) {
                System.out.println("File Added:");

                for (String link : diffSet) {
                    System.out.println("---> " + link);
                }

            }

            diffSet = Sets.difference(existingChangedSet, intersectionSet);

            if (!diffSet.isEmpty()) {
                System.out.println("File Deleted:");

                for (String link : diffSet) {
                    System.out.println("---> " + link);
                }

            }
        }
    }

}
