package com.cefalo.cci.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.util.List;

import javax.persistence.EntityManager;

import com.cefalo.cci.model.EpubFile;
import com.cefalo.cci.model.Issue;
import com.cefalo.cci.model.Publication;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import org.hibernate.Session;

import java.util.Date;

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
    public long getIssueCountByPublicationAndDeviceId(String publicationId, String deviceType) {
        return (Long) entityManager.createQuery("select count(i) from Issue i where i.publication.id like :pName and i.platform.id like :deviceType")
                .setParameter("pName", publicationId).setParameter("deviceType", deviceType).getSingleResult();
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public List<Issue> getIssueListByPublicationId(String publicationId) {
        return entityManager
                .createQuery("select i from Issue i where i.publication.id like :pName order by i.updated desc")
                .setParameter("pName", publicationId).getResultList();
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public List<Issue> getIssueListByPublicationId(String publicationId, long start, long maxResult) {
        return entityManager
                .createQuery("select i from Issue i where i.publication.id like :pName order by i.updated  desc")
                .setParameter("pName", publicationId).setFirstResult((int) start).setMaxResults((int) maxResult)
                .getResultList();
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public List<Issue> getIssueListByPublicationAndDeviceId(String publicationId, long start, long maxResult, String deviceType) {
        return entityManager
                .createQuery("select i from Issue i where i.publication.id like :pName and i.platform.id like :deviceType order by i.updated  desc")
                .setParameter("pName", publicationId).setParameter("deviceType", deviceType).setFirstResult((int) start).setMaxResults((int) maxResult)
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

    @Transactional
    public Publication getPublication(String id) {
        return entityManager.find(Publication.class, id);
    }


    @Override
    @Transactional
    public void uploadEpubFile(String publicationId, String fileName, InputStream inputStream) throws IOException {
        Session session = (Session) entityManager.getDelegate();
        Blob fileContent;
        try {
/*
            need to fix file size limit based on disscussion
            there is a method to get fileSize--- inputStream.avaiable();
*/
            fileContent = session.getLobHelper().createBlob(inputStream, 1024L);
        } catch (Exception ex) {
            throw new IOException();
        }
        EpubFile epubFile = new EpubFile();
        epubFile.setFile(fileContent);

        Serializable id = session.save(epubFile);

        Issue issue = new Issue();
        issue.setId(fileName.substring(0, fileName.indexOf(".epub")));
        issue.setName(fileName);
        issue.setPublication(new Publication(publicationId));
        issue.setEpubFile(new EpubFile((Long) id));
        entityManager.persist(issue);
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public List<Issue> getOldIssueList(Date date) {
        return (List<Issue>)entityManager.createQuery("select i from Issue i where i.updated < :date").setParameter("date", date).getResultList();
    }

}
