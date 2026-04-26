package sh.jmx.jmxsh;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

import javax.management.JMException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommandTest {

  /** Minimal Command subclass that throws from its doSuggest* methods to exercise catch branches. */
  private static class ThrowingCommand extends Command {
    @Override
    public void execute() {}

    @Override
    protected List<String> doSuggestArgument() throws IOException {
      throw new IOException("simulated failure");
    }

    @Override
    protected List<String> doSuggestOption(String optionName) throws JMException {
      throw new JMException("simulated failure");
    }
  }

  @Mock
  private Session session;

  @Test
  void suggestArgumentReturnsEmptyWhenPartialArgIsNonNull() {
    SelfRecordingCommand cmd = new SelfRecordingCommand(new java.util.ArrayList<>());
    cmd.setSession(session);
    assertThat(cmd.suggestArgument("partial")).isEmpty();
  }

  @Test
  void suggestArgumentCallsDoSuggestArgument() {
    SelfRecordingCommand cmd = new SelfRecordingCommand(new java.util.ArrayList<>());
    cmd.setSession(session);
    assertThat(cmd.suggestArgument(null)).isEmpty();
  }

  @Test
  void suggestArgumentReturnsEmptyOnException() {
    ThrowingCommand cmd = new ThrowingCommand();
    cmd.setSession(session);
    assertThat(cmd.suggestArgument(null)).isEmpty();
  }

  @Test
  void suggestOptionReturnsEmptyWhenPartialValueIsNonNull() {
    SelfRecordingCommand cmd = new SelfRecordingCommand(new java.util.ArrayList<>());
    cmd.setSession(session);
    assertThat(cmd.suggestOption("x", "partial")).isEmpty();
  }

  @Test
  void suggestOptionCallsDoSuggestOption() {
    SelfRecordingCommand cmd = new SelfRecordingCommand(new java.util.ArrayList<>());
    cmd.setSession(session);
    assertThat(cmd.suggestOption("x", null)).isEmpty();
  }

  @Test
  void suggestOptionReturnsEmptyOnException() {
    ThrowingCommand cmd = new ThrowingCommand();
    cmd.setSession(session);
    assertThat(cmd.suggestOption("x", null)).isEmpty();
  }
}
