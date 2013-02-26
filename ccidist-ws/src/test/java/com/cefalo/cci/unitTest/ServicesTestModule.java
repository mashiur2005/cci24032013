package com.cefalo.cci.unitTest;

import com.cefalo.cci.service.CciService;
import com.cefalo.cci.service.CciServiceImpl;
import com.cefalo.cci.utils.XpathUtils;
import com.google.inject.AbstractModule;

public class ServicesTestModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(XpathUtils.class);
      bind(CciService.class).to(CciServiceImpl.class);
    }

}
