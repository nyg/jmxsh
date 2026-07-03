package sh.jmx.jmxsh.io;

import java.util.function.Supplier;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Command output decorator that filters messages based on a live {@link OutputMode} supplier.
 * Using a supplier instead of a captured value ensures that mode changes made after construction
 * are immediately reflected.
 */
@Slf4j
public class VerboseCommandOutput implements CommandOutput {

  private final CommandOutput output;
  private final Supplier<OutputMode> modeSupplier;

  public VerboseCommandOutput(@NonNull CommandOutput output, @NonNull Supplier<OutputMode> modeSupplier) {
    this.output = output;
    this.modeSupplier = modeSupplier;
  }

  @Override
  public void close() {
    output.close();
  }

  @Override
  public void print(String value) {
    output.print(value);
  }

  @Override
  public void printError(Throwable e) {
    log.error("command execution error: {}", e.getMessage(), e);
    if (modeSupplier.get() != OutputMode.SILENT) {
      String message = e.getMessage() != null ? e.getMessage() : e.toString();
      output.printMessage(message);
    }
  }

  @Override
  public void printMessage(String message) {
    if (modeSupplier.get() != OutputMode.SILENT) {
      output.printMessage(message);
    }
  }
}
