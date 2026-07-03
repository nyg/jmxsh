package sh.jmx.jmxsh.io;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VerboseCommandOutputTest {
  @Mock
  private CommandOutput output;

  @Test
  void constructorThrowsWhenOutputNull() {
    assertThatThrownBy(() -> new VerboseCommandOutput(null, () -> OutputMode.BRIEF))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void constructorThrowsWhenModeSupplierNull() {
    assertThatThrownBy(() -> new VerboseCommandOutput(output, null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void delegatesPrintToWrappedOutput() {
    VerboseCommandOutput verbose = new VerboseCommandOutput(output, () -> OutputMode.BRIEF);
    verbose.print("value");
    verify(output).print("value");
  }
}
