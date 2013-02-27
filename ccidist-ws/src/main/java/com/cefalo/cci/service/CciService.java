package com.cefalo.cci.service;

import java.util.List;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndLink;

public interface CciService {
    List<String> getAllFileNamesInDirectory(String dirPath);
    SyndFeed getIssueAsAtomFeed(String contextPath, String organizationName, String publicationName, String fileDir, int start, int limit);
    List<SyndLink> getLinks(int start, int limit, String organizationName, String publicationName, int totalFile);
    String getMediaType(String epubFileLoc, String contentLocInEpub);
}
