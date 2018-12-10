package com.thalasoft.butik.rest.resource;

import javax.validation.constraints.NotEmpty;

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
public class ProductResource extends AbstractResource {

  @NotEmpty
  private String name;
  @NotEmpty
  private String price;

}
