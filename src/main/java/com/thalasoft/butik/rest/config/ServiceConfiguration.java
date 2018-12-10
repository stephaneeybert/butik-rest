package com.thalasoft.butik.rest.config;

import com.thalasoft.butik.rest.service.ResourceServiceImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ServiceConfiguration {

  @Bean
  public ResourceServiceImpl resourceService() {
    return new ResourceServiceImpl();
  }

}
