package org.cyclopsgroup.jmxterm.io;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command output implementation where detail message can be turned on and off dynamically
 *
 * @author <a href="mailto:jiaqi.guo@gmail.com">Jiaqi Guo</a>
 */
public class VerboseCommandOutput extends CommandOutput {
  private static final Logger LOG = LoggerFactory.getLogger(VerboseCommandOutput.class);

  private final VerboseCommandOutputConfig config;

  private final CommandOutput output;

  /**
   * @param output Proxy'ed output
   * @param config Dynamic config
   */
  public VerboseCommandOutput(CommandOutput output, VerboseCommandOutputConfig config) {
    Objects.requireNonNull(output, "The proxy'ed output can't be NULL");
    Objects.requireNonNull(config, "Config can't be NULL");
    this.output = output;
    this.config = config;
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
    LOG.error("command execution error: {}", e.getMessage(), e);
    switch (config.getVerboseLevel()) {
      case VERBOSE:
        output.printError(e);
        break;
      case SILENT:
        break;
      case BRIEF:
      default:
        output.printMessage("#" + e.getClass().getName() + ": " + e.getMessage());
        break;
    }
  }

  @Override
  public void printMessage(String message) {
    if (config.getVerboseLevel() != VerboseLevel.SILENT) {
      output.printMessage("#" + message);
    }
  }
}
