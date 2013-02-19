package com.cefalo.cci.service;

import com.google.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GuiceJUnitRunner.class)
@GuiceJUnitRunner.GuiceModules({ServicesTestModule.class })
public class HelloServiceTest {

  @Inject
  private HelloService helloService;

  @Test
  public void getHelloMessageTest() {
    Assert.assertEquals("Hello From Service", helloService.getHelloMessage());
  }
}
