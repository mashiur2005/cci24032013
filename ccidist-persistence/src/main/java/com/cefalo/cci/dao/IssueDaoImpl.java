package com.cefalo.cci.dao;

import java.util.List;

import javax.persistence.EntityManager;

import com.cefalo.cci.model.EpubFile;
import com.cefalo.cci.model.Issue;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class IssueDaoImpl implements IssueDao {
    @Inject
    private EntityManager entityManager;

    @Override
    @Transactional
    public long getIssueCountByPublicationId(String publicationId) {
        return (Long) entityManager.createQuery("select count(i) from Issue i where i.publication.id like :pName")
                .setParameter("pName", publicationId).getSingleResult();
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public List<Issue> getIssueListByPublicationId(String publicationId) {
        return entityManager
                .createQuery("select i from Issue i where i.publication.id like :pName order by updated desc")
                .setParameter("pName", publicationId).getResultList();
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public List<Issue> getIssueListByPublicationId(String publicationId, long start, long maxResult) {
        return entityManager
                .createQuery("select i from Issue i where i.publication.id like :pName order by updated  desc")
                .setParameter("pName", publicationId).setFirstResult((int) start).setMaxResults((int) maxResult)
                .getResultList();
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
