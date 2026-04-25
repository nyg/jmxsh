package sh.jmx.jmxsh.io;

import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Implementation of CommandInput with given File
 *
 */
public class FileCommandInput extends CommandInput {
  private final LineNumberReader in;

  /**
   * Read input from a given file
   *
   * @param inputFile Given input file
   * @throws IOException Thrown when file doesn't exist or can't be read
   */
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
