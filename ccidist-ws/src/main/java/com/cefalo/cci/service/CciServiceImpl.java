package com.cefalo.cci.service;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.sun.syndication.feed.synd.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.google.common.io.Files.getNameWithoutExtension;

public class CciServiceImpl implements CciService {
	private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public List<String> getAllFileNamesInDirectory(final String directory) {
    	String dir = Strings.nullToEmpty(directory);
    	Preconditions.checkArgument(dir.trim().length() > 0, "Directory path may not be empty or null.");
    	
    	final List<String> epubFileNames = new ArrayList<String>();
    	try {
			final Path directoryPath = Paths.get(directory);
			Files.walkFileTree(directoryPath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					epubFileNames.add(file.getFileName().toString());
					return super.visitFile(file, attrs);
				}
				
				@Override
				public FileVisitResult preVisitDirectory(Path dir,
						BasicFileAttributes attrs) throws IOException {
					if (dir.equals(directoryPath)) {
						return super.preVisitDirectory(dir, attrs);
					}
					
					return FileVisitResult.SKIP_SUBTREE;
				}
			});
		} catch (IOException e) {
			logger.error("Error while trying to get the list of files.", e);
		}

        return epubFileNames;
    }

    @Override
    public List<SyndLink> getLinks(int start, int limit, String organizationName, String publicationName, int totalFile) {
        List<SyndLink> links = new ArrayList<SyndLink>();

        if (start <= 0 || limit <= 0 || start > totalFile) {
            return links;
        }

        if (start > 0 && limit > 0 && start + limit - 1 > totalFile) {
            SyndLink self = new SyndLinkImpl();
            self.setRel("self");
            self.setHref("/" + organizationName + "/" + publicationName + "/issue" + "?limit=" + totalFile + "&start=" + start);
            links.add(self);
            return links;
        }

        int prevStart = 0;
        int prevLimit = 0;
        int selfStart = 0;
        int selfLimit = 0;
        int nextStart = 0;
        int nextLimit = 0;
        boolean addPrev = true;
        boolean addNext = true;

        if (start + limit - 1 == totalFile) {
            addNext = false;
        }

        if (start > limit) {
            prevStart = start - limit;
            selfStart = start;
            nextStart = start + limit;

            prevLimit = limit;
            selfLimit = limit;
            int left = totalFile - (start + limit) + 1;
            nextLimit = limit < left ? limit : left;
        } else if (start < limit && start > 1) {
            prevStart = 1;
            selfStart = start;
            nextStart = start + limit;

            prevLimit = start - prevStart;
            selfLimit = limit;
            int left = totalFile - (start + limit) + 1;
            nextLimit = limit < left ? limit : left;
        } else if (start == 1) {
            addPrev = false;
            selfStart = start;
            nextStart = start + limit;

            selfLimit = limit;
            int left = totalFile - (start + limit) + 1;
            nextLimit = limit < left ? limit : left;
        } else if (start == limit) {
            prevStart = start - limit + 1;
            selfStart = start;
            nextStart = start + limit;

            selfLimit = limit;
            prevLimit = start - prevStart;
            int left = totalFile - (start + limit) + 1;
            nextLimit = limit < left ? limit : left;
        }

        if (addPrev) {
            SyndLink prev = new SyndLinkImpl();
            prev.setRel("prev");
            prev.setHref("/" + organizationName + "/" + publicationName + "/issue" + "?limit=" + prevLimit + "&start=" + prevStart);
            links.add(prev);
        }

        SyndLink self = new SyndLinkImpl();
        self.setRel("self");
        self.setHref("/" + organizationName + "/" + publicationName + "/issue" + "?limit=" + selfLimit + "&start=" + selfStart);
        links.add(self);
        if (addNext) {
            SyndLink next = new SyndLinkImpl();
            next.setRel("next");
            next.setHref("/" + organizationName + "/" + publicationName + "/issue" + "?limit=" + nextLimit + "&start=" + nextStart);
            links.add(next);
        }

        return links;
    }

    @SuppressWarnings("unchecked")
	@Override
    public SyndFeed getIssueAsAtomFeed(List<String> fileNameList,String contextPath, String organizationName, String publicationName, int start, int limit) {
        String feedType = "atom_1.0";
        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType(feedType);
        feed.setTitle(publicationName + " Issues");
        feed.setUri("feed Id test");
        feed.setPublishedDate(new Date());

        SyndPerson syndPerson = new SyndPersonImpl();
        syndPerson.setName(publicationName);

        feed.getAuthors().add(syndPerson);

        List<SyndLink> links = getLinks(start, limit, organizationName, publicationName, fileNameList.size());

        List<SyndEntry> entries = new ArrayList<SyndEntry>();
        if (!links.isEmpty()) {
            SyndEntry syndEntry;
            int toIndex = 0;
            if (start + limit - 1 > fileNameList.size()) {
                toIndex = fileNameList.size();
            } else {
                toIndex = start + limit - 1;
            }

            for (String fileName : fileNameList.subList(start - 1, toIndex)) {
                syndEntry = new SyndEntryImpl();
                syndEntry.setUri("entry Id test");
                syndEntry.setUpdatedDate(new Date());
                syndEntry.setTitle(fileName);
                syndEntry.setAuthor(publicationName);
                syndEntry.setLink(contextPath + organizationName + "/" + publicationName + "/issue/" + getNameWithoutExtension(fileName));
                entries.add(syndEntry);
            }

        } else {
            SyndLink self = new SyndLinkImpl();
            self.setRel("self");
            self.setHref(contextPath + organizationName + "/" + publicationName + "/issue?start=0&limit=0");
            links.add(self);
        }

        feed.setLinks(links);
        feed.setEntries(entries);

        return feed;
    }

    public String getMediaType(String contentLocInEpub) {
        try {
            return Files.probeContentType(Paths.get(contentLocInEpub));
        } catch (IOException e) {
            return null;
        }
    }
}
