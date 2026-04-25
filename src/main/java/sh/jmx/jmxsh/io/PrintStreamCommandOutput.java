package sh.jmx.jmxsh.io;

import java.io.PrintStream;
import java.util.Objects;

public class PrintStreamCommandOutput implements CommandOutput {
  private final PrintStream messageOutput;

  private final PrintStream resultOutput;

  public PrintStreamCommandOutput() {
    this(System.out);
  }

  public PrintStreamCommandOutput(PrintStream output) {
    this(output, System.err);
  }

  public PrintStreamCommandOutput(PrintStream resultOutput, PrintStream messageOutput) {
    Objects.requireNonNull(resultOutput, "Result output can't be NULL");
    Objects.requireNonNull(messageOutput, "Message output can't be NULL");
    this.resultOutput = resultOutput;
    this.messageOutput = messageOutput;
  }

  @Override
  public void print(String output) {
    resultOutput.print(output);
  }

  @Override
  public void printError(Throwable e) {
    String message = e.getMessage() != null ? e.getMessage() : e.toString();
    messageOutput.println("#" + message);
  }

  @Override
  public void printMessage(String message) {
    messageOutput.println(message);
  }
}
