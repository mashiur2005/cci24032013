package com.cefalo.cci.dao;

import com.cefalo.cci.model.Issue;

import java.util.List;

public interface IssueDao {
    List<Issue> getIssueListByPublicationName(String publicationName);
    List<String> getIssueNameAsList(String publicationName);
}
