package org.cyclopsgroup.jmxterm.cmd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.StringWriter;

import javax.management.JMException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.cyclopsgroup.jmxterm.MockSession;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link BeanCommand}
 *
 */
class BeanCommandTest {
  private final BeanCommand command = new BeanCommand();

  private final StringWriter output = new StringWriter();

  private void setBeanAndVerify(String beanName, String domainName, String expectedBean)
      throws IOException, JMException {
    MBeanServerConnection con = mock(MBeanServerConnection.class);
    command.setBean(beanName);
    MockSession s = new MockSession(output, con);
    if (domainName != null) {
      s.setDomain(domainName);
    }
    command.setSession(s);
    command.execute();
    assertThat(s.getBean()).isEqualTo(expectedBean);
    verify(con, atLeastOnce()).getMBeanInfo(new ObjectName(expectedBean));
  }

  /**
   * Test execution with NULL result
   *
   * @throws IOException Allows network IO errors
   * @throws JMException Allows JMX exceptions
   */
  @Test
  void executeWithGettingNull() throws Exception {
    command.setSession(new MockSession(output, null));
    command.execute();
    assertThat(output.toString().trim()).isEqualTo("null");
  }

  /**
   * Test execution with some result
   *
   * @throws IOException Allows network IO errors
   * @throws JMException Allows JMX exceptions
   */
  @Test
  void executeWithGettingSomething() throws Exception {
    MockSession s = new MockSession(output, null);
    s.setBean("something");
    command.setSession(s);
    command.execute();
    assertThat(output.toString().trim()).isEqualTo("something");
  }

  /**
   * Test the case where an illegal bean is requested
   *
   * @throws IOException Allows network IO errors
   * @throws JMException Allows JMX exceptions
   */
  @Test
  void executeWithInvalidBean() throws Exception {
    command.setBean("blablabla");
    command.setSession(new MockSession(output, null));
    assertThatThrownBy(() -> command.execute()).isInstanceOf(IllegalArgumentException.class);
  }

  /**
   * Test the case where NULL is get
   *
   * @throws IOException Allows network IO errors
   * @throws JMException Allows JMX exceptions
   */
  @Test
  void executeWithSettingNull() throws Exception {
    command.setBean("null");
    MockSession s = new MockSession(output, null);
    s.setBean("something");
    command.setSession(s);
    command.execute();
    assertThat(s.getBean()).isNull();
  }

  /**
   * Test setting names with special character such as dot, dash and underline, without setting a
   * domain first
   *
   * @throws IOException Allows network IO errors
   * @throws JMException Allows JMX exceptions
   */
  @Test
  void settingSpecialCharactersWithoutDomain() throws Exception {
    setBeanAndVerify(
        "domain_name.with-dash:attr.name_1-1=a.b", null, "domain_name.with-dash:attr.name_1-1=a.b");
  }

  /**
   * Test the case where a domain is set
   *
   * @throws IOException Allows network IO errors
   * @throws JMException Allows JMX exceptions
   */
  @Test
  void settingWithDomain() throws Exception {
    setBeanAndVerify("type=x", "something", "something:type=x");
  }

  /**
   * Test the case where domain is set
   *
   * @throws IOException Allows network IO errors
   * @throws JMException Allows JMX exceptions
   */
  @Test
  void settingWithoutDomain() throws Exception {
    setBeanAndVerify("something:type=x", null, "something:type=x");
  }

  /**
   * Test setting names with special character such as dot, dash and underline
   *
   * @throws IOException Allows network IO errors
   * @throws JMException Allows JMX exceptions
   */
  @Test
  void settingWithSpecialCharacters() throws Exception {
    setBeanAndVerify(
        "attr.name_1-1=a.b", "domain_name.with-dash", "domain_name.with-dash:attr.name_1-1=a.b");
  }
}
