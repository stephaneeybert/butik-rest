package com.thalasoft.butik.rest.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import javax.servlet.http.HttpServletResponse;

import com.thalasoft.butik.data.jpa.domain.Product;
import com.thalasoft.butik.rest.utils.RESTConstants;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping(RESTConstants.SLASH)
public class RootController {

  @GetMapping
  @ResponseBody
  public HttpEntity<ResourceSupport> root(final PagedResourcesAssembler<Product> pagedResourcesAssembler,
      final UriComponentsBuilder builder, final HttpServletResponse response) {
    Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "name"),
        new Sort.Order(Sort.Direction.ASC, "price"));
    Pageable pageable = PageRequest.of(1, 10, sort);
    Link search = linkTo(
        methodOn(ProductController.class).search("searchTerm", pageable, sort, pagedResourcesAssembler, builder))
            .withRel("products search");
    final StringBuilder links = new StringBuilder();
    links.append(search);
    response.addHeader("Link", links.toString());
    ResourceSupport resource = new ResourceSupport();
    resource.add(search);
    return ResponseEntity.ok(resource);
  }

}
