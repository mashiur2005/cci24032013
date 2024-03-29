package com.cefalo.cci.listener;

import com.cefalo.cci.mapping.TestJerseyServletModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class TestServletContextListener extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new TestJerseyServletModule());
    }
}
