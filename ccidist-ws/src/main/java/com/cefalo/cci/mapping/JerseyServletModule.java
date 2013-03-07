package com.cefalo.cci.mapping;

import com.cefalo.cci.dao.*;
import com.cefalo.cci.dao.IssueDao;
import com.cefalo.cci.dao.IssueDaoImpl;
import com.cefalo.cci.service.*;
import com.cefalo.cci.storage.DatabaseStorage;
import com.cefalo.cci.storage.CacheStorage;
import com.cefalo.cci.storage.Storage;
import com.cefalo.cci.utils.Utils;
import com.google.inject.name.Names;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.spi.container.servlet.ServletContainer;

import java.util.HashMap;
import java.util.Map;

public class JerseyServletModule extends com.sun.jersey.guice.JerseyServletModule {
    @Override
    protected void configureServlets() {
        install(new JpaPersistModule("cciJpaUnit"));

        bind(CciService.class).to(CciServiceImpl.class);
        bind(Storage.class).annotatedWith(Names.named("databaseStorage")).to(DatabaseStorage.class);
        bind(Storage.class).to(CacheStorage.class);
        bind(OrganizationDao.class).to(OrganizationDaoImpl.class);
        bind(OrganizationService.class).to(OrganizationServiceImpl.class);
        bind(PublicationDao.class).to(PublicationDaoImpl.class);
        bind(PublicationService.class).to(PublicationServiceImpl.class);
        bind(IssueDao.class).to(IssueDaoImpl.class);
        bind(IssueService.class).to(IssueServiceImpl.class);

        bindConstant().annotatedWith(Names.named("epubFileDirPath")).to(Utils.FILE_BASE_PATH);
        bindConstant().annotatedWith(Names.named("cacheDirFullPath")).to(Utils.CACHE_DIR_FULLPATH);
        bindConstant().annotatedWith(Names.named("fileSystemSeperator")).to(Utils.FILE_SEPARATOR);

        Map<String, String> params = new HashMap<String, String>();
        params.put(PackagesResourceConfig.PROPERTY_PACKAGES, "com.cefalo.cci.restResource");
        params.put(ServletContainer.JSP_TEMPLATES_BASE_PATH, "/WEB-INF/jsp");

        filter("/*").through(GuiceContainer.class, params);
        filter("/*").through(PersistFilter.class, params);
    }
}
