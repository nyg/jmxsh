package sh.jmx.jmxsh.io;

import java.io.IOException;
import java.io.Writer;

import lombok.NonNull;

public class WriterCommandOutput implements CommandOutput {

  private final Writer messageOutput;
  private final Writer resultOutput;

  public WriterCommandOutput(Writer output) {
    this(output, output);
  }

  public WriterCommandOutput(@NonNull Writer resultOutput, Writer messageOutput) {
    this.resultOutput = resultOutput;
    this.messageOutput = messageOutput == null ? Writer.nullWriter() : messageOutput;
  }

  @Override
  public void print(String output) {
    if (output == null) {
      return;
    }
    write(resultOutput, output, "Can't print out result");
  }

  @Override
  public void printMessage(String message) {
    write(messageOutput, String.format("%s%n", message), "Can't print out message");
  }

  private void write(Writer target, String text, String errorMessage) {
    try {
      target.write(text);
    } catch (IOException e) {
      throw new RuntimeIOException(errorMessage, e);
    }
  }
}
