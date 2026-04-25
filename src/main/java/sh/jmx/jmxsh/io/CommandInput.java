package sh.jmx.jmxsh.io;

import java.io.Closeable;
import java.io.IOException;

/**
 * Interface that provides command line input line by line
 *
 */
public interface CommandInput extends Closeable {
  @Override
  default void close() throws IOException {}

  /**
   * Reads a single line from input.
   *
   * @return The line it reads.
   * @throws IOException allows any communication error.
   */
  String readLine() throws IOException;

  /**
   * Reads input without echoing back keyboard input.
   *
   * @param prompt The full or partial input that user types.
   * @return The string it reads.
   * @throws IOException allows any communication error.
   */
  String readMaskedString(String prompt) throws IOException;
}
