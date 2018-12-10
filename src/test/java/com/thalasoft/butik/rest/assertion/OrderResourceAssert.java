package com.thalasoft.butik.rest.assertion;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import com.thalasoft.butik.data.jpa.domain.Order;
import com.thalasoft.butik.rest.resource.OrderResource;
import com.thalasoft.butik.rest.resource.ProductResource;

import org.assertj.core.api.AbstractAssert;

public class OrderResourceAssert extends AbstractAssert<OrderResourceAssert, OrderResource> {

	private OrderResourceAssert(OrderResource actual) {
		super(actual, OrderResourceAssert.class);
	}
	
	public static OrderResourceAssert assertThatOrderResource(OrderResource actual) {
		return new OrderResourceAssert(actual);
	}

	public OrderResourceAssert hasOrderRefId(Integer orderRefId) {
		isNotNull();
		assertThat(actual.getOrderRefId()).overridingErrorMessage("Expected the order reference id to be <%s> but was <%s>.", orderRefId, actual.getOrderRefId()).isEqualTo(orderRefId);
		return this;
	}
	
	public OrderResourceAssert hasEmail(String email) {
		isNotNull();
		assertThat(actual.getEmail()).overridingErrorMessage("Expected the email to be <%s> but was <%s>.", email, actual.getEmail()).isEqualTo(email);
		return this;
	}

	public OrderResourceAssert hasOrderedOn(LocalDateTime orderedOn) {
		isNotNull();
		assertThat(actual.getOrderedOn()).overridingErrorMessage("Expected the ordered on date to be <%s> but was <%s>.", orderedOn, actual.getOrderedOn()).isEqualTo(orderedOn);
		return this;
	}

  public OrderResourceAssert hasProduct(ProductResource productResource) {
		isNotNull();
		boolean hasProduct = false;
		for (ProductResource currentProductResource : actual.getProductResources()) {
			if (currentProductResource.getName().equals(productResource.getName())) {
				hasProduct = true;
			}
		}
		assertThat(hasProduct).overridingErrorMessage("Expected to have the product <%s> but didn't.", productResource.getName()).isTrue();
		return this;
  }
	
	public OrderResourceAssert isSameAs(OrderResource orderOrder) {
		isNotNull();
		assertThat(actual.hashCode()).overridingErrorMessage("Expected the hash code to be <%s> but was <%s>.", orderOrder.hashCode(), actual.hashCode()).isEqualTo(orderOrder.hashCode());
		return this;
	}
	
	public OrderResourceAssert exists() {
		isNotNull();
		assertThat(actual).overridingErrorMessage("Expected the order to exist but it didn't.").isNotNull();
		return this;
	}
	
	public OrderResourceAssert doesNotExist() {
		isNull();
		assertThat(actual).overridingErrorMessage("Expected the order not to exist but it did.").isNull();
		return this;
	}
  
}
