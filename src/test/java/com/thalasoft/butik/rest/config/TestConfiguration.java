package com.thalasoft.butik.rest.config;

import com.thalasoft.toolbox.spring.PackageBeanNameGenerator;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(nameGenerator = PackageBeanNameGenerator.class, basePackages = { "com.thalasoft.butik.rest.config",
    "com.thalasoft.butik.rest.service" })
public class TestConfiguration {
}
