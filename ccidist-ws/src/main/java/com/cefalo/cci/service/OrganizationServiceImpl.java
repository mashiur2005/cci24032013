package com.cefalo.cci.service;

import com.cefalo.cci.dao.OrganizationDao;
import com.cefalo.cci.model.Organization;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

public class OrganizationServiceImpl implements OrganizationService {
    @Inject
    private OrganizationDao organizationDao;

    @Override
    public List<Organization> getAllOrganizations() {
        List<Organization> organizationList = organizationDao.getAllOrganizations();
        if (organizationList == null) {
            return new ArrayList<>();
        }
        return organizationList;
    }

    @Override
    public Organization getOrganization(String id) {
        return organizationDao.getOrganization(id);
    }
}
