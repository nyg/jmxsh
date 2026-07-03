package sh.jmx.jmxsh.cc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConsoleCompleterTest {
  @Mock
  private CommandCenter commandCenter;

  @Test
  void constructorThrowsWhenCommandCenterNull() {
    assertThatThrownBy(() -> new ConsoleCompleter(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void constructorSucceedsWithCommandCenter() {
    when(commandCenter.getCommandNames()).thenReturn(new HashSet<>(List.of("a", "b")));
    ConsoleCompleter completer = new ConsoleCompleter(commandCenter);
    assertThat(completer).isNotNull();
  }
}
