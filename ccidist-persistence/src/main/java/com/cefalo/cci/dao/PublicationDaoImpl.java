package com.cefalo.cci.dao;

import com.cefalo.cci.model.Publication;

import javax.inject.Inject;
import javax.persistence.EntityManager;

public class PublicationDaoImpl implements PublicationDao {
    @Inject
    private EntityManager entityManager;

    @Override
    public Publication getPublication(String id) {
        return (Publication) entityManager.find(Publication.class, id);
    }
}
