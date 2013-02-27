package com.cefalo.cci.service;

import com.cefalo.cci.model.Issue;

import java.util.List;

public interface IssueService {
    List<Issue> getIssueListByPublicationName(String publicationName);
    List<String> getIssueNameAsList(String publicationName);
}
