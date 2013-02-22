package com.cefalo.cci.service;

import com.sun.syndication.feed.synd.SyndFeed;

import java.util.List;

public interface CciService {
    List<String> getAllFileNamesInDirectory(String dirPath);
    SyndFeed getIssueAsAtomFeed(String feedType, String organizationName, String publicationName, String fileDir);
}
