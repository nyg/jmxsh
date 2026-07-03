package sh.jmx.jmxsh.io;

import java.io.PrintStream;

import lombok.NonNull;

public class PrintStreamCommandOutput implements CommandOutput {
  private final PrintStream messageOutput;

  private final PrintStream resultOutput;

  public PrintStreamCommandOutput() {
    this(System.out);
  }

  public PrintStreamCommandOutput(PrintStream output) {
    this(output, System.err);
  }

  public PrintStreamCommandOutput(@NonNull PrintStream resultOutput, @NonNull PrintStream messageOutput) {
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
    messageOutput.println(message);
  }

  @Override
  public void printMessage(String message) {
    messageOutput.println(message);
  }
}
