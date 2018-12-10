package com.thalasoft.butik.rest.assembler;

import java.util.ArrayList;
import java.util.List;

import com.thalasoft.butik.data.jpa.domain.Product;
import com.thalasoft.butik.rest.controller.ProductController;
import com.thalasoft.butik.rest.resource.ProductResource;
import com.thalasoft.butik.rest.service.ResourceService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class ProductResourceAssembler extends ResourceAssemblerSupport<Product, ProductResource> {

  @Autowired
  private ResourceService resourceService;

  public ProductResourceAssembler() {
    super(ProductController.class, ProductResource.class);
  }

  @Override
  public ProductResource toResource(Product product) {
    ProductResource productResource = createResourceWithId(product.getId(), product);
    BeanUtils.copyProperties(resourceService.fromProduct(product), productResource);
    return productResource;
  }

  @Override
  public List<ProductResource> toResources(Iterable<? extends Product> products) {
    List<ProductResource> productResources = new ArrayList<ProductResource>();
    for (Product product : products) {
      ProductResource productResource = createResourceWithId(product.getId(), product);
      BeanUtils.copyProperties(resourceService.fromProduct(product), productResource);
      productResources.add(productResource);
    }
    return productResources;
  }

}
