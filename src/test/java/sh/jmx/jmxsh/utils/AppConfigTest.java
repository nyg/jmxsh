package sh.jmx.jmxsh.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class AppConfigTest {

  @Test
  void defaultsWhenFileDoesNotExist(@TempDir Path dir) {
    AppConfig config = AppConfig.load(dir.resolve("nonexistent.properties"));
    assertThat(config.isLoggingFileEnabled()).isFalse();
    assertThat(config.getPrompt()).isEqualTo(AppConfig.DEFAULT_PROMPT);
  }

  static Stream<Arguments> loggingFileEnabledCases() {
    return Stream.of(
        Arguments.of("logging.file.enabled=true\n", true),
        Arguments.of("logging.file.enabled=false\n", false),
        Arguments.of("logging.file.enabled=yes\n", false),
        Arguments.of("some.other.key=value\n", false));
  }

  static Stream<Arguments> promptCases() {
    return Stream.of(
        Arguments.of("prompt=> \n", "> "),
        Arguments.of("prompt=[{server}]> \n", "[{server}]> "),
        Arguments.of("prompt=\n", ""),
        Arguments.of("some.other.key=value\n", AppConfig.DEFAULT_PROMPT));
  }

  @ParameterizedTest
  @MethodSource("promptCases")
  void promptProperty(String content, String expected, @TempDir Path dir) throws IOException {
    Path configFile = dir.resolve("config.properties");
    Files.writeString(configFile, content);
    AppConfig config = AppConfig.load(configFile);
    assertThat(config.getPrompt()).isEqualTo(expected);
  }

  @ParameterizedTest
  @MethodSource("loggingFileEnabledCases")
  void loggingFileEnabled(String content, boolean expected, @TempDir Path dir) throws IOException {
    Path configFile = dir.resolve("config.properties");
    Files.writeString(configFile, content);
    AppConfig config = AppConfig.load(configFile);
    assertThat(config.isLoggingFileEnabled()).isEqualTo(expected);
  }
}
