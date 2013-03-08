package com.cefalo.cci.dao;

import java.util.List;

import javax.persistence.EntityManager;

import com.cefalo.cci.model.Organization;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class OrganizationDaoImpl implements OrganizationDao {
    @Inject
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public List<Organization> getAllOrganizations() {
        return entityManager.createQuery("select o from Organization o order by o.updated desc").getResultList();
    }

    @Override
    @Transactional
    public Organization getOrganization(String id) {
        return entityManager.find(Organization.class, id);
    }
}
