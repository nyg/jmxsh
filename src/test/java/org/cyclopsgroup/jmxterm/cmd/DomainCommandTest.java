package org.cyclopsgroup.jmxterm.cmd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringWriter;

import javax.management.JMException;
import javax.management.MBeanServerConnection;

import org.cyclopsgroup.jmxterm.MockSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test of {@link DomainCommand}
 *
 */
class DomainCommandTest {
  private DomainCommand command;

  private StringWriter output;

  private void setDomainAndVerify(String domainName, String[] knownDomains) throws IOException {
    MBeanServerConnection con = mock(MBeanServerConnection.class);
    command.setDomain(domainName);
    MockSession session = new MockSession(output, con);
    when(con.getDomains()).thenReturn(knownDomains);
    command.setSession(session);
    command.execute();
    assertThat(session.getDomain()).isEqualTo(domainName);
    verify(con).getDomains();
  }

  /** Set up command to test */
  @BeforeEach
  void setUp() {
    command = new DomainCommand();
    output = new StringWriter();
  }

  /**
   * Test execution and get empty result
   *
   * @throws IOException Allows network IO errors
   * @throws JMException Allows JMX errors
   */
  @Test
  void executeWithGettingNull() throws Exception {
    command.setSession(new MockSession(output, null));
    command.execute();
    assertThat(output.toString().trim()).isEqualTo("null");
  }

  /**
   * Test execution and get valid result
   *
   * @throws IOException Allows network IO errors
   * @throws JMException Allows JMX errors
   */
  @Test
  void executeWithGettingSomething() throws Exception {
    MockSession session = new MockSession(output, null);
    session.setDomain("something");
    command.setSession(session);
    command.execute();
    assertThat(output.toString().trim()).isEqualTo("something");
  }

  /**
   * Test the case where invalid value is declined
   *
   * @throws IOException Allows network IO errors
   * @throws JMException Allows JMX errors
   */
  @Test
  void settingWithInvalidDomain() throws Exception {
    assertThatThrownBy(() -> setDomainAndVerify("invalid", new String[] {"something"}))
        .isInstanceOf(IllegalArgumentException.class);
  }

  /**
   * Test execution and set value with special characters
   *
   * @throws IOException Allows network IO errors
   * @throws JMException Allows JMX errors
   */
  @Test
  void settingWithSpecialCharacters() throws Exception {
    setDomainAndVerify("my_domain.1-1", new String[] {"my_domain.1-1", "something"});
  }

  /**
   * Test execution and set valid value
   *
   * @throws IOException Allows network IO errors
   * @throws JMException Allows JMX errors
   */
  @Test
  void settingWithValidDomain() throws Exception {
    setDomainAndVerify("something", new String[] {"something"});
  }
}
