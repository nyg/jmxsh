package sh.jmx.jmxsh.io;

/**
 * Interface to output messages and values
 *
 */
public interface CommandOutput {
  /** Close the output. */
  default void close() {}

  /**
   * Print out value to output without line break
   *
   * @param output Value to print out
   */
  void print(String output);

  /** @param e Error to print out */
  void printError(Throwable e);

  /**
   * Print out value to output as standalone line
   *
   * @param output Value to print out
   */
  default void println(String output) {
    print(output);
    print(System.lineSeparator());
  }

  /**
   * Print message to non-standard console for human to read. New line is always appended
   *
   * @param message Message to print out.
   */
  void printMessage(String message);
}
