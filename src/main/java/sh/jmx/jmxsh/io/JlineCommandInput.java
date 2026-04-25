package sh.jmx.jmxsh.io;

import java.io.IOException;
import java.util.Objects;
import org.jline.reader.impl.LineReaderImpl;

public class JlineCommandInput implements CommandInput {
  private final LineReaderImpl console;

  private final String prompt;

  public JlineCommandInput(LineReaderImpl console, String prompt) {
    Objects.requireNonNull(console, "Jline console reader can't be NULL");
    this.console = console;
    this.prompt = prompt == null ? "" : prompt.trim();
  }

  public final LineReaderImpl getConsole() {
    return console;
  }

  @Override
  public String readLine() throws IOException {
    return console.readLine(prompt);
  }

  @Override
  public String readMaskedString(String prompt) throws IOException {
    return console.readLine(prompt, '*');
  }
}
