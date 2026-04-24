package sh.jmx.jmxsh.io;

import java.io.IOException;
import java.io.Serial;

/**
 * Unchecked version of IOException
 *
 */
public class RuntimeIOException extends RuntimeException {
  @Serial
  private static final long serialVersionUID = -2304094504586109315L;

  /**
   * @param message Error message
   * @param e Original IOException
   */
  public RuntimeIOException(String message, IOException e) {
    super(message, e);
  }
}
