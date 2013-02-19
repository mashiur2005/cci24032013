package com.cefalo.cci.service;

public class HelloServiceImpl implements HelloService {
  @Override
  public String getHelloMessage() {
    return "Hello From Service";
  }
}
