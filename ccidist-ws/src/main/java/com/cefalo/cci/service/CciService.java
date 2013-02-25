package com.cefalo.cci.service;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndLink;

import java.util.List;

public interface CciService {
    List<String> getAllFileNamesInDirectory(String dirPath);
    SyndFeed getIssueAsAtomFeed(String organizationName, String publicationName, String fileDir, int start, int limit);
    List<SyndLink> getLinks(int start, int limit, String organizationName, String publicationName, int totalFile);
}
