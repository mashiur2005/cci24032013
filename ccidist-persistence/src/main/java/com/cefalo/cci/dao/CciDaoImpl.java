package com.cefalo.cci.dao;

import com.cefalo.cci.model.Organization;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

public class CciDaoImpl implements CciDao{
    @Inject
    private EntityManager entityManager;

    @Override
    @Transactional
    public List<Organization> getAllOrganization() {
        return (List<Organization>) entityManager.createQuery("from Organization").getResultList();
    }
}
