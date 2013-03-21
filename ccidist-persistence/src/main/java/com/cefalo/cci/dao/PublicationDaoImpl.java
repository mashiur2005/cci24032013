package com.cefalo.cci.dao;

import com.cefalo.cci.model.Publication;
import com.google.inject.Singleton;

import javax.inject.Inject;
import javax.persistence.EntityManager;

@Singleton
public class PublicationDaoImpl implements PublicationDao {
    @Inject
    private EntityManager entityManager;

    @Override
    public Publication getPublication(String id) {
        return (Publication) entityManager.find(Publication.class, id);
    }

    @Override
    public boolean isDuplicatePublicationExists(String publicationId,String organizationId) {
        return (Long) entityManager.createQuery("select count(p) from Publication p, Organization o where p.organization.id = o.id and p.organization.id like :oName and p.id like :pName")
                .setParameter("oName", organizationId)
                .setParameter("pName", publicationId)
                .getSingleResult() > 0;

    }
}
