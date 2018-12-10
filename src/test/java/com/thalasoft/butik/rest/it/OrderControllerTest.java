package com.thalasoft.butik.rest.it;

import static com.thalasoft.butik.rest.assertion.OrderResourceAssert.assertThatOrderResource;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.thalasoft.butik.rest.exception.ErrorFormInfo;
import com.thalasoft.butik.rest.resource.OrderResource;
import com.thalasoft.butik.rest.utils.RESTConstants;
import com.thalasoft.butik.rest.utils.DomainConstants;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

public class OrderControllerTest extends UnsecuredBaseTest {

  @Test
  public void testCrudOperations() throws Exception {
    MvcResult mvcResult = this.mockMvc
        .perform(post(RESTConstants.SLASH + DomainConstants.ORDERS).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(jacksonObjectMapper.writeValueAsString(orderResource0)))
        .andDo(print())
        .andExpect(status().isCreated()).andExpect(jsonPath("$.orderRefId").exists())
        .andExpect(jsonPath("$.orderRefId").value(orderResource0.getOrderRefId()))
        .andExpect(jsonPath("$.email").value(orderResource0.getEmail()))
        .andExpect(header().string("Location", Matchers.containsString(RESTConstants.SLASH + DomainConstants.ORDERS)))
        .andReturn();
    OrderResource retrievedOrderResource = deserializeResource(mvcResult, OrderResource.class);
    orderResource0.setResourceId(retrievedOrderResource.getResourceId());
    assertThatOrderResource(retrievedOrderResource).hasEmail(orderResource0.getEmail());
    assertThatOrderResource(retrievedOrderResource)
        .hasProduct(retrievedOrderResource.getProductResources().iterator().next());

    mvcResult = this.mockMvc.perform(
        get(RESTConstants.SLASH + DomainConstants.ORDERS + RESTConstants.SLASH + retrievedOrderResource.getResourceId())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk()).andReturn();
    retrievedOrderResource = deserializeResource(mvcResult, OrderResource.class);
    assertThatOrderResource(retrievedOrderResource).hasEmail(orderResource0.getEmail());

    Integer changedOrderRefId = 77;
    retrievedOrderResource.setOrderRefId(changedOrderRefId);
    mvcResult = this.mockMvc.perform(
        put(RESTConstants.SLASH + DomainConstants.ORDERS + RESTConstants.SLASH + retrievedOrderResource.getResourceId())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper.writeValueAsString(retrievedOrderResource)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.orderRefId").value(changedOrderRefId))
        .andReturn();
    retrievedOrderResource = deserializeResource(mvcResult, OrderResource.class);
    assertThatOrderResource(retrievedOrderResource).hasOrderRefId(changedOrderRefId);

    this.mockMvc
        .perform(delete(
            RESTConstants.SLASH + DomainConstants.ORDERS + RESTConstants.SLASH + retrievedOrderResource.getResourceId())
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk());
    orderResource0.setResourceId(null);

    this.mockMvc.perform(
        get(RESTConstants.SLASH + DomainConstants.ORDERS + RESTConstants.SLASH + retrievedOrderResource.getResourceId())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isNotFound());
  }

  @Test
  public void testPostInvalidOrderShouldReturnValidationErrorMessages() throws Exception {
    OrderResource faultyOrderResource = new OrderResource();
    faultyOrderResource.setOrderRefId(3);
    faultyOrderResource.setEmail("notvalidemail");
    faultyOrderResource.setOrderedOn(LocalDateTime.now());
    MvcResult mvcResult = this.mockMvc
        .perform(post(RESTConstants.SLASH + DomainConstants.ORDERS).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).locale(Locale.FRENCH)
            .content(jacksonObjectMapper.writeValueAsString(faultyOrderResource)))
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
  public void testFindAllBetweenOrderedOnShouldReturnSome() throws Exception {
    LocalDateTime openingDateTime = LocalDateTime.now().minusMinutes(10);
    LocalDateTime closingDateTime = LocalDateTime.now();
    this.mockMvc
        .perform(get(RESTConstants.SLASH + DomainConstants.ORDERS)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .param("openingDateTime", openingDateTime.toString())
        .param("closingDateTime", closingDateTime.toString())
        .param("page", "0").param("size", "10").param("sort", "orderedOn,asc"))
        .andDo(print())
        .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.page.number").value(0)).andExpect(jsonPath("$.page.size").value(10))
        .andExpect(jsonPath("$.page.totalPages").value(1)).andExpect(jsonPath("$.page.totalElements").value(10))
        .andExpect(jsonPath("$._links.self.href").exists())
        .andExpect(jsonPath("$._links.prev.href").doesNotExist())
        .andExpect(jsonPath("$._links.next.href").doesNotExist())
        .andExpect(jsonPath("$._embedded.orderResourceList[0].orderRefId").exists())
        .andExpect(
            jsonPath("$._embedded.orderResourceList[0].orderRefId").value(manyOrderResources.get(9).getOrderRefId()))
        .andExpect(jsonPath("$._embedded.orderResourceList[0].email").value(manyOrderResources.get(9).getEmail()))
        .andExpect(header().string("Location",
            Matchers.containsString(RESTConstants.SLASH + DomainConstants.ORDERS + "?openingDateTime=" + openingDateTime.toString())))
        .andReturn();
  }

  @Test
  public void testPaginationIsZeroIndexed() throws Exception {
    this.mockMvc
        .perform(get(RESTConstants.SLASH + DomainConstants.ORDERS).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).param("page", "2").param("size", "10").param("sort", "orderedOn,asc"))
        .andDo(print()).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$._links.first.href", Matchers.containsString("page=0")))
        .andExpect(jsonPath("$._links.prev.href", Matchers.containsString("page=1")))
        .andExpect(
            jsonPath("$._links.self.href", Matchers.containsString(RESTConstants.SLASH + DomainConstants.ORDERS)))
        .andExpect(jsonPath("$._links.last.href", Matchers.containsString("page=2")))
        .andExpect(jsonPath("$.page.size").value(10)).andExpect(jsonPath("$.page.totalElements").value(30))
        .andExpect(jsonPath("$.page.totalPages").value(3)).andExpect(jsonPath("$.page.number").value(2)).andReturn();
  }

}
