package com.cefalo.cci.dao;

import com.cefalo.cci.model.Organization;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

public class OrganizationDaoImpl implements OrganizationDao {
    @Inject
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public List<Organization> getAllOrganizations() {
        return (List<Organization>) entityManager.createQuery("FROM Organization").getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public Organization getOrganization(String id) {
        return (Organization) entityManager.find(Organization.class, id);
    }
}
