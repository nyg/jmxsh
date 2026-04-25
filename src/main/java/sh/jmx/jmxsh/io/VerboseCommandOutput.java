package sh.jmx.jmxsh.io;

import java.util.Objects;
import java.util.function.Supplier;

import lombok.extern.slf4j.Slf4j;

/**
 * Command output decorator that filters messages based on a live {@link OutputMode} supplier.
 * Using a supplier instead of a captured value ensures that mode changes made after construction
 * are immediately reflected.
 */
@Slf4j
public class VerboseCommandOutput extends CommandOutput {

  private final CommandOutput output;
  private final Supplier<OutputMode> modeSupplier;

  public VerboseCommandOutput(CommandOutput output, Supplier<OutputMode> modeSupplier) {
    this.output = Objects.requireNonNull(output, "output can't be NULL");
    this.modeSupplier = Objects.requireNonNull(modeSupplier, "modeSupplier can't be NULL");
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
      output.printMessage("#" + e.getMessage());
    }
  }

  @Override
  public void printMessage(String message) {
    if (modeSupplier.get() != OutputMode.SILENT) {
      output.printMessage("#" + message);
    }
  }
}
