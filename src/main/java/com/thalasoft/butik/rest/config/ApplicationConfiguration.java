package com.thalasoft.butik.rest.config;

import com.thalasoft.toolbox.spring.PackageBeanNameGenerator;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@Component
@ComponentScan(nameGenerator = PackageBeanNameGenerator.class, basePackages = { "com.thalasoft.butik.rest.service", "com.thalasoft.butik.data.config" })
public class ApplicationConfiguration {
}
