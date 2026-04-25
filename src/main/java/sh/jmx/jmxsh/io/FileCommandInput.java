package sh.jmx.jmxsh.io;

import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class FileCommandInput implements CommandInput {
  private final LineNumberReader in;

  public FileCommandInput(Path inputFile) throws IOException {
    Objects.requireNonNull(inputFile, "Input can't be NULL");
    this.in = new LineNumberReader(Files.newBufferedReader(inputFile, StandardCharsets.UTF_8));
  }

  @Override
  public void close() throws IOException {
    in.close();
  }

  @Override
  public String readLine() throws IOException {
    return in.readLine();
  }

  @Override
  public String readMaskedString(String prompt) throws IOException {
    throw new UnsupportedOperationException("Reading password from a file is not supported");
  }
}
