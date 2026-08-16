package sh.jmx.jmxsh.io;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import lombok.NonNull;

public class FileCommandOutput implements CommandOutput {

  private final PrintWriter fileWriter;
  private final WriterCommandOutput output;

  @SuppressWarnings("java:S106")
  public FileCommandOutput(@NonNull Path file, boolean appendToOutput) throws IOException {
    Path af = file.toAbsolutePath();
    Files.createDirectories(af.getParent());

    StandardOpenOption[] openOptions = appendToOutput
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
