package com.cefalo.cci.mapping;

import java.util.HashMap;
import java.util.Map;

import com.cefalo.cci.dao.ResourceDao;
import com.cefalo.cci.dao.ResourceDaoImpl;
import com.cefalo.cci.service.HelloService;
import com.cefalo.cci.service.HelloServiceImpl;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.spi.container.servlet.ServletContainer;

public class JerseyServletModule extends com.sun.jersey.guice.JerseyServletModule {
    @Override
    protected void configureServlets() {
        install(new JpaPersistModule("cciJpaUnit"));

        bind(HelloService.class).to(HelloServiceImpl.class);
        bind(ResourceDao.class).to(ResourceDaoImpl.class);

        Map<String, String> params = new HashMap<String, String>();
        params.put(PackagesResourceConfig.PROPERTY_PACKAGES, "com.cefalo.cci.restResource");
        params.put(ServletContainer.JSP_TEMPLATES_BASE_PATH, "/WEB-INF/jsp");

        filter("/resource/*").through(GuiceContainer.class, params);
        filter("/resource/*").through(PersistFilter.class, params);
    }
}
