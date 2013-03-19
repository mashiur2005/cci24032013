package com.cefalo.cci.dao;

import java.util.List;

import javax.persistence.EntityManager;

import com.cefalo.cci.model.Organization;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class OrganizationDaoImpl implements OrganizationDao {

    @Inject
     private Provider<EntityManager> entityManagerProvider;

    @SuppressWarnings("unchecked")
    @Override
    public List<Organization> getAllOrganizations() {
        EntityManager entityManager = entityManagerProvider.get();
        return entityManager.createQuery("select o from Organization o order by o.updated desc")
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.cacheRegion", "query.organizationList")
                .getResultList();
    }

    @Override
    public Organization getOrganization(String id) {
        EntityManager entityManager = entityManagerProvider.get();
        return entityManager.find(Organization.class, id);
    }
}
