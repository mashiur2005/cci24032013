package com.cefalo.cci.service;

import com.cefalo.cci.dao.OrganizationDao;
import com.cefalo.cci.model.Organization;
import com.google.inject.Inject;

import java.util.List;

public class OrganizationServiceImpl implements OrganizationService {
    @Inject
    private OrganizationDao organizationDao;

    @Override
    public List<Organization> getAllOrganizations() {
        return organizationDao.getAllOrganizations();
    }

    @Override
    public Organization getOrganization(String id) {
        return organizationDao.getOrganization(id);
    }
}
