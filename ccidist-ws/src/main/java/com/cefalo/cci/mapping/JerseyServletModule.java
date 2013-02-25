package com.cefalo.cci.mapping;

import com.cefalo.cci.dao.ResourceDao;
import com.cefalo.cci.dao.ResourceDaoImpl;
import com.cefalo.cci.service.CciService;
import com.cefalo.cci.service.CciServiceImpl;
import com.cefalo.cci.service.HelloService;
import com.cefalo.cci.service.HelloServiceImpl;
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

        bind(HelloService.class).to(HelloServiceImpl.class);
        bind(ResourceDao.class).to(ResourceDaoImpl.class);
        bind(CciService.class).to(CciServiceImpl.class);

        bindConstant().annotatedWith(Names.named("epubFileDirPath")).to(Utils.FILE_BASE_PATH);

        Map<String, String> params = new HashMap<String, String>();
        params.put(PackagesResourceConfig.PROPERTY_PACKAGES, "com.cefalo.cci.restResource");
        params.put(ServletContainer.JSP_TEMPLATES_BASE_PATH, "/WEB-INF/jsp");

        filter("/resource/*").through(GuiceContainer.class, params);
        filter("/*").through(GuiceContainer.class, params);
        filter("/resource/*").through(PersistFilter.class, params);
    }
}
