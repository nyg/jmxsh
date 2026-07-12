package sh.jmx.jmxsh.io;

import java.io.PrintStream;

import lombok.NonNull;

public class PrintStreamCommandOutput implements CommandOutput {

  private final PrintStream messageOutput;
  private final PrintStream resultOutput;

  public PrintStreamCommandOutput(@NonNull PrintStream resultOutput, @NonNull PrintStream messageOutput) {
    this.resultOutput = resultOutput;
    this.messageOutput = messageOutput;
  }

  @Override
  public void print(String output) {
    resultOutput.print(output);
  }

  @Override
  public void printMessage(String message) {
    messageOutput.println(message);
  }
}
