package com.thalasoft.butik.rest.ut;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import com.thalasoft.butik.rest.resource.ProductResource;

import org.junit.Before;
import org.junit.Test;

public class ProductTest {

  private Validator validator;

  private ProductResource productResource0;

  private Set<ConstraintViolation<ProductResource>> constraintViolations;

  @Before
  public void beforeAnyTest() throws Exception {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();

    productResource0 = new ProductResource();
    productResource0.setName("Water cooler");
    productResource0.setPrice("10");
  }

  @Test
  public void testNoValidationViolation() {
    constraintViolations = validator.validate(productResource0);
    assertThat(constraintViolations.size(), is(0));
  }

  @Test
  public void testEmptyNameViolation() {
    productResource0.setName(null);
    constraintViolations = validator.validate(productResource0);
    assertThat(constraintViolations.size(), is(1));
  }

  @Test
  public void testEmptyPriceViolation() {
    productResource0.setPrice(null);
    constraintViolations = validator.validate(productResource0);
    assertThat(constraintViolations.size(), is(1));
  }

}
