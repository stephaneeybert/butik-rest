package com.thalasoft.butik.rest.it;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.thalasoft.butik.data.jpa.domain.Order;
import com.thalasoft.butik.data.jpa.domain.Product;
import com.thalasoft.butik.data.service.OrderService;
import com.thalasoft.butik.data.service.ProductService;
import com.thalasoft.butik.rest.resource.AbstractResource;
import com.thalasoft.butik.rest.resource.OrderResource;
import com.thalasoft.butik.rest.resource.ProductResource;
import com.thalasoft.butik.rest.service.ResourceService;
import com.thalasoft.toolbox.utils.CommonTools;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@RunWith(SpringRunner.class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    "classpath:mysql/clean-up-before-each-test.sql" })
public abstract class BaseTest {

  private static final int MAX_COLLECTION = 30;

  protected ProductResource productResource0;
  protected List<ProductResource> manyProductResources;
  protected OrderResource orderResource0;
  protected List<OrderResource> manyOrderResources;

  @Autowired
  private OrderService orderService;

  @Autowired
  private ProductService productService;

  @Autowired
  private ResourceService resourceService;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  protected ObjectMapper jacksonObjectMapper;

  @Autowired
  protected MessageSource messageSource;

  @Autowired
  private AcceptHeaderLocaleResolver localeResolver;

  protected MockHttpSession session;

  protected MockHttpServletRequest request;

  protected MockMvc mockMvc;

  protected HttpHeaders httpHeaders;

  @Before
  public void setup() throws Exception {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    httpHeaders = new HttpHeaders();
  }

  @Before
  public void createResources() throws Exception {
    productResource0 = new ProductResource();
    productResource0.setName("Water cooler");
    productResource0.setPrice("10");

    manyProductResources = new ArrayList<ProductResource>();
    for (int i = 0; i < MAX_COLLECTION; i++) {
      String index = CommonTools.formatSortableStringNumber(i, 2);
      ProductResource oneProductResource = new ProductResource();
      oneProductResource.setName("product" + index);
      oneProductResource.setPrice("10");
      Product createdProduct = productService.add(resourceService.toProduct(oneProductResource));
      oneProductResource.setResourceId(createdProduct.getId());
      manyProductResources.add(oneProductResource);
    }

    orderResource0 = new OrderResource();
    orderResource0.setOrderRefId(10);
    orderResource0.setEmail("peter@gmail.com");
    orderResource0.setOrderedOn(LocalDateTime.now());
    orderResource0.setProductResources(new HashSet<ProductResource>(manyProductResources));

    manyOrderResources = new ArrayList<OrderResource>();
    for (int i = 0; i < MAX_COLLECTION; i++) {
      String index = CommonTools.formatSortableStringNumber(i, 2);
      OrderResource oneOrderResource = new OrderResource();
      oneOrderResource.setOrderRefId(i);
      oneOrderResource.setEmail("peter" + index + "@gmail.com");
      oneOrderResource.setOrderedOn(LocalDateTime.now().minusMinutes(i));
      oneOrderResource.setProductResources(new HashSet<ProductResource>(manyProductResources));
      Order createdOrder = orderService.add(resourceService.toOrder(oneOrderResource));
      oneOrderResource.setResourceId(createdOrder.getId());
      manyOrderResources.add(oneOrderResource);
    }
  }

  @After
  public void deleteResources() throws Exception {
    for (OrderResource orderResource : manyOrderResources) {
      if (orderResource != null && !StringUtils.isEmpty(orderResource.getResourceId())) {
        orderService.delete(orderResource.getResourceId());
      }
    }

    for (ProductResource productResource : manyProductResources) {
      if (productResource != null && !StringUtils.isEmpty(productResource.getResourceId())) {
        productService.delete(productResource.getResourceId());
      }
    }
  }

  protected String localizeErrorMessage(String errorCode, Object args[], Locale locale) {
    return messageSource.getMessage(errorCode, args, locale);
  }

  protected String localizeErrorMessage(String errorCode, Locale locale) {
    return messageSource.getMessage(errorCode, null, locale);
  }

  protected String localizeErrorMessage(String errorCode, Object args[]) {
    Locale locale = localeResolver.getDefaultLocale();
    return messageSource.getMessage(errorCode, args, locale);
  }

  protected String localizeErrorMessage(String errorCode) {
    Locale locale = localeResolver.getDefaultLocale();
    return messageSource.getMessage(errorCode, null, locale);
  }

  protected <T extends Object> T deserialize(final MvcResult mvcResult, Class<T> clazz) throws Exception {
    return jacksonObjectMapper.readValue(mvcResult.getResponse().getContentAsString(), clazz);
  }

  protected <T extends AbstractResource> T deserializeResource(final MvcResult mvcResult, Class<T> clazz)
      throws Exception {
    return jacksonObjectMapper.readValue(mvcResult.getResponse().getContentAsString(), clazz);
  }

  protected <T extends AbstractResource> List<T> deserializeResources(final MvcResult mvcResult, Class<T> clazz)
      throws Exception {
    final CollectionType javaType = jacksonObjectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
    return jacksonObjectMapper.readValue(mvcResult.getResponse().getContentAsString(), javaType);
  }

}
