package com.thalasoft.butik.rest.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;

import com.thalasoft.butik.data.exception.EntityNotFoundException;
import com.thalasoft.butik.data.jpa.domain.Order;
import com.thalasoft.butik.data.service.OrderService;
import com.thalasoft.butik.rest.resource.OrderResource;
import com.thalasoft.butik.rest.service.ResourceService;
import com.thalasoft.butik.rest.utils.CommonUtils;
import com.thalasoft.butik.rest.utils.DomainConstants;
import com.thalasoft.butik.rest.utils.RESTConstants;
import com.thalasoft.butik.rest.assembler.OrderResourceAssembler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping(RESTConstants.SLASH + DomainConstants.ORDERS)
public class OrderController {

  @Autowired
  private ResourceService resourceService;

  @Autowired
  private OrderService orderService;

  @Autowired
  private OrderResourceAssembler orderResourceAssembler;

  private static final String RESOURCE_PROPERTY_ID = "id";
  private static final Set<String> nonSortableColumns = new HashSet<String>(Arrays.asList(RESOURCE_PROPERTY_ID));

  @GetMapping(value = RESTConstants.SLASH + "{id}")
  @ResponseBody
  public ResponseEntity<OrderResource> findById(@PathVariable Long id, UriComponentsBuilder builder) {
    try {
      Order order = orderService.findById(id);
      OrderResource orderResource = orderResourceAssembler.toResource(order);
      URI location = builder.path(RESTConstants.SLASH + DomainConstants.ORDERS + RESTConstants.SLASH + "{id}")
          .buildAndExpand(order.getId()).toUri();
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.setLocation(location);
      return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(orderResource);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping
  @ResponseBody
  public ResponseEntity<OrderResource> add(@Valid @RequestBody OrderResource orderResource,
      UriComponentsBuilder builder) {
    Order order = orderService.add(resourceService.toOrder(orderResource));
    OrderResource createdOrderResource = null;
    URI location = builder.path(RESTConstants.SLASH + DomainConstants.ORDERS + RESTConstants.SLASH + "{id}")
        .buildAndExpand(order.getId()).toUri();
    createdOrderResource = orderResourceAssembler.toResource(order);
    return ResponseEntity.created(location).body(createdOrderResource);
  }

  @PutMapping(value = RESTConstants.SLASH + "{id}")
  @ResponseBody
  public ResponseEntity<OrderResource> update(@PathVariable Long id, @Valid @RequestBody OrderResource orderResource,
      UriComponentsBuilder builder) {
    Order order = orderService.update(id, resourceService.toOrder(orderResource));
    OrderResource updatedOrderResource = orderResourceAssembler.toResource(order);
    URI location = builder.path(RESTConstants.SLASH + DomainConstants.ORDERS + RESTConstants.SLASH + "{id}")
        .buildAndExpand(order.getId()).toUri();
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.setLocation(location);
    return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(updatedOrderResource);
  }

  @PatchMapping(value = RESTConstants.SLASH + "{id}")
  @ResponseBody
  public ResponseEntity<OrderResource> partialUpdate(@PathVariable Long id,
      @Valid @RequestBody OrderResource orderResource, UriComponentsBuilder builder) {
    Order order = orderService.partialUpdate(id, resourceService.toOrder(orderResource));
    OrderResource updatedOrderResource = orderResourceAssembler.toResource(order);
    URI location = builder.path(RESTConstants.SLASH + DomainConstants.ORDERS + RESTConstants.SLASH + "{id}")
        .buildAndExpand(order.getId()).toUri();
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.setLocation(location);
    return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(updatedOrderResource);
  }

  @DeleteMapping(value = RESTConstants.SLASH + "{id}")
  @ResponseBody
  public ResponseEntity<OrderResource> delete(@PathVariable Long id) {
    Order order = orderService.delete(id);
    OrderResource orderResource = orderResourceAssembler.toResource(order);
    return ResponseEntity.ok(orderResource);
  }

  @GetMapping
  @ResponseBody
  public ResponseEntity<PagedResources<OrderResource>> all(@PageableDefault(sort = { "orderedOn" }, direction = Sort.Direction.ASC) Pageable pageable, Sort sort, PagedResourcesAssembler<Order> pagedResourcesAssembler, UriComponentsBuilder builder) {
    sort = CommonUtils.stripColumnsFromSorting(sort, nonSortableColumns);
    orderService.addSortToPageable(pageable, sort);
    Page<Order> foundOrders = orderService.all(pageable);
    PagedResources<OrderResource> orderPagedResources = pagedResourcesAssembler.toResource(foundOrders,
        orderResourceAssembler);
    UriComponentsBuilder uriComponentsBuilder = builder.path(RESTConstants.SLASH + DomainConstants.ORDERS);
    resourceService.addPageableToUri(uriComponentsBuilder, pageable);
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.setLocation(uriComponentsBuilder.buildAndExpand().toUri());
    return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(orderPagedResources);
  }

  @GetMapping(params = "email")
  @ResponseBody
  public ResponseEntity<OrderResource> findByEmail(@RequestParam(value = "email") String email,
      UriComponentsBuilder builder) {
    HttpHeaders responseHeaders = new HttpHeaders();
    try {
      Order order = orderService.findByEmail(email);
      OrderResource orderResource = orderResourceAssembler.toResource(order);
      responseHeaders
          .setLocation(builder.path(RESTConstants.SLASH + DomainConstants.ORDERS + RESTConstants.SLASH + "{id}")
              .buildAndExpand(order.getId()).toUri());
      return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(orderResource);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping(params = { "openingDateTime", "closingDateTime" })
  @ResponseBody
  public ResponseEntity<PagedResources<OrderResource>> allByOrderedOnBetween(
      @RequestParam(value = "openingDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime openingDateTime,
      @RequestParam(value = "closingDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime closingDateTime,
      @PageableDefault(sort = { "orderedOn" }, direction = Sort.Direction.ASC) Pageable pageable, Sort sort,
      PagedResourcesAssembler<Order> pagedResourcesAssembler, UriComponentsBuilder builder) {
    sort = CommonUtils.stripColumnsFromSorting(sort, nonSortableColumns);
    orderService.addSortToPageable(pageable, sort);
    Page<Order> foundOrders = orderService.findAllByOrderedOnBetween(openingDateTime, closingDateTime, pageable);
    PagedResources<OrderResource> orderPagedResources = pagedResourcesAssembler.toResource(foundOrders,
        orderResourceAssembler);
    UriComponentsBuilder uriComponentsBuilder = builder.path(RESTConstants.SLASH + DomainConstants.ORDERS)
        .queryParam("openingDateTime", openingDateTime).queryParam("closingDateTime", closingDateTime);
    resourceService.addPageableToUri(uriComponentsBuilder, pageable);
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.setLocation(uriComponentsBuilder.buildAndExpand().toUri());
    return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(orderPagedResources);
  }

}
