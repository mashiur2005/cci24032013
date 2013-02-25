package com.cefalo.cci.service;

import com.sun.syndication.feed.synd.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CciServiceImpl implements CciService {
    @Override
    public List<String> getAllFileNamesInDirectory(String dirPath) {
        if (dirPath == null) {
            dirPath = "";
        }
        File dir = new File(dirPath);
        List<String> epubFileNames = new ArrayList<String>();

        if (dir.isDirectory()) {
            List<File> files = (List<File>) FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
            for (File file : files) {
                epubFileNames.add(file.getName());
            }
        }

        return epubFileNames;
    }

    public List<SyndLink> getLinks(int start, int limit, String organizationName, String publicationName, int totalFile) {
        if (start < 0 || limit < 0 || start > totalFile || limit > totalFile || (start > 0 && limit > 0 && start + limit > totalFile)) {
            return new ArrayList<SyndLink>();
        }

        List<SyndLink> links = new ArrayList<SyndLink>();
        int prevStart = 0;
        int prevLimit = 0;
        int selfStart = 0;
        int selfLimit = 0;
        int nextStart = 0;
        int nextLimit = 0;
        boolean addPrev = true;

        if (start > limit) {
            prevStart = start - limit;
            selfStart = start;
            nextStart = start + limit;

            prevLimit = limit;
            selfLimit = limit;
            nextLimit = limit;
        } else if (start < limit && start > 1) {
            prevStart = 1;
            selfStart = start;
            nextStart = start + limit;

            prevLimit = start - prevStart;
            selfLimit = limit;
            nextLimit = limit;
        } else if (start == 1) {
            addPrev = false;
            selfStart = start;
            nextStart = start + limit;

            selfLimit = limit;
            nextLimit = limit;
        }

        if (addPrev) {
            SyndLink prev = new SyndLinkImpl();
            prev.setRel("prev");
            prev.setHref("/" + organizationName + "/" + publicationName + "/issues" + "?limit=" + prevLimit + "&start=" + prevStart);
            links.add(prev);
        }

        SyndLink self = new SyndLinkImpl();
        self.setRel("self");
        self.setHref("/" + organizationName + "/" + publicationName + "/issues" + "?limit=" + selfLimit + "&start=" + selfStart);
        SyndLink next = new SyndLinkImpl();
        next.setRel("next");
        next.setHref("/" + organizationName + "/" + publicationName + "/issues" + "?limit=" + nextLimit + "&start=" + nextStart);

        links.add(self);
        links.add(next);

        return links;
    }

    @Override
    public SyndFeed getIssueAsAtomFeed(String organizationName, String publicationName, String fileDir, int start, int limit) {
        String feedType = "atom_1.0";
        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType(feedType);
        feed.setTitle(publicationName + " Issues");
        feed.setUri("feed Id test");
        feed.setPublishedDate(new Date());

        SyndPerson syndPerson = new SyndPersonImpl();
        syndPerson.setName(publicationName);

        feed.getAuthors().add(syndPerson);

        List<String> fileNameList = getAllFileNamesInDirectory(fileDir);

        List<SyndLink> links = getLinks(start, limit, organizationName, publicationName, fileNameList.size());

        if (!links.isEmpty()) {
            List<SyndEntry> entries = new ArrayList<SyndEntry>();
            SyndEntry syndEntry;

            for (String aFileNameList : fileNameList.subList(start, start + limit)) {
                syndEntry = new SyndEntryImpl();
                syndEntry.setUri("entry Id test");
                syndEntry.setUpdatedDate(new Date());
                syndEntry.setTitle(aFileNameList);
                syndEntry.setAuthor(publicationName);
                syndEntry.setLink("/" + organizationName + "/" + publicationName + "/" + StringUtils.remove(aFileNameList, ".epub"));
                entries.add(syndEntry);
            }

            feed.setLinks(links);
            feed.setEntries(entries);
        }
        return feed;
    }
}
