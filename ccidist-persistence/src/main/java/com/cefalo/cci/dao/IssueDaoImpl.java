package com.cefalo.cci.dao;

import com.cefalo.cci.model.EpubFile;
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
    public List<Issue> getIssueListByPublicationId(String publicationId) {
        return entityManager.createQuery("select i from Issue i where i.publication.id like :pName")
                .setParameter("pName", publicationId).getResultList();
    }

    @Override
    @Transactional
    public EpubFile getEpubFile(long id) {
        return entityManager.find(EpubFile.class, id);
    }

    @Override
    @Transactional
    public Issue getIssue(String id) {
        return entityManager.find(Issue.class, id);
    }
}
