package com.thalasoft.butik.rest.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import com.thalasoft.butik.data.jpa.domain.Order;
import com.thalasoft.butik.rest.controller.OrderController;
import com.thalasoft.butik.rest.resource.OrderResource;
import com.thalasoft.butik.rest.service.ResourceService;
import com.thalasoft.butik.rest.utils.DomainConstants;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class OrderResourceAssembler extends ResourceAssemblerSupport<Order, OrderResource> {

  @Autowired
  private ResourceService resourceService;

  public OrderResourceAssembler() {
    super(OrderController.class, OrderResource.class);
  }

  @Override
  public OrderResource toResource(Order order) {
    OrderResource productResource = createResourceWithId(order.getId(), order);
    BeanUtils.copyProperties(resourceService.fromOrder(order), productResource);
    productResource.add(linkTo(OrderController.class).slash(order.getId()).slash(DomainConstants.PRODUCTS)
        .withRel(DomainConstants.PRODUCTS));
    return productResource;
  }

  @Override
  public List<OrderResource> toResources(Iterable<? extends Order> orders) {
    List<OrderResource> orderResources = new ArrayList<OrderResource>();
    for (Order order : orders) {
      OrderResource orderResource = createResourceWithId(order.getId(), order);
      BeanUtils.copyProperties(resourceService.fromOrder(order), orderResource);
      orderResource.add(linkTo(OrderController.class).slash(order.getId()).slash(DomainConstants.PRODUCTS)
          .withRel(DomainConstants.PRODUCTS));
      orderResources.add(orderResource);
    }
    return orderResources;
  }

}
