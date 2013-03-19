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
}
