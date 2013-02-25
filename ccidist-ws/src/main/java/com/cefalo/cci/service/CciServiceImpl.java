package com.cefalo.cci.service;

import com.sun.syndication.feed.synd.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
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

    @Override
    public SyndFeed getIssueAsAtomFeed(String feedType, String organizationName, String publicationName, String fileDir) {
        List<SyndLink> links = new ArrayList<SyndLink>();

        SyndLink prev = new SyndLinkImpl();
        prev.setRel("prev");
        prev.setHref("/" + organizationName + "/" + publicationName + "/issueList" + "?limit=4&amp;start=1");

        SyndLink self = new SyndLinkImpl();
        self.setRel("self");
        self.setHref("/" + organizationName + "/" + publicationName + "/issueList" + "?limit=4&amp;start=5");
        SyndLink next = new SyndLinkImpl();
        next.setRel("next");
        next.setHref("/" + organizationName + "/" + publicationName + "/issueList" + "?limit=4&amp;start=9");

        links.add(self);
        links.add(prev);
        links.add(next);

        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType(feedType);

        feed.setTitle(publicationName + " Issues");
        feed.setLinks(links);
        feed.setAuthor(publicationName);

        List<String> fileNameList = getAllFileNamesInDirectory(fileDir);

        List<SyndEntry> entries = new ArrayList<SyndEntry>();
        SyndEntry syndEntry;

        for (String aFileNameList : fileNameList) {
            syndEntry = new SyndEntryImpl();
            syndEntry.setTitle(aFileNameList);
            syndEntry.setAuthor(publicationName);
            syndEntry.setLink("/" + organizationName + "/" + publicationName + "/" + StringUtils.remove(aFileNameList, ".epub"));
            entries.add(syndEntry);
        }

        feed.setEntries(entries);
        return feed;
    }
}
