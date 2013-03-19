package com.cefalo.cci.dao;

import com.cefalo.cci.model.Publication;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import javax.persistence.EntityManager;

@Singleton
public class PublicationDaoImpl implements PublicationDao {
    @Inject
    private Provider<EntityManager> entityManagerProvider;

    @Override
    public Publication getPublication(String id) {
        EntityManager entityManager = entityManagerProvider.get();
        return (Publication) entityManager.find(Publication.class, id);
    }
}
