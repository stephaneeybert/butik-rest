package com.thalasoft.butik.rest.condition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class BootstrapSQLCondition implements Condition {

  private static Logger logger = LoggerFactory.getLogger(BootstrapSQLCondition.class);

  private static final String BOOTSTRAP_SQL = "bootstrapsql";

  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    logger.debug("==============>> bootstrapsql : " + context.getEnvironment().getProperty(BOOTSTRAP_SQL));
    return context.getEnvironment().getProperty(BOOTSTRAP_SQL) != null
        && context.getEnvironment().getProperty(BOOTSTRAP_SQL).equals("true");
  }

}