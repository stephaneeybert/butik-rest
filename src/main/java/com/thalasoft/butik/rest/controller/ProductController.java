package com.thalasoft.butik.rest.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;

import com.thalasoft.butik.data.exception.EntityNotFoundException;
import com.thalasoft.butik.data.jpa.domain.Product;
import com.thalasoft.butik.data.service.ProductService;
import com.thalasoft.butik.rest.assembler.ProductResourceAssembler;
import com.thalasoft.butik.rest.resource.ProductResource;
import com.thalasoft.butik.rest.service.ResourceService;
import com.thalasoft.butik.rest.utils.CommonUtils;
import com.thalasoft.butik.rest.utils.DomainConstants;
import com.thalasoft.butik.rest.utils.RESTConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
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

import springfox.documentation.annotations.ApiIgnore;

@Controller
@RequestMapping(RESTConstants.SLASH + DomainConstants.PRODUCTS)
public class ProductController {

  @Autowired
  private ResourceService resourceService;

  @Autowired
  private ProductService productService;

  @Autowired
  private ProductResourceAssembler productResourceAssembler;

  private static final String RESOURCE_PROPERTY_ID = "id";
  private static final Set<String> nonSortableColumns = new HashSet<String>(Arrays.asList(RESOURCE_PROPERTY_ID));

  @GetMapping(value = RESTConstants.SLASH + "{id}")
  @ResponseBody
  public ResponseEntity<ProductResource> findById(@PathVariable Long id, UriComponentsBuilder builder) {
    try {
      Product product = productService.findById(id);
      ProductResource productResource = productResourceAssembler.toResource(product);
      URI location = builder.path(RESTConstants.SLASH + DomainConstants.PRODUCTS + RESTConstants.SLASH + "{id}")
          .buildAndExpand(product.getId()).toUri();
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.setLocation(location);
      return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(productResource);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping
  @ResponseBody
  public ResponseEntity<ProductResource> add(@Valid @RequestBody ProductResource productResource,
      UriComponentsBuilder builder) {
    Product product = productService.add(resourceService.toProduct(productResource));
    ProductResource createdProductResource = null;
    URI location = builder.path(RESTConstants.SLASH + DomainConstants.PRODUCTS + RESTConstants.SLASH + "{id}")
        .buildAndExpand(product.getId()).toUri();
    createdProductResource = productResourceAssembler.toResource(product);
    return ResponseEntity.created(location).body(createdProductResource);
  }

  @PutMapping(value = RESTConstants.SLASH + "{id}")
  @ResponseBody
  public ResponseEntity<ProductResource> update(@PathVariable Long id, @Valid @RequestBody ProductResource productResource,
      UriComponentsBuilder builder) {
    Product product = productService.update(id, resourceService.toProduct(productResource));
    ProductResource updatedProductResource = productResourceAssembler.toResource(product);
    URI location = builder.path(RESTConstants.SLASH + DomainConstants.PRODUCTS + RESTConstants.SLASH + "{id}")
        .buildAndExpand(product.getId()).toUri();
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.setLocation(location);
    return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(updatedProductResource);
  }

  @PatchMapping(value = RESTConstants.SLASH + "{id}")
  @ResponseBody
  public ResponseEntity<ProductResource> partialUpdate(@PathVariable Long id,
      @Valid @RequestBody ProductResource productResource, UriComponentsBuilder builder) {
    Product product = productService.partialUpdate(id, resourceService.toProduct(productResource));
    ProductResource updatedProductResource = productResourceAssembler.toResource(product);
    URI location = builder.path(RESTConstants.SLASH + DomainConstants.PRODUCTS + RESTConstants.SLASH + "{id}")
        .buildAndExpand(product.getId()).toUri();
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.setLocation(location);
    return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(updatedProductResource);
  }

  @DeleteMapping(value = RESTConstants.SLASH + "{id}")
  @ResponseBody
  public ResponseEntity<ProductResource> delete(@PathVariable Long id) {
    Product product = productService.delete(id);
    ProductResource productResource = productResourceAssembler.toResource(product);
    return ResponseEntity.ok(productResource);
  }

  @GetMapping
  @ResponseBody
  public ResponseEntity<PagedResources<ProductResource>> all(
    @ApiIgnore(
      "Ignored because swagger ui shows the wrong params, " +
      "instead they are explained in the implicit params"
    ) @PageableDefault(sort = { "name" }, direction = Sort.Direction.ASC) Pageable pageable, Sort sort,
      PagedResourcesAssembler<Product> pagedResourcesAssembler, UriComponentsBuilder builder) {
    sort = CommonUtils.stripColumnsFromSorting(sort, nonSortableColumns);
    productService.addSortToPageable(pageable, sort);
    Page<Product> foundProducts = productService.all(pageable);
    PagedResources<ProductResource> productPagedResources = pagedResourcesAssembler.toResource(foundProducts,
        productResourceAssembler);
    UriComponentsBuilder uriComponentsBuilder = builder.path(RESTConstants.SLASH + DomainConstants.PRODUCTS);
    resourceService.addPageableToUri(uriComponentsBuilder, pageable);
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.setLocation(uriComponentsBuilder.buildAndExpand().toUri());
    return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(productPagedResources);
  }

  @GetMapping(params = "searchTerm")
  @ResponseBody
  public ResponseEntity<PagedResources<ProductResource>> search(@RequestParam(value = "searchTerm") String searchTerm,
    @ApiIgnore(
      "Ignored because swagger ui shows the wrong params, " +
      "instead they are explained in the implicit params"
    ) @PageableDefault(sort = { "name" }, direction = Sort.Direction.ASC) Pageable pageable, Sort sort,
      PagedResourcesAssembler<Product> pagedResourcesAssembler, UriComponentsBuilder builder) {
    sort = CommonUtils.stripColumnsFromSorting(sort, nonSortableColumns);
    productService.addSortToPageable(pageable, sort);
    Page<Product> foundProducts = productService.search(searchTerm, pageable);
    Link selfLink = linkTo(
        methodOn(ProductController.class).search(searchTerm, pageable, sort, pagedResourcesAssembler, builder))
            .withSelfRel();
    PagedResources<ProductResource> productPagedResources = pagedResourcesAssembler.toResource(foundProducts,
        productResourceAssembler, selfLink);
    UriComponentsBuilder uriComponentsBuilder = builder.path(RESTConstants.SLASH + DomainConstants.PRODUCTS);
    uriComponentsBuilder.queryParam("searchTerm", searchTerm);
    resourceService.addPageableToUri(uriComponentsBuilder, pageable);
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.setLocation(uriComponentsBuilder.buildAndExpand().toUri());
    return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(productPagedResources);
  }

}
