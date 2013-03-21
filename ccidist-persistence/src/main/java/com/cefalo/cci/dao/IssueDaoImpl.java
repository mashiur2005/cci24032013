package com.cefalo.cci.dao;

import com.cefalo.cci.model.*;
import com.cefalo.cci.utils.Utils;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

import javax.persistence.EntityManager;
import java.io.*;
import java.io.Serializable;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class IssueDaoImpl implements IssueDao {

    @Inject
    private EntityManager entityManager;


    @Override
    public long getIssueCountByPublicationId(String publicationId) {
        return (Long) entityManager.createQuery("select count(i) from Issue i where i.publication.id like :pName")
                .setParameter("pName", publicationId).getSingleResult();
    }

    @Override
    public long getIssueCountByPublicationAndDeviceId(String publicationId, String deviceType, Date fromDate) {
        //form date need to be set
        return (Long) entityManager.createQuery("select count(i) from Issue i where i.publication.id like :pName and i.platform.id like :deviceType and i.created >= :fromDate")
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.cacheRegion", "query.issueList")
                .setParameter("pName", publicationId)
                .setParameter("deviceType", deviceType)
                .setParameter("fromDate", fromDate)
                .getSingleResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Issue> getIssueListByPublicationId(String publicationId) {
        return entityManager
                .createQuery("select i from Issue i where i.publication.id like :pName order by i.updated desc")
                .setParameter("pName", publicationId).getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Issue> getIssueListByPublicationId(String publicationId, long start, long maxResult) {
        return entityManager
                .createQuery("select i from Issue i where i.publication.id like :pName order by i.updated  desc")
                .setParameter("pName", publicationId).setFirstResult((int) start).setMaxResults((int) maxResult)
                .getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Issue> getIssueListByPublicationAndDeviceId(String publicationId, long start, long maxResult, String deviceType, Date fromDate, String sortOrder) {

        //Here creaded date used to compare with fromDate
        return entityManager
                .createQuery("select i from Issue i where i.publication.id like :pName and i.platform.id like :deviceType  and i.created >= :fromDate  order by i.updated "  + sortOrder)
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.cacheRegion", "query.issueList")
                .setParameter("pName", publicationId)
                .setParameter("deviceType", deviceType)
                .setParameter("fromDate", fromDate)
                .setFirstResult((int) start)
                .setMaxResults((int) maxResult)
                .getResultList();
    }


    @Override
    @Transactional
    public EpubFile getEpubFile(long id) {
        return entityManager.find(EpubFile.class, id);
    }

    @Override
    public Issue getIssue(String id) {
        return entityManager.find(Issue.class, id);
    }

    public Publication getPublication(String id) {
        return entityManager.find(Publication.class, id);
    }


/*
    @Override
    public void uploadEpubFile(String publicationId, String fileName, Set<String> deviceSet, InputStream inputStream) throws IOException {
        byte[] fileContent;
        try {
            fileContent = ByteStreams.toByteArray(inputStream);
        } catch (IOException ex) {
            throw ex;
        }

        try {
            EpubFile epubFile = new EpubFile();
            epubFile.setFile(fileContent);
            entityManager.persist(epubFile);

            long epubId = epubFile.getId();
            for (String deviceId : deviceSet) {
                //generating issue primary key....based on fileName and count on existing fileName
                String issuePK = generateIssuePrimaryKey(fileName.substring(0, fileName.length() - 1 - 4)); //length of .epub = 4
                Issue issue = createIssue(publicationId, issuePK, fileName, deviceId, epubId);
                entityManager.persist(issue);
                entityManager.flush();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
*/

    @Override
    @SuppressWarnings("unchecked")
    public List<Issue> getOldIssueList(Date date) {
        return entityManager.createQuery("select i from Issue i where i.updated < :date").setParameter("date", date).getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Issue> getIssueByPublicationAndDeviceIdAndIssue(String publicationId, String deviceId, String issueName, String sortOrder) {
        return entityManager
                .createQuery("select i from Issue i where i.publication.id like :pName and i.platform.id like :deviceType and i.name like :issueName order by i.updated " + sortOrder)
                .setParameter("pName", publicationId).setParameter("deviceType", deviceId).setParameter("issueName", issueName)
                .getResultList();
    }

    @Override
    @Transactional
    public void updateEpub(long Id, InputStream updateInputStream) {
        byte[] fileContent;
        try {
            fileContent = ByteStreams.toByteArray(updateInputStream);

            EpubFile epubFile = getEpubFile(Id);
            epubFile.setFile(fileContent);
            entityManager.persist(epubFile);
            entityManager.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            entityManager.clear();
        }
    }

    @Override
    @Transactional
    public URI saveEpub(InputStream epubInputStream) throws IOException {
        byte[] fileContent;
        try {
            fileContent = ByteStreams.toByteArray(epubInputStream);
        } catch (IOException ex) {
            throw ex;
        }

        try {
            EpubFile epubFile = new EpubFile();
            epubFile.setFile(fileContent);
            entityManager.persist(epubFile);
            entityManager.flush();
            return URI.create(String.valueOf(epubFile.getId()));
        } catch (Exception e) {
            throw new RuntimeException();
        } finally {
            entityManager.clear();
        }
    }

    @Override
    public void saveIssue(String publicationId, String fileName, Date date, Set<String> deviceSet, long epubId) {
        for (String deviceId : deviceSet) {
            //generating issue primary key....based on fileName and count on existing fileName
            String issuePK = generateIssuePrimaryKey(fileName);
            Issue issue = createIssue(publicationId, issuePK, fileName, deviceId, epubId, date);
            entityManager.persist(issue);
            entityManager.flush();
        }
        entityManager.clear();
    }



    public Issue createIssue(String publicationId, String issuePK, String fileName, String deviceId,  Serializable epubId, Date date) {
        Issue issue = new Issue();
        issue.setId(issuePK);
        issue.setName(fileName);
        issue.setPlatform(new Platform(deviceId));
        issue.setPublication(new Publication(publicationId));
        issue.setEpubFile(new EpubFile((Long) epubId));
        issue.setCreated(date);
        issue.setUpdated(date);
        return issue;
    }

    public String generateIssuePrimaryKey(String fileName) {
        long countDuplicateIssue = (Long) entityManager.createQuery("SELECT COUNT(i) FROM Issue i Where i.id LIKE :fileName").setParameter("fileName", fileName + "_%")
                .getSingleResult();
        return fileName + "_" + (countDuplicateIssue + 1);
    }
}
