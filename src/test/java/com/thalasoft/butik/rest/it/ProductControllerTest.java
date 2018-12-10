package com.thalasoft.butik.rest.it;

import static com.thalasoft.butik.rest.assertion.ProductResourceAssert.assertThatProductResource;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Locale;

import com.thalasoft.butik.rest.exception.ErrorFormInfo;
import com.thalasoft.butik.rest.resource.ProductResource;
import com.thalasoft.butik.rest.utils.RESTConstants;
import com.thalasoft.butik.rest.utils.DomainConstants;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

public class ProductControllerTest extends UnsecuredBaseTest {

  @Test
  public void testCrudOperations() throws Exception {
    MvcResult mvcResult = this.mockMvc
        .perform(post(RESTConstants.SLASH + DomainConstants.PRODUCTS).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(jacksonObjectMapper.writeValueAsString(productResource0)))
        .andDo(print()).andExpect(status().isCreated()).andExpect(jsonPath("$.name").exists())
        .andExpect(jsonPath("$.name").value(productResource0.getName()))
        .andExpect(jsonPath("$.price").value(productResource0.getPrice()))
        .andExpect(header().string("Location", Matchers.containsString(RESTConstants.SLASH + DomainConstants.PRODUCTS)))
        .andReturn();
    ProductResource retrievedProductResource = deserializeResource(mvcResult, ProductResource.class);
    productResource0.setResourceId(retrievedProductResource.getResourceId());
    assertThatProductResource(retrievedProductResource).hasName(productResource0.getName());
    assertThatProductResource(retrievedProductResource).hasPrice(retrievedProductResource.getPrice());

    mvcResult = this.mockMvc.perform(get(
        RESTConstants.SLASH + DomainConstants.PRODUCTS + RESTConstants.SLASH + retrievedProductResource.getResourceId())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk()).andReturn();
    retrievedProductResource = deserializeResource(mvcResult, ProductResource.class);
    assertThatProductResource(retrievedProductResource).hasName(productResource0.getName());

    String changedName = "Boiler New!";
    retrievedProductResource.setName(changedName);
    mvcResult = this.mockMvc
        .perform(put(RESTConstants.SLASH + DomainConstants.PRODUCTS + RESTConstants.SLASH
            + retrievedProductResource.getResourceId()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jacksonObjectMapper.writeValueAsString(retrievedProductResource)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.name").value(changedName)).andReturn();
    retrievedProductResource = deserializeResource(mvcResult, ProductResource.class);
    assertThatProductResource(retrievedProductResource).hasName(changedName);

    this.mockMvc.perform(delete(
        RESTConstants.SLASH + DomainConstants.PRODUCTS + RESTConstants.SLASH + retrievedProductResource.getResourceId())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk());
    productResource0.setResourceId(null);

    this.mockMvc.perform(get(
        RESTConstants.SLASH + DomainConstants.PRODUCTS + RESTConstants.SLASH + retrievedProductResource.getResourceId())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isNotFound());
  }

  @Test
  public void testPostInvalidProductShouldReturnValidationErrorMessages() throws Exception {
    ProductResource faultyProductResource = new ProductResource();
    faultyProductResource.setName("Water cooler");
    faultyProductResource.setPrice(null);
    MvcResult mvcResult = this.mockMvc
        .perform(post(RESTConstants.SLASH + DomainConstants.PRODUCTS).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).locale(Locale.FRENCH)
            .content(jacksonObjectMapper.writeValueAsString(faultyProductResource)))
        .andDo(print()).andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.message").value(localizeErrorMessage("error.failed.controller.validation", Locale.FRENCH)))
        .andReturn();
    ErrorFormInfo retrievedMessage = deserialize(mvcResult, ErrorFormInfo.class);
    assertEquals(retrievedMessage.getHttpStatus(), HttpStatus.BAD_REQUEST);
    assertEquals(retrievedMessage.getMessage(),
        localizeErrorMessage("error.failed.controller.validation", Locale.FRENCH));
    assertEquals(retrievedMessage.getFieldErrors().size(), 1);
  }

  @Test
  public void testSearchShouldReturnSome() throws Exception {
    this.mockMvc
        .perform(get(RESTConstants.SLASH + DomainConstants.PRODUCTS).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).param("searchTerm", "produ").param("page", "0").param("size", "10")
            .param("sort", "name,asc").param("sort", "price,asc"))
        .andDo(print()).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.page.number").value(0)).andExpect(jsonPath("$.page.size").value(10))
        .andExpect(jsonPath("$.page.totalPages").value(3)).andExpect(jsonPath("$.page.totalElements").value(30))
        .andExpect(jsonPath("$._links.self.href").exists()).andExpect(jsonPath("$._links.next.href").exists())
        .andExpect(jsonPath("$._links.prev.href").doesNotExist())
        .andExpect(jsonPath("$._embedded.productResourceList[0].name").exists())
        .andExpect(jsonPath("$._embedded.productResourceList[0].name").value(manyProductResources.get(0).getName()))
        .andExpect(jsonPath("$._embedded.productResourceList[0].price").value(manyProductResources.get(0).getPrice()))
        .andExpect(header().string("Location", Matchers.containsString(RESTConstants.SLASH + DomainConstants.PRODUCTS + "?searchTerm="))).andReturn();
  }

  @Test
  public void testPaginationIsZeroIndexed() throws Exception {
    this.mockMvc
        .perform(get(RESTConstants.SLASH + DomainConstants.PRODUCTS).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).param("searchTerm", "oduct").param("page", "1").param("size", "10")
            .param("sort", "name,asc").param("sort", "price,asc"))
        .andDo(print()).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$._links.first.href", Matchers.containsString("page=0")))
        .andExpect(jsonPath("$._links.prev.href", Matchers.containsString("page=0")))
        .andExpect(jsonPath("$._links.self.href", Matchers.containsString(RESTConstants.SLASH + DomainConstants.PRODUCTS + "?searchTerm=oduct")))
        .andExpect(jsonPath("$._links.next.href", Matchers.containsString("page=2")))
        .andExpect(jsonPath("$.page.size").value(10)).andExpect(jsonPath("$.page.totalElements").value(30))
        .andExpect(jsonPath("$.page.totalPages").value(3))
        .andExpect(jsonPath("$.page.number").value(1)).andReturn();
  }

}
