package org.cyclopsgroup.jmxterm.cc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.management.remote.JMXConnector;

import org.cyclopsgroup.jmxterm.SyntaxUtils;
import org.junit.jupiter.api.Test;

/**
 * Test case of {@link ConnectionImpl}
 *
 */
class ConnectionImplTest {
  /**
   * Test the object is constructed correctly
   *
   * @throws IOException
   */
  @Test
  void construction() throws Exception {
    JMXConnector con = mock(JMXConnector.class);
    ConnectionImpl c = new ConnectionImpl(con, SyntaxUtils.getUrl("localhost:9991", null));
    assertThat(c.getConnector()).isSameAs(con);

    when(con.getConnectionId()).thenReturn("xyz");
    assertThat(c.getConnectorId()).isEqualTo("xyz");
    verify(con).getConnectionId();
  }
}
