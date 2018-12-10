package com.thalasoft.butik.rest.assertion;

import static org.assertj.core.api.Assertions.assertThat;

import com.thalasoft.butik.rest.resource.ProductResource;

import org.assertj.core.api.AbstractAssert;

public class ProductResourceAssert extends AbstractAssert<ProductResourceAssert, ProductResource> {

  private ProductResourceAssert(ProductResource actual) {
    super(actual, ProductResourceAssert.class);
  }

  public static ProductResourceAssert assertThatProductResource(ProductResource actual) {
    return new ProductResourceAssert(actual);
  }

  public ProductResourceAssert hasId(Long id) {
    isNotNull();
    assertThat(actual.getResourceId())
        .overridingErrorMessage("Expected the id to be <%s> but was <%s>.", id, actual.getResourceId()).isEqualTo(id);
    return this;
  }

  public ProductResourceAssert hasName(String name) {
    isNotNull();
    assertThat(actual.getName().toString())
        .overridingErrorMessage("Expected the name to be <%s> but was <%s>.", name, actual.getName()).isEqualTo(name);
    return this;
  }

  public ProductResourceAssert hasPrice(String price) {
    isNotNull();
    assertThat(actual.getPrice())
        .overridingErrorMessage("Expected the price to be <%s> but was <%s>.", price, actual.getPrice())
        .isEqualTo(price);
    return this;
  }

  public ProductResourceAssert isSameAs(ProductResource productResource) {
    isNotNull();
    assertThat(actual.hashCode())
        .overridingErrorMessage("Expected the hash code to be <%s> but was <%s>.", productResource.hashCode(), actual.hashCode())
        .isEqualTo(productResource.hashCode());
    return this;
  }

  public ProductResourceAssert exists() {
    isNotNull();
    assertThat(actual).overridingErrorMessage("Expected the product to exist but it didn't.").isNotNull();
    return this;
  }

  public ProductResourceAssert doesNotExist() {
    isNull();
    assertThat(actual).overridingErrorMessage("Expected the product not to exist but it did.").isNull();
    return this;
  }

}
