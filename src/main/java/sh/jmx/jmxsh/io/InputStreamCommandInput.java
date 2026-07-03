package sh.jmx.jmxsh.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;

import lombok.NonNull;

public class InputStreamCommandInput implements CommandInput {
  private final LineNumberReader reader;

  public InputStreamCommandInput(@NonNull InputStream in) {
    reader = new LineNumberReader(new InputStreamReader(in, StandardCharsets.UTF_8));
  }

  @Override
  public String readLine() throws IOException {
    return reader.readLine();
  }

  @Override
  public String readMaskedString(String prompt) throws IOException {
    throw new UnsupportedOperationException("Reading password from stream is not supported");
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }
}
