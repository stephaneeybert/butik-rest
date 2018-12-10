package com.thalasoft.butik.rest.it;

import com.thalasoft.butik.rest.config.TestConfiguration;
import com.thalasoft.butik.rest.config.WebConfiguration;

import org.junit.Before;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = { TestConfiguration.class, WebConfiguration.class })
public abstract class UnsecuredBaseTest extends BaseTest {

  @Before
  public void setup() throws Exception {
    super.setup();
  }

}
