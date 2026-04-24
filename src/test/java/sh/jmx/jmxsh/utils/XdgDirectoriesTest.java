package sh.jmx.jmxsh.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class XdgDirectoriesTest {

  @Test
  void stateHomeUsesEnvVarWhenSet() {
    var dirs = new XdgDirectories(_ -> "/custom/state", "/home/testuser");
    assertThat(dirs.getStateHome()).isEqualTo(Path.of("/custom/state"));
  }

  @Test
  void stateHomeFallsBackWhenEnvVarIsNull() {
    var dirs = new XdgDirectories(_ -> null, "/home/testuser");
    assertThat(dirs.getStateHome()).isEqualTo(Path.of("/home/testuser/.local/state"));
  }

  @Test
  void stateHomeFallsBackWhenEnvVarIsBlank() {
    var dirs = new XdgDirectories(_ -> "  ", "/home/testuser");
    assertThat(dirs.getStateHome()).isEqualTo(Path.of("/home/testuser/.local/state"));
  }

  @Test
  void historyFileResolvesUnderStateHome() {
    var dirs = new XdgDirectories(_ -> "/xdg/state", "/ignored");
    assertThat(dirs.getHistoryFile()).isEqualTo(Path.of("/xdg/state/jmxsh/history"));
  }

  @Test
  void historyFileUsesDefaultStateHome() {
    var dirs = new XdgDirectories(_ -> null, "/home/testuser");
    assertThat(dirs.getHistoryFile())
        .isEqualTo(Path.of("/home/testuser/.local/state/jmxsh/history"));
  }

  @Test
  void configHomeUsesEnvVarWhenSet() {
    var dirs = new XdgDirectories(_ -> "/custom/config", "/home/testuser");
    assertThat(dirs.getConfigHome()).isEqualTo(Path.of("/custom/config"));
  }

  @Test
  void configHomeFallsBackToDefaultWhenEnvVarIsNull() {
    var dirs = new XdgDirectories(_ -> null, "/home/testuser");
    assertThat(dirs.getConfigHome()).isEqualTo(Path.of("/home/testuser/.config"));
  }

  @Test
  void configHomeFallsBackToDefaultWhenEnvVarIsBlank() {
    var dirs = new XdgDirectories(_ -> "  ", "/home/testuser");
    assertThat(dirs.getConfigHome()).isEqualTo(Path.of("/home/testuser/.config"));
  }

  @Test
  void configFileResolvesUnderConfigHome() {
    var dirs = new XdgDirectories(k -> k.equals("XDG_CONFIG_HOME") ? "/xdg/config" : null, "/ignored");
    assertThat(dirs.getConfigFile()).isEqualTo(Path.of("/xdg/config/jmxsh/config.properties"));
  }

  @Test
  void configFileUsesDefaultConfigHome() {
    var dirs = new XdgDirectories(_ -> null, "/home/testuser");
    assertThat(dirs.getConfigFile())
        .isEqualTo(Path.of("/home/testuser/.config/jmxsh/config.properties"));
  }

  @Test
  void logFileResolvesUnderStateHome() {
    var dirs = new XdgDirectories(k -> k.equals("XDG_STATE_HOME") ? "/xdg/state" : null, "/ignored");
    assertThat(dirs.getLogFile()).isEqualTo(Path.of("/xdg/state/jmxsh/logs/jmxsh.log"));
  }

  @Test
  void logFileUsesDefaultStateHome() {
    var dirs = new XdgDirectories(_ -> null, "/home/testuser");
    assertThat(dirs.getLogFile())
        .isEqualTo(Path.of("/home/testuser/.local/state/jmxsh/logs/jmxsh.log"));
  }
}
