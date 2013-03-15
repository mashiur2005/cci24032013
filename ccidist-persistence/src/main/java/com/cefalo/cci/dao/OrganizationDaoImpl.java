package com.cefalo.cci.dao;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import com.cefalo.cci.model.Organization;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

@Singleton
public class OrganizationDaoImpl implements OrganizationDao {
    @Inject
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @Override
    public List<Organization> getAllOrganizations() {
        return entityManager.createQuery("select o from Organization o order by o.updated desc")
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.cacheRegion", "query.organizationList")
                .getResultList();
    }

    @Override
    public Organization getOrganization(String id) {
        return entityManager.find(Organization.class, id);
    }
}
