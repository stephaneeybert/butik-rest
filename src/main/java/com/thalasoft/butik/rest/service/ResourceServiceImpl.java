package com.thalasoft.butik.rest.service;

import java.util.HashSet;
import java.util.Set;

import com.thalasoft.butik.data.exception.EntityNotFoundException;
import com.thalasoft.butik.data.jpa.domain.EmailAddress;
import com.thalasoft.butik.data.jpa.domain.Order;
import com.thalasoft.butik.data.jpa.domain.Product;
import com.thalasoft.butik.data.jpa.domain.OrderProduct;
import com.thalasoft.butik.data.service.OrderService;
import com.thalasoft.butik.data.service.ProductService;
import com.thalasoft.butik.rest.resource.ProductResource;
import com.thalasoft.butik.rest.resource.OrderResource;
import com.thalasoft.butik.rest.utils.RESTConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ResourceServiceImpl implements ResourceService {

  @Autowired
  private ProductService productService;

  @Autowired
  private OrderService orderService;

  @Override
  public Product toProduct(ProductResource productResource) {
    Product product = null;
    if (productResource.getResourceId() == null) {
      product = new Product();
    } else {
      try {
        product = productService.findById(productResource.getResourceId());
      } catch (EntityNotFoundException e) {
        product = new Product();
      }
    }
    product.setName(productResource.getName());
    product.setPrice(productResource.getPrice());
    return product;
  }

  @Override
  public ProductResource fromProduct(Product product) {
    ProductResource productResource = new ProductResource();
    productResource.setResourceId(product.getId());
    productResource.setName(product.getName());
    productResource.setPrice(product.getPrice());
    return productResource;
  }

  @Override
  public Order toOrder(OrderResource orderResource) {
    Order order = null;
    if (orderResource.getResourceId() == null) {
      order = new Order();
    } else {
      try {
        order = orderService.findById(orderResource.getResourceId());
      } catch (EntityNotFoundException e) {
        order = new Order();
      }
    }
    order.setOrderRefId(orderResource.getOrderRefId());
    order.setEmail(new EmailAddress(orderResource.getEmail()));
    order.setOrderedOn(orderResource.getOrderedOn());
    for (ProductResource productResource : orderResource.getProductResources()) {
      Product product = toProduct(productResource);
      order.addProduct(product);
    }
    return order;
  }

  @Override
  public OrderResource fromOrder(Order order) {
    OrderResource orderResource = new OrderResource();
    orderResource.setResourceId(order.getId());
    orderResource.setOrderRefId(order.getOrderRefId());
    orderResource.setEmail(order.getEmail().getEmailAddress());
    orderResource.setOrderedOn(order.getOrderedOn());
    Set<ProductResource> orderProductResources = new HashSet<ProductResource>();
    for (OrderProduct orderProduct : order.getOrderProducts()) {
      ProductResource productResource = fromProduct(orderProduct.getProduct());
      orderProductResources.add(productResource);
    }
    orderResource.setProductResources(orderProductResources);
    return orderResource;
  }

  @Override
  public void addPageableToUri(UriComponentsBuilder uriComponentsBuilder, Pageable pageable) {
    uriComponentsBuilder.queryParam("page", pageable.getPageNumber()).queryParam("size", pageable.getPageSize());
    if (pageable.getSort() != null) {
      for (Sort.Order order : pageable.getSort()) {
        uriComponentsBuilder.queryParam("sort", order.getProperty())
            .queryParam(order.getProperty() + RESTConstants.PAGEABLE_SORT_SUFFIX, order.getDirection().name());
      }
    }
  }

}
