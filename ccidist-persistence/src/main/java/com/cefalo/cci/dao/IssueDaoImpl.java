package com.cefalo.cci.dao;

import com.cefalo.cci.model.Issue;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

public class IssueDaoImpl implements IssueDao {
    @Inject
    private EntityManager entityManager;

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public List<Issue> getIssueListByPublicationName(String publicationName) {
        return (List<Issue>) entityManager.createQuery("from Issue ").getResultList();
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public List<String> getIssueNameAsList(String publicationName) {
        return (List<String>)entityManager.createQuery("select i from Issue i where i.name like :pName").setParameter("pName", publicationName).getResultList();
    }
}
