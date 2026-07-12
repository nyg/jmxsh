package sh.jmx.jmxsh.io;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.StringWriter;
import java.io.Writer;

import org.junit.jupiter.api.Test;

class WriterCommandOutputTest {

  @Test
  void constructorThrowsWhenResultOutputNull() {
    Writer messageOutput = Writer.nullWriter();
    assertThatThrownBy(() -> new WriterCommandOutput(null, messageOutput))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void writesToResultOutput() {
    StringWriter writer = new StringWriter();
    WriterCommandOutput output = new WriterCommandOutput(writer);
    output.print("hello world");
    assertThat(writer).hasToString("hello world");
  }

  @Test
  void printMessageAppendsNewlineToMessageOutput() {
    StringWriter result = new StringWriter();
    StringWriter message = new StringWriter();
    WriterCommandOutput output = new WriterCommandOutput(result, message);
    output.printMessage("status");
    assertThat(message).hasToString("status" + System.lineSeparator());
    assertThat(result).hasToString("");
  }
}
