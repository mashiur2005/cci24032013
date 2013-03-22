package com.cefalo.cci.service;

import com.cefalo.cci.dao.IssueDao;
import com.cefalo.cci.mapping.ResourceLocator;
import com.cefalo.cci.model.Issue;
import com.cefalo.cci.model.MetaData;
import com.cefalo.cci.model.Organization;
import com.cefalo.cci.model.Publication;
import com.cefalo.cci.storage.CacheStorage;
import com.cefalo.cci.enums.MetaDataInfo;
import com.cefalo.cci.utils.Utils;
import com.cefalo.cci.utils.XpathUtils;
import com.google.common.base.Splitter;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
    private EventService eventService;

    @Inject
    @Named("cacheEpubDirFullPath")
    private String cacheEpubDirFullPath;

    @Inject
    @Named("tmpDirFullPath")
    private String tmpDirFullPath;


    public Issue getIssue(String issueId) {
        // TODO: Maybe throw an exception from here if issue not found????
        return issueDao.getIssue(issueId);
    }

    public Publication getPublication(String publicationId) {
        return  issueDao.getPublication(publicationId);
    }


    @Override
    public List<Issue> getIssueListByPublicationId(String publicationId) {
        return issueDao.getIssueListByPublicationId(publicationId);
    }

    @Override
    public List<Issue> getOldIssueList(Date date) {
        return issueDao.getOldIssueList(date);
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

    @Override
    public List<SyndLink> getLinks(long start, long limit, String deviceType, Date fromDate, String sortOrder, long total, String issueListUri) {
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

/*
    @Transactional
    public void uploadEpubFile(String publicationId, String fileName, Set<String> deviceSet, InputStream inputStream) throws IOException {
        issueDao.uploadEpubFile(publicationId, fileName, deviceSet, inputStream);
    }
*/

    @Transactional
    public void writeAndUploadEpubFile(String organizationId, String publicationId, String fileName, Set<String> deviceSet, InputStream inputStream) throws IOException {
        URI epubResource;
        try {
            epubResource = cacheStorage.create(inputStream);
            processMetaDataAndCreateIssue(cacheEpubDirFullPath + epubResource.getPath(), "META-INF/container.xml",
                    organizationId, publicationId, deviceSet, Long.valueOf(epubResource.getPath()));
        } catch (IllegalArgumentException e) {
            throw e;
        }catch (IOException io) {
            io.printStackTrace();
            throw io;
        }
    }

    @Override
    public Issue getIssueByPublicationAndDeviceIdAndIssue(String publicationId, String deviceId, String issueName) {
        List<Issue> issueList = issueDao.getIssueByPublicationAndDeviceIdAndIssue(publicationId, deviceId, issueName, "desc");
        if (issueList.isEmpty()) {
            return null;
        }
        return issueList.get(0);
    }

    @Override
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
    @Transactional
    public void findDifferenceAndSaveToDb(URI uploadedFileUri, URI existingFileUri, long fileId, String fileName) throws IOException {
        InputStream fileInputStream = null;
        final Map<String, Boolean> visitedFiles = new HashMap<>();
        final Set<String> updatedSet = new HashSet<>();
        final Set<String> insertedSet = new HashSet<>();
        final Set<String> deletedSet = new HashSet<>();

        final Map<String, String> env = new HashMap<String, String>();
        env.put("create", "false");

        FileSystem uploadedFS = null;
        FileSystem existingFS = null;
        try {
            uploadedFS = FileSystems.newFileSystem(uploadedFileUri, env);
            existingFS = FileSystems.newFileSystem(existingFileUri, env);
            findDifference(existingFS, uploadedFS, visitedFiles, updatedSet, insertedSet);
            findDifference(uploadedFS, existingFS, visitedFiles, updatedSet, deletedSet);
            eventService.addEvents(fileId, updatedSet, insertedSet, deletedSet);
            fileInputStream = Utils.readFileFromDir(tmpDirFullPath + fileName);
            updateEpub(fileId, fileInputStream);
        } finally {
            Closeables.close(uploadedFS, true);
            Closeables.close(existingFS, true);
            Closeables.close(fileInputStream, true);
        }
    }




    public void findDifference(final FileSystem comparedFromFS, FileSystem comparedToFS, final Map<String, Boolean> visitedFiles,
                               final Set<String> updatedSet, final Set<String> newSet) throws IOException {

        try {
            java.nio.file.Files.walkFileTree(comparedToFS.getPath("/"), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path comparedToPath, BasicFileAttributes attrs)
                        throws IOException {
                    Path comparedFromPath = comparedFromFS.getPath(comparedToPath.toString());

                    if (visitedFiles.get(comparedToPath.toAbsolutePath().toString()) != null && visitedFiles.get(comparedToPath.toAbsolutePath().toString())) {
                        return FileVisitResult.CONTINUE;
                    } else if (java.nio.file.Files.exists(comparedFromPath.toAbsolutePath())) {
                        visitedFiles.put(comparedToPath.toAbsolutePath().toString(), true);

                        byte[] comparedToByte = java.nio.file.Files.readAllBytes(comparedToPath.toAbsolutePath());
                        byte[] comparedFromByte = java.nio.file.Files.readAllBytes(comparedFromPath.toAbsolutePath());
                        boolean isEqual = ByteStreams.equal(ByteStreams.newInputStreamSupplier(comparedToByte), ByteStreams.newInputStreamSupplier(comparedFromByte));
                        if (!isEqual) {
                            updatedSet.add(comparedToPath.toAbsolutePath().toString());
                        }
                    } else {
                        newSet.add(comparedToPath.toAbsolutePath().toString());
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
           throw e;
        }
    }

    public void processMetaDataAndCreateIssue(String fileLoc, String fileName, String organizationId, String publicationId, Set<String> deviceSet, long epubId) throws IOException {
        MetaData metaData = parseMetaData(fileLoc, fileName);

        if (!organizationId.equals(metaData.getCreator())) {
            throw new IllegalArgumentException("Creator is empty");
        }
        if (!publicationId.equals(metaData.getTitle())) {
            throw new IllegalArgumentException("Title is empty");
        }
        if (Utils.isBlank(metaData.getDevice()) || !Utils.areSetsEquals(deviceSet, Sets.newHashSet(Splitter.on(" , ").split(metaData.getDevice())))) {
            throw new IllegalArgumentException("Device is Null or device not found");
        }

        if (Utils.isBlank(metaData.getIssue())) {
            throw new IllegalArgumentException("Issue name is empty");
        }

        try {
            if (!Utils.isBlank(metaData.getDateStr())) {
                metaData.setDate(Utils.convertDateFormatTZ(metaData.getDateStr()));
            } else {
                metaData.setDate(Utils.convertDateWithTZ(new Date()));
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid date format");
        }
        issueDao.saveIssue(publicationId, metaData.getIssue(), metaData.getDate(), deviceSet, epubId);
    }

    public MetaData parseMetaData(String fileLoc, String fileName) throws IOException {
        Node node =  null;
        MetaData metaData = null;
        try {
            String content = Utils.getExtractedContent(fileLoc, "META-INF/container.xml");
            node = extractedContentNode("container/rootfiles/rootfile/@full-path", content);
            content = Utils.getExtractedContent(fileLoc, node.getTextContent());
            node = extractedContentNode("package/metadata", content);
            metaData = createMetaDateObject(node);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        }

        return metaData;
    }

    public Node extractedContentNode(String pattern, String content) throws IllegalArgumentException {
        XpathUtils xpathUtils = new XpathUtils();
        NodeList nodeList = xpathUtils.getNodeListFromHtml(pattern, content);
        if (nodeList == null) {
            throw new IllegalArgumentException("Can not extract epub file");
        }
        return nodeList.item(0);
    }

    public MetaData createMetaDateObject(Node node) throws IllegalArgumentException {
        if (!node.hasChildNodes()) {
            throw new IllegalArgumentException("MetaData is empty");
        }
        NodeList nodeList = node.getChildNodes();
        MetaData metaData = new MetaData();
        for (int i= 0; i < nodeList.getLength(); i++) {
             Node n = nodeList.item(i);
            if (MetaDataInfo.DC_CREATOR.getValue().equals(n.getNodeName())) {
                metaData.setCreator(n.getTextContent());
            } else if (MetaDataInfo.DC_DATE.getValue().equals(n.getNodeName())) {
                metaData.setDateStr(n.getTextContent());
            } else if (MetaDataInfo.DC_DEVICE.getValue().equals(n.getNodeName())) {
                metaData.setDevice(n.getTextContent());
            } else if (MetaDataInfo.DC_IDENTIFIER.getValue().equals(n.getNodeName())) {
                metaData.setIdentifier(n.getTextContent());
            } else if (MetaDataInfo.DC_ISSUE.getValue().equals(n.getNodeName())) {
                metaData.setIssue(n.getTextContent());
            } else if (MetaDataInfo.DC_LANGUAGE.getValue().equals(n.getNodeName())) {
                metaData.setLanguage(n.getTextContent());
            } else if (MetaDataInfo.DC_TITLE.getValue().equals(n.getNodeName())) {
                metaData.setTitle(n.getTextContent());
            }
        }
        return metaData;
    }
}
