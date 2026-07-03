package sh.jmx.jmxsh.io;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

class PrintStreamCommandOutputTest {
  /** Write something to output and verify what's written */
  @Test
  void print() {
    ByteArrayOutputStream w1 = new ByteArrayOutputStream();
    ByteArrayOutputStream w2 = new ByteArrayOutputStream();

    PrintStreamCommandOutput output =
        new PrintStreamCommandOutput(new PrintStream(w1), new PrintStream(w2));
    output.println("hello world");
    output.printMessage("yeeha");

    assertThat(new String(w1.toByteArray()).trim()).isEqualTo("hello world");
    assertThat(new String(w2.toByteArray()).trim()).isEqualTo("yeeha");
  }

  @Test
  void constructorThrowsWhenResultOutputNull() {
    assertThatThrownBy(() -> new PrintStreamCommandOutput(null, System.out))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void constructorThrowsWhenMessageOutputNull() {
    assertThatThrownBy(() -> new PrintStreamCommandOutput(System.out, null))
        .isInstanceOf(NullPointerException.class);
  }
}
