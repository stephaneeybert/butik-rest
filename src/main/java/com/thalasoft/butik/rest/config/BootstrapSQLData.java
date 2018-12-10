package com.thalasoft.butik.rest.config;

import com.thalasoft.butik.rest.service.FixtureService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "bootstrapsql", havingValue = "true")
public class BootstrapSQLData implements ApplicationListener<ContextRefreshedEvent> {

  @Autowired
  private FixtureService fixtureService;

  @Override
  public void onApplicationEvent(final ContextRefreshedEvent event) {
    fixtureService.addData();
    fixtureService.createProductResources();
    fixtureService.createOrderResources();
  }

}
