package org.cyclopsgroup.jmxterm.io;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Command output implementation where detail message can be turned on and off dynamically
 *
 */
@Slf4j
@RequiredArgsConstructor
public class VerboseCommandOutput extends CommandOutput {

  @NonNull
  private final CommandOutput output;

  @NonNull
  private final VerboseCommandOutputConfig config;

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
    if (config.getVerboseLevel() != VerboseLevel.SILENT) {
      output.printMessage("#" + e.getMessage());
    }
  }

  @Override
  public void printMessage(String message) {
    if (config.getVerboseLevel() != VerboseLevel.SILENT) {
      output.printMessage("#" + message);
    }
  }
}
