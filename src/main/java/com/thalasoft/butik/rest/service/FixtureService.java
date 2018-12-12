package com.thalasoft.butik.rest.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.thalasoft.butik.data.jpa.domain.EmailAddress;
import com.thalasoft.butik.data.jpa.domain.Order;
import com.thalasoft.butik.data.jpa.domain.Product;
import com.thalasoft.butik.data.service.OrderService;
import com.thalasoft.butik.data.service.ProductService;
import com.thalasoft.butik.rest.resource.OrderResource;
import com.thalasoft.butik.rest.resource.ProductResource;
import com.thalasoft.toolbox.utils.CommonTools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FixtureService {

  @Autowired
  private OrderService orderService;

  @Autowired
  private ProductService productService;

  @Autowired
  private ResourceService resourceService;

  private Order order0;
  private List<Product> manyProducts;

  public void addData() {
    manyProducts = new ArrayList<Product>();
    for (int i = 0; i < 40; i++) {
      String index = CommonTools.formatSortableStringNumber(i, 3);
      Product oneProduct = new Product();
      oneProduct.setName("item" + index);
      oneProduct.setPrice("11");
      oneProduct = productService.add(oneProduct);
      manyProducts.add(oneProduct);
    }

    order0 = new Order();
    order0.setOrderRefId(1);
    order0.setEmail(new EmailAddress("rob@gmail.com"));
    order0.setOrderedOn(LocalDateTime.now());

    order0.addProduct(manyProducts.get(0));
    order0.addProduct(manyProducts.get(1));
    order0.addProduct(manyProducts.get(2));
    orderService.add(order0);
  }

  public void removeData() {
    orderService.delete(order0.getId());
    for (Product oneProduct : manyProducts) {
      productService.delete(oneProduct.getId());
    }
  }

  public void createProductResources() {
    List<ProductResource> manyProductResources = new ArrayList<ProductResource>();
    for (int i = 0; i < 30; i++) {
      String index = CommonTools.formatSortableStringNumber(i + 1, 2);
      ProductResource oneProductResource = new ProductResource();
      oneProductResource.setName("product" + index);
      oneProductResource.setPrice("10");
      Product createdProduct = productService.add(resourceService.toProduct(oneProductResource));
      oneProductResource.setResourceId(createdProduct.getId());
      manyProductResources.add(oneProductResource);
    }
  }

  public void createOrderResources() {
    OrderResource orderResource0 = new OrderResource();
    orderResource0.setOrderRefId(1);
    orderResource0.setEmail("peter@gmail.com");

    OrderResource orderResource1 = new OrderResource();
    orderResource1.setOrderRefId(2);
    orderResource1.setEmail("paul@gmail.com");

    OrderResource orderResource2 = new OrderResource();
    orderResource2.setOrderRefId(3);
    orderResource2.setEmail("marie@gmail.com");
  }

}
