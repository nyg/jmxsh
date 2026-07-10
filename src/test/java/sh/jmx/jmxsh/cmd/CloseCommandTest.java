package sh.jmx.jmxsh.cmd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sh.jmx.jmxsh.Session;
import sh.jmx.jmxsh.io.WriterCommandOutput;

/**
 * Test of {@link CloseCommand}
 *
 */
@ExtendWith(MockitoExtension.class)
class CloseCommandTest {

  @Mock
  private Session session;

  @Test
  void should_disconnect_and_report_when_connected() throws IOException {
    // Given
    StringWriter writer = new StringWriter();
    when(session.isConnected()).thenReturn(true);
    when(session.getOutput()).thenReturn(new WriterCommandOutput(writer));
    CloseCommand unit = new CloseCommand();
    unit.setSession(session);

    // When
    unit.execute();

    // Then
    verify(session).disconnect();
    assertThat(writer).hasToString("Disconnected.");
  }

  @Test
  void should_report_not_connected_and_skip_disconnect_when_not_connected() throws IOException {
    // Given
    StringWriter writer = new StringWriter();
    when(session.isConnected()).thenReturn(false);
    when(session.getOutput()).thenReturn(new WriterCommandOutput(writer));
    CloseCommand unit = new CloseCommand();
    unit.setSession(session);

    // When
    unit.execute();

    // Then
    verify(session, never()).disconnect();
    assertThat(writer).hasToString("Not connected.");
  }
}
