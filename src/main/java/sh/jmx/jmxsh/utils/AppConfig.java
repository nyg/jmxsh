package sh.jmx.jmxsh.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import lombok.Getter;

/**
 * Loads application configuration from {@code $XDG_CONFIG_HOME/jmxsh/config.properties}. All
 * settings fall back to defaults when the file is absent or a key is missing.
 */
public final class AppConfig {

  private static final String KEY_LOGGING_FILE_ENABLED = "logging.file.enabled";
  private static final boolean DEFAULT_LOGGING_FILE_ENABLED = false;

  @Getter
  private final boolean loggingFileEnabled;

  private AppConfig(boolean loggingFileEnabled) {
    this.loggingFileEnabled = loggingFileEnabled;
  }

  /**
   * Loads configuration from the XDG config file for the given directories. If the file does not
   * exist, returns a config with all defaults.
   */
  public static AppConfig load(XdgDirectories xdg) {
    return load(xdg.getConfigFile());
  }

  /** Loads configuration from the given path. Visible for testing. */
  static AppConfig load(Path configFile) {
    Properties props = new Properties();
    if (Files.isRegularFile(configFile)) {
      try (InputStream in = Files.newInputStream(configFile)) {
        props.load(in);
      } catch (IOException e) {
        // Silently fall back to defaults if the file cannot be read.
      }
    }
    boolean loggingFileEnabled =
        parseBoolean(props.getProperty(KEY_LOGGING_FILE_ENABLED), DEFAULT_LOGGING_FILE_ENABLED);
    return new AppConfig(loggingFileEnabled);
  }

  private static boolean parseBoolean(String value, boolean defaultValue) {
    if (value == null || value.isBlank()) {
      return defaultValue;
    }
    String trimmed = value.trim();
    if (trimmed.equalsIgnoreCase("true")) {
      return true;
    }
    if (trimmed.equalsIgnoreCase("false")) {
      return false;
    }
    return defaultValue;
  }

  private static final String DEFAULT_CONFIG_CONTENT =
      """
      # jmxsh configuration file
      #
      # Enable logging to a rotating file in the XDG state directory.
      # Log files are stored in $XDG_STATE_HOME/jmxsh/logs/
      # (default: ~/.local/state/jmxsh/logs/)
      # logging.file.enabled=false
      """;

  /**
   * Creates the config file with default (commented-out) content if it does not already exist.
   * Parent directories are created as needed. Silently ignores failures.
   */
  public static void createDefaultIfMissing(Path configFile) {
    if (Files.exists(configFile)) {
      return;
    }
    try {
      Files.createDirectories(configFile.getParent());
      Files.writeString(configFile, DEFAULT_CONFIG_CONTENT, StandardCharsets.UTF_8);
    } catch (IOException e) {
      // Non-fatal: if we cannot write the config, continue without it.
    }
  }
}
