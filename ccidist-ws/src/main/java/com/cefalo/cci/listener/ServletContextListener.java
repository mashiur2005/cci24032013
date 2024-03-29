package com.cefalo.cci.listener;

import com.cefalo.cci.mapping.JerseyServletModule;
import com.cefalo.cci.mapping.PurgeFileQuartzModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class ServletContextListener extends GuiceServletContextListener{
  @Override
  protected Injector getInjector() {
    return Guice.createInjector(new JerseyServletModule(), new PurgeFileQuartzModule());
  }
}
