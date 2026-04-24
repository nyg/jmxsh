package org.cyclopsgroup.jmxterm.boot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.Path;

import org.cyclopsgroup.jmxterm.utils.AppConfig;
import org.cyclopsgroup.jmxterm.utils.XdgDirectories;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

class LoggingConfiguratorTest {

  @TempDir
  Path tempDir;

  private LoggerContext context;
  private Logger root;

  @BeforeEach
  void setUp() {
    context = (LoggerContext) LoggerFactory.getILoggerFactory();
    root = context.getLogger(Logger.ROOT_LOGGER_NAME);
    // Remove any FILE appender left over from a prior test run.
    root.detachAppender("FILE");
  }

  @AfterEach
  void tearDown() {
    root.detachAppender("FILE");
  }

  @Test
  void noFileAppenderAddedWhenDisabled() {
    AppConfig config = mock(AppConfig.class);
    when(config.isLoggingFileEnabled()).thenReturn(false);
    XdgDirectories xdg = mock(XdgDirectories.class);

    LoggingConfigurator.configure(config, xdg);

    assertThat(root.getAppender("FILE")).isNull();
  }

  @Test
  void fileAppenderAddedWhenEnabled() {
    AppConfig config = mock(AppConfig.class);
    when(config.isLoggingFileEnabled()).thenReturn(true);
    XdgDirectories xdg = mock(XdgDirectories.class);
    when(xdg.getLogFile()).thenReturn(tempDir.resolve("logs/jmxsh.log"));

    LoggingConfigurator.configure(config, xdg);

    assertThat(root.getAppender("FILE")).isNotNull();
  }

  @Test
  void fileAppenderNotAddedTwiceWhenCalledMultipleTimes() {
    AppConfig config = mock(AppConfig.class);
    when(config.isLoggingFileEnabled()).thenReturn(true);
    XdgDirectories xdg = mock(XdgDirectories.class);
    when(xdg.getLogFile()).thenReturn(tempDir.resolve("logs/jmxsh.log"));

    LoggingConfigurator.configure(config, xdg);
    LoggingConfigurator.configure(config, xdg);

    // iteratorForAppenders() returns a single element; we verify no duplicate by
    // checking that exactly one appender named FILE exists.
    var appenders = root.iteratorForAppenders();
    int count = 0;
    while (appenders.hasNext()) {
      if ("FILE".equals(appenders.next().getName())) {
        count++;
      }
    }
    assertThat(count).isEqualTo(1);
  }
}
