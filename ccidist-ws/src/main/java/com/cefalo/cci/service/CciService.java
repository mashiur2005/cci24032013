package com.cefalo.cci.service;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndLink;

import java.util.List;

public interface CciService {
    List<String> getAllFileNamesInDirectory(String dirPath);
    SyndFeed getIssueAsAtomFeed(List<String> fileNameList, String contextPath, String organizationName, String publicationName, int start, int limit);
    List<SyndLink> getLinks(int start, int limit, String organizationName, String publicationName, int totalFile);
    String getMediaType(String contentLocInEpub);
}
