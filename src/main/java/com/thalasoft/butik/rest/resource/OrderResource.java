package com.thalasoft.butik.rest.resource;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderResource extends AbstractResource {

  @NotNull
  private Integer orderRefId;
  @NotEmpty
  @Email
  private String email;
  @NotNull
  private LocalDateTime orderedOn;
  @Valid
  private Set<ProductResource> productResources = new HashSet<ProductResource>();

}
