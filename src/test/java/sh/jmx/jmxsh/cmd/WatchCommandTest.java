package sh.jmx.jmxsh.cmd;

import static org.assertj.core.api.Assertions.assertThat;

import sh.jmx.jmxsh.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WatchCommandTest {

  @Mock
  private Session session;

  private WatchCommand command;

  @BeforeEach
  void setUp() {
    command = new WatchCommand();
  }

  @Test
  void suggestArgumentWithNoBean() {
    command.setSession(session);
    assertThat(command.suggestArgument(null)).isEmpty();
  }
}
