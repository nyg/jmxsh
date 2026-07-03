package sh.jmx.jmxsh.io;

import java.io.IOException;
import java.util.function.Supplier;

import lombok.NonNull;
import org.jline.reader.impl.LineReaderImpl;

public class JlineCommandInput implements CommandInput {
  private final LineReaderImpl console;

  private final Supplier<String> promptSupplier;

  public JlineCommandInput(@NonNull LineReaderImpl console, @NonNull Supplier<String> promptSupplier) {
    this.console = console;
    this.promptSupplier = promptSupplier;
  }

  public final LineReaderImpl getConsole() {
    return console;
  }

  @Override
  public String readLine() throws IOException {
    return console.readLine(promptSupplier.get());
  }

  @Override
  public String readMaskedString(String prompt) throws IOException {
    return console.readLine(prompt, '*');
  }
}

