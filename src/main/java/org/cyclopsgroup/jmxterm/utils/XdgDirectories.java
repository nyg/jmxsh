package org.cyclopsgroup.jmxterm.utils;

import java.nio.file.Path;
import java.util.function.Function;

/**
 * Resolves paths according to the
 * <a href="https://specifications.freedesktop.org/basedir-spec/latest/">XDG Base Directory
 * Specification</a>.
 */
public final class XdgDirectories {

  static final String APP_NAME = "jmxsh";
  static final String HISTORY_FILENAME = "history";
  static final String CONFIG_FILENAME = "config.properties";
  static final String LOG_FILENAME = "jmxsh.log";

  private final Function<String, String> env;
  private final String userHome;

  /** Production instance that reads real environment variables. */
  public static final XdgDirectories INSTANCE =
      new XdgDirectories(System.getenv()::get, System.getProperty("user.home"));

  /**
   * @param env function that returns the value of an environment variable (or {@code null})
   * @param userHome value of the user's home directory
   */
  public XdgDirectories(Function<String, String> env, String userHome) {
    this.env = env;
    this.userHome = userHome;
  }

  /**
   * Returns the XDG state home directory ({@code $XDG_STATE_HOME}, defaulting to
   * {@code $HOME/.local/state}).
   */
  public Path getStateHome() {
    String xdg = env.apply("XDG_STATE_HOME");
    if (xdg != null && !xdg.isBlank()) {
      return Path.of(xdg);
    }
    return Path.of(userHome, ".local", "state");
  }

  /** Returns the path where command history should be stored. */
  public Path getHistoryFile() {
    return getStateHome().resolve(APP_NAME).resolve(HISTORY_FILENAME);
  }

  /** Returns the legacy history file path ({@code ~/.jmxterm_history}). */
  public Path getLegacyHistoryFile() {
    return Path.of(userHome, ".jmxterm_history");
  }

  /**
   * Returns the XDG config home directory ({@code $XDG_CONFIG_HOME}, defaulting to
   * {@code $HOME/.config}).
   */
  public Path getConfigHome() {
    String xdg = env.apply("XDG_CONFIG_HOME");
    if (xdg != null && !xdg.isBlank()) {
      return Path.of(xdg);
    }
    return Path.of(userHome, ".config");
  }

  /** Returns the path where the application config file should be stored. */
  public Path getConfigFile() {
    return getConfigHome().resolve(APP_NAME).resolve(CONFIG_FILENAME);
  }

  /** Returns the path where the log file should be stored. */
  public Path getLogFile() {
    return getStateHome().resolve(APP_NAME).resolve("logs").resolve(LOG_FILENAME);
  }
}
