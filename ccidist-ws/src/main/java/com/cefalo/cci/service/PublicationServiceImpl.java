package com.cefalo.cci.service;

import com.cefalo.cci.dao.PublicationDao;
import com.cefalo.cci.model.Publication;
import com.google.inject.Singleton;

import javax.inject.Inject;

@Singleton
public class PublicationServiceImpl implements PublicationService {

    @Inject
    private PublicationDao publicationDao;

    @Override
    public Publication getPublication(String id) {
        return publicationDao.getPublication(id);
    }
}
