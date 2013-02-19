package com.cefalo.cci.service;

import com.google.inject.AbstractModule;

public class ServicesTestModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(HelloService.class).to(HelloServiceImpl.class);
    }

}
