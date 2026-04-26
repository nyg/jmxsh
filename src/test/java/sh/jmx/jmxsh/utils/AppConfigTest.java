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
  }

  static Stream<Arguments> loggingFileEnabledCases() {
    return Stream.of(
        Arguments.of("logging.file.enabled=true\n", true),
        Arguments.of("logging.file.enabled=false\n", false),
        Arguments.of("logging.file.enabled=yes\n", false),
        Arguments.of("some.other.key=value\n", false));
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
