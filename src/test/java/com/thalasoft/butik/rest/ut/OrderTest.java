package com.thalasoft.butik.rest.ut;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.thalasoft.butik.rest.resource.OrderResource;
import com.thalasoft.butik.rest.resource.ProductResource;

import org.junit.Before;
import org.junit.Test;

public class OrderTest {

  private Validator validator;

  private OrderResource orderResource0;
  private Set<ConstraintViolation<OrderResource>> constraintViolations;

  @Before
  public void beforeAnyTest() throws Exception {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();

    orderResource0 = new OrderResource();
    orderResource0.setOrderRefId(1);
    orderResource0.setEmail("peter@gmail.com");
    orderResource0.setOrderedOn(LocalDateTime.now());

    Set<ProductResource> manyProductResources = new HashSet<ProductResource>();
    for (int i = 0; i < 4; i++) {
      ProductResource oneProductResource = new ProductResource();
      oneProductResource.setName("product" + i);
      oneProductResource.setPrice("11");
      manyProductResources.add(oneProductResource);
    }
    orderResource0.setProductResources(manyProductResources);
  }

  @Test
  public void testNoValidationViolation() {
    constraintViolations = validator.validate(orderResource0);
    assertThat(constraintViolations.size(), is(0));
  }

  @Test
  public void testEmptyOrderRefIdViolation() {
    orderResource0.setOrderRefId(null);
    constraintViolations = validator.validate(orderResource0);
    assertThat(constraintViolations.size(), is(1));
  }

  @Test
  public void testEmptyEmailViolation() {
    orderResource0.setEmail(null);
    constraintViolations = validator.validate(orderResource0);
    assertThat(constraintViolations.size(), is(1));
  }

}
