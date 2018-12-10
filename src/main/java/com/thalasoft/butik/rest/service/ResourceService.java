package com.thalasoft.butik.rest.service;

import com.thalasoft.butik.data.jpa.domain.Order;
import com.thalasoft.butik.data.jpa.domain.Product;
import com.thalasoft.butik.rest.resource.OrderResource;
import com.thalasoft.butik.rest.resource.ProductResource;

import org.springframework.data.domain.Pageable;
import org.springframework.web.util.UriComponentsBuilder;

public interface ResourceService {

  public Product toProduct(ProductResource productResource);

  public ProductResource fromProduct(Product product);

  public Order toOrder(OrderResource orderResource);

  public OrderResource fromOrder(Order order);

  public void addPageableToUri(UriComponentsBuilder uriComponentsBuilder, Pageable pageable);

}
