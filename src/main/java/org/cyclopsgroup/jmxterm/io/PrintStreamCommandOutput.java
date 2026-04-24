package org.cyclopsgroup.jmxterm.io;

import java.io.PrintStream;
import java.util.Objects;

/**
 * Implementation of CommandOutput where output is written in given PrintStream objects
 *
 */
public class PrintStreamCommandOutput extends CommandOutput {
  private final PrintStream messageOutput;

  private final PrintStream resultOutput;

  /** Default constructor that uses system standard output and err output */
  public PrintStreamCommandOutput() {
    this(System.out);
  }

  /**
   * Constructor with given result output and system error as message output
   *
   * @param output Output for result
   */
  public PrintStreamCommandOutput(PrintStream output) {
    this(output, System.err);
  }

  /**
   * @param resultOutput PrintStream where result is written to
   * @param messageOutput PrintStream where message is written to
   */
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
