package sh.jmx.jmxsh.cmd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringWriter;

import javax.management.JMException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import sh.jmx.jmxsh.Connection;
import sh.jmx.jmxsh.Session;
import sh.jmx.jmxsh.io.WriterCommandOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BeanCommandTest {
  @Mock
  private Session session;
  @Mock
  private Connection connection;
  @Mock
  private MBeanServerConnection con;

  private final BeanCommand command = new BeanCommand();
  private StringWriter writer;

  @BeforeEach
  void setUp() throws IOException {
    writer = new StringWriter();
    lenient().when(session.getOutput()).thenReturn(new WriterCommandOutput(writer, null));
    lenient().when(session.getConnection()).thenReturn(connection);
    lenient().when(connection.getServerConnection()).thenReturn(con);
  }

  private void setBeanAndVerify(String beanName, String domainName, String expectedBean)
      throws IOException, JMException {
    command.setBean(beanName);
    if (domainName != null) {
      when(session.getDomain()).thenReturn(domainName);
    }
    command.setSession(session);
    command.execute();
    verify(session).setBean(expectedBean);
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
    command.setSession(session);
    command.execute();
    assertThat(writer.toString().trim()).isEqualTo("null");
  }

  /**
   * Test execution with some result
   *
   * @throws IOException Allows network IO errors
   * @throws JMException Allows JMX exceptions
   */
  @Test
  void executeWithGettingSomething() throws Exception {
    when(session.getBean()).thenReturn("something");
    command.setSession(session);
    command.execute();
    assertThat(writer.toString().trim()).isEqualTo("something");
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
    command.setSession(session);
    assertThatThrownBy(command::execute).isInstanceOf(IllegalArgumentException.class);
  }

  /**
   * Test the case where NULL is set
   *
   * @throws IOException Allows network IO errors
   * @throws JMException Allows JMX exceptions
   */
  @Test
  void executeWithSettingNull() throws Exception {
    command.setBean("null");
    command.setSession(session);
    command.execute();
    verify(session).setBean(null);
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

  @Test
  void suggestOptionWithUnknownOption() {
    command.setSession(session);
    assertThat(command.suggestOption("x", null)).isEmpty();
  }

  @Test
  void executeWhenColonBeanNotFound() throws Exception {
    command.setBean("some:type=Thing");
    when(session.getConnection()).thenReturn(connection);
    when(connection.getServerConnection()).thenReturn(con);
    when(con.getMBeanInfo(new ObjectName("some:type=Thing")))
        .thenThrow(new javax.management.InstanceNotFoundException("not found"));
    // domain is null → triggers IAE about specifying domain
    command.setSession(session);
    assertThatThrownBy(command::execute).isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Please specify domain");
  }

  @Test
  void executeWhenBeanNotFoundInDomain() throws Exception {
    command.setBean("type=Thing");
    when(session.getDomain()).thenReturn("mydomain");
    when(session.getConnection()).thenReturn(connection);
    when(connection.getServerConnection()).thenReturn(con);
    when(con.getMBeanInfo(new ObjectName("mydomain:type=Thing")))
        .thenThrow(new javax.management.InstanceNotFoundException("not found"));
    command.setSession(session);
    assertThatThrownBy(command::execute).isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("isn't valid");
  }
}
