package sh.jmx.jmxsh.io;

import java.io.IOException;

public class UnimplementedCommandInput implements CommandInput {
  @Override
  public String readLine() throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public String readMaskedString(String prompt) throws IOException {
    throw new UnsupportedOperationException();
  }
}
