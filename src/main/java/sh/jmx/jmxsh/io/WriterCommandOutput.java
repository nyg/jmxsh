package sh.jmx.jmxsh.io;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

public class WriterCommandOutput implements CommandOutput {
  private final Writer messageOutput;

  private final Writer resultOutput;

  public WriterCommandOutput(Writer output) {
    this(output, output);
  }

  public WriterCommandOutput(Writer resultOutput, Writer messageOutput) {
    Objects.requireNonNull(resultOutput, "Result output can't be NULL");
    this.resultOutput = resultOutput;
    this.messageOutput = messageOutput == null ? Writer.nullWriter() : messageOutput;
  }

  @Override
  public void print(String output) {
    if (output == null) {
      return;
    }
    try {
      resultOutput.write(output);
    } catch (IOException e) {
      throw new RuntimeIOException("Can't print out result", e);
    }
  }

  @Override
  public void printError(Throwable e) {
    try {
      messageOutput.write("#" + e.getMessage());
    } catch (IOException ex) {
      throw new RuntimeIOException("Can't print error message", ex);
    }
  }

  @Override
  public void printMessage(String message) {
    try {
      messageOutput.write(message);
    } catch (IOException e) {
      throw new RuntimeIOException("Can't print out message", e);
    }
  }
}
