package sh.jmx.jmxsh.cmd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.StringWriter;

import javax.management.MBeanServerConnection;

import sh.jmx.jmxsh.MockSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DomainsCommandTest {
  private DomainsCommand command;

  /** Set up objects to test */
  @BeforeEach
  void setUp() {
    command = new DomainsCommand();
  }

  /**
   * Test normal execution
   *
   * @throws Exception
   */
  @Test
  void execution() throws Exception {
    MBeanServerConnection con = mock(MBeanServerConnection.class);
    StringWriter output = new StringWriter();
    when(con.getDomains()).thenReturn(new String[] {"a", "b"});
    command.setSession(new MockSession(output, con));
    command.execute();
    verify(con).getDomains();
    assertThat(output.toString().trim()).isEqualTo("a" + System.lineSeparator() + "b");
  }
}
