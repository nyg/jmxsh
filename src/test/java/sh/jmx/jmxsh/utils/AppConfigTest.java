package sh.jmx.jmxsh.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AppConfigTest {

  @Test
  void defaultsWhenFileDoesNotExist(@TempDir Path dir) {
    AppConfig config = AppConfig.load(dir.resolve("nonexistent.properties"));
    assertThat(config.isLoggingFileEnabled()).isFalse();
  }

  @Test
  void loggingFileEnabledWhenSetToTrue(@TempDir Path dir) throws IOException {
    Path configFile = dir.resolve("config.properties");
    Files.writeString(configFile, "logging.file.enabled=true\n");
    AppConfig config = AppConfig.load(configFile);
    assertThat(config.isLoggingFileEnabled()).isTrue();
  }

  @Test
  void loggingFileDisabledWhenSetToFalse(@TempDir Path dir) throws IOException {
    Path configFile = dir.resolve("config.properties");
    Files.writeString(configFile, "logging.file.enabled=false\n");
    AppConfig config = AppConfig.load(configFile);
    assertThat(config.isLoggingFileEnabled()).isFalse();
  }

  @Test
  void malformedValueFallsBackToDefault(@TempDir Path dir) throws IOException {
    Path configFile = dir.resolve("config.properties");
    Files.writeString(configFile, "logging.file.enabled=yes\n");
    AppConfig config = AppConfig.load(configFile);
    assertThat(config.isLoggingFileEnabled()).isFalse();
  }

  @Test
  void missingKeyFallsBackToDefault(@TempDir Path dir) throws IOException {
    Path configFile = dir.resolve("config.properties");
    Files.writeString(configFile, "some.other.key=value\n");
    AppConfig config = AppConfig.load(configFile);
    assertThat(config.isLoggingFileEnabled()).isFalse();
  }
}
