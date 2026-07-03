package sh.jmx.jmxsh.io;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.function.Supplier;

import org.jline.reader.impl.LineReaderImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JlineCommandInputTest {
  @Mock
  private LineReaderImpl console;

  @Test
  void constructorThrowsWhenConsoleNull() {
    Supplier<String> prompt = () -> "> ";
    assertThatThrownBy(() -> new JlineCommandInput(null, prompt))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void constructorThrowsWhenPromptSupplierNull() {
    assertThatThrownBy(() -> new JlineCommandInput(console, null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void constructorStoresConsole() {
    JlineCommandInput input = new JlineCommandInput(console, () -> "> ");
    assertThat(input.getConsole()).isSameAs(console);
  }
}
