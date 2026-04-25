package sh.jmx.jmxsh.io;

import java.io.IOException;
import java.io.Serial;

public class RuntimeIOException extends RuntimeException {
  @Serial
  private static final long serialVersionUID = -2304094504586109315L;

  public RuntimeIOException(String message, IOException e) {
    super(message, e);
  }
}
