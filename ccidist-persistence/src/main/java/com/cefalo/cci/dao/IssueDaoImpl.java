package com.cefalo.cci.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.util.List;

import javax.persistence.EntityManager;

import com.cefalo.cci.model.EpubFile;
import com.cefalo.cci.model.Issue;
import com.cefalo.cci.model.Platform;
import com.cefalo.cci.model.Publication;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Set;

public class IssueDaoImpl implements IssueDao {

    private final Logger logger = LoggerFactory.getLogger(getClass());

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
    public long getIssueCountByPublicationAndDeviceId(String publicationId, String deviceType, Date fromDate) {
        //form date need to be set
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
    public List<Issue> getIssueListByPublicationAndDeviceId(String publicationId, long start, long maxResult, String deviceType, Date fromDate, String order) {

        //form date need to be compared
        return entityManager
                .createQuery("select i from Issue i where i.publication.id like :pName and i.platform.id like :deviceType order by i.updated " + order)
                .setParameter("pName", publicationId).setParameter("deviceType", deviceType)
                .setFirstResult((int) start).setMaxResults((int) maxResult)
                .getResultList();
    }


    @Override
    @Transactional
    public EpubFile getEpubFile(long id) {

        logger.info("epub...id.." + id);
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
    public void uploadEpubFile(String publicationId, String fileName, Set<String> deviceSet, InputStream inputStream) throws IOException {
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

        Serializable epubId = session.save(epubFile);

        for (String deviceId : deviceSet) {
            //generating issue primary key....based on fileName and count on existing fileName
            String issuePK = generateIssuePrimaryKey(fileName.substring(0, fileName.indexOf(".epub")));
            Issue issue = createIssue(publicationId, issuePK, fileName, deviceId, epubId);
            entityManager.persist(issue);
        }
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public List<Issue> getOldIssueList(Date date) {
        return (List<Issue>)entityManager.createQuery("select i from Issue i where i.updated < :date").setParameter("date", date).getResultList();
    }

    public Issue createIssue(String publicationId, String issuePK, String fileName, String deviceId,  Serializable epubId) {
        Issue issue = new Issue();
        issue.setId(issuePK);
        issue.setName(fileName);
        issue.setPlatform(new Platform(deviceId));
        issue.setPublication(new Publication(publicationId));
        issue.setEpubFile(new EpubFile((Long) epubId));
        return issue;
    }

    public String generateIssuePrimaryKey(String fileName) {
        long countDuplicateIssue = (Long) entityManager.createQuery("SELECT COUNT(i) FROM Issue i Where i.id LIKE :fileName").setParameter("fileName", fileName + "_%")
                .getSingleResult();
        return fileName + "_" + (countDuplicateIssue + 1);
    }
}
