package sh.jmx.jmxsh.io;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.StringWriter;
import java.io.Writer;

import org.junit.jupiter.api.Test;

class WriterCommandOutputTest {

  @Test
  void constructorThrowsWhenResultOutputNull() {
    assertThatThrownBy(() -> new WriterCommandOutput(null, Writer.nullWriter()))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void writesToResultOutput() {
    StringWriter writer = new StringWriter();
    WriterCommandOutput output = new WriterCommandOutput(writer);
    output.print("hello world");
    assertThat(writer.toString()).isEqualTo("hello world");
  }
}
