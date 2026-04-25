package sh.jmx.jmxsh.io;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import java.util.Objects;

/**
 * Output with a file
 *
 */
public class FileCommandOutput extends CommandOutput {
  private final PrintWriter fileWriter;

  private final WriterCommandOutput output;

  /**
   * @param file where the result is written to.
   * @param appendToOutput whether to write to output.
   * @throws IOException allows IO error.
   */
  public FileCommandOutput(Path file, boolean appendToOutput) throws IOException {
    Objects.requireNonNull(file, "File can't be NULL");
    Path af = file.toAbsolutePath();
    Files.createDirectories(af.getParent());

    var openOptions = appendToOutput
        ? new StandardOpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND}
        : new StandardOpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};
    fileWriter = new PrintWriter(
        Files.newBufferedWriter(af, StandardCharsets.UTF_8, openOptions));
    output = new WriterCommandOutput(fileWriter, new PrintWriter(System.err, true));
  }

  @Override
  public void close() {
    fileWriter.flush();
    fileWriter.close();
  }

  @Override
  public void print(String value) {
    output.print(value);
  }

  @Override
  public void printError(Throwable e) {
    output.printError(e);
  }

  @Override
  public void printMessage(String message) {
    output.printMessage(message);
  }
}
