package sh.jmx.jmxsh.cmd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringWriter;

import javax.management.JMException;
import javax.management.MBeanServerConnection;

import sh.jmx.jmxsh.Connection;
import sh.jmx.jmxsh.Session;
import sh.jmx.jmxsh.io.WriterCommandOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test of {@link DomainCommand}
 *
 */
@ExtendWith(MockitoExtension.class)
class DomainCommandTest {
  @Mock
  private Session session;
  @Mock
  private Connection connection;
  @Mock
  private MBeanServerConnection con;

  private DomainCommand command;
  private StringWriter writer;

  private void setDomainAndVerify(String domainName, String[] knownDomains) throws IOException {
    command.setDomain(domainName);
    when(session.getConnection()).thenReturn(connection);
    when(connection.getServerConnection()).thenReturn(con);
    when(con.getDomains()).thenReturn(knownDomains);
    command.setSession(session);
    command.execute();
    verify(session).setDomain(domainName);
    verify(con).getDomains();
  }

  /** Set up command to test */
  @BeforeEach
  void setUp() throws IOException {
    command = new DomainCommand();
    writer = new StringWriter();
    lenient().when(session.getOutput()).thenReturn(new WriterCommandOutput(writer, null));
  }

  /**
   * Test execution and get empty result
   *
   * @throws IOException Allows network IO errors
   * @throws JMException Allows JMX errors
   */
  @Test
  void executeWithGettingNull() throws Exception {
    when(session.getOutput()).thenReturn(new WriterCommandOutput(writer, null));
    command.setSession(session);
    command.execute();
    assertThat(writer.toString().trim()).isEqualTo("null");
  }

  /**
   * Test execution and get valid result
   *
   * @throws IOException Allows network IO errors
   * @throws JMException Allows JMX errors
   */
  @Test
  void executeWithGettingSomething() throws Exception {
    when(session.getDomain()).thenReturn("something");
    when(session.getOutput()).thenReturn(new WriterCommandOutput(writer, null));
    command.setSession(session);
    command.execute();
    assertThat(writer.toString().trim()).isEqualTo("something");
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
