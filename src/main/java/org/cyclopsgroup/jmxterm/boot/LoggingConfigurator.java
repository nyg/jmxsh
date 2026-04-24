package org.cyclopsgroup.jmxterm.boot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.cyclopsgroup.jmxterm.utils.AppConfig;
import org.cyclopsgroup.jmxterm.utils.XdgDirectories;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;

/**
 * Configures Logback at application startup. When {@code logging.file.enabled=true} in the
 * application config, a daily-rotating file appender is added to the root logger targeting the XDG
 * state directory.
 */
public final class LoggingConfigurator {

  private static final String LOG_PATTERN = "%d{ISO8601} [%thread] %-5level %logger{36} - %msg%n";
  private static final int MAX_HISTORY_DAYS = 30;
  private static final String MAX_FILE_SIZE = "10MB";
  private static final String TOTAL_SIZE_CAP = "50MB";

  private LoggingConfigurator() {}

  /**
   * Configures logging based on {@link AppConfig} and {@link XdgDirectories}. Calling this method
   * more than once is safe; a file appender is added at most once.
   */
  public static void configure(AppConfig config, XdgDirectories xdg) {
    if (!config.isLoggingFileEnabled()) {
      return;
    }
    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
    Logger root = context.getLogger(Logger.ROOT_LOGGER_NAME);

    // Detach any pre-existing FILE appender that failed to start on a previous call.
    Appender<ILoggingEvent> existing = root.getAppender("FILE");
    if (existing != null) {
      if (existing.isStarted()) {
        return;
      }
      root.detachAppender(existing);
      existing.stop();
    }

    Path logFile = xdg.getLogFile();
    try {
      Files.createDirectories(logFile.getParent());
    } catch (IOException e) {
      System.err.println(
          "jmxsh: cannot create log directory " + logFile.getParent() + ": " + e.getMessage());
      return;
    }

    PatternLayoutEncoder encoder = new PatternLayoutEncoder();
    encoder.setContext(context);
    encoder.setPattern(LOG_PATTERN);
    encoder.start();

    RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
    appender.setContext(context);
    appender.setName("FILE");
    appender.setFile(logFile.toString());
    appender.setAppend(true);
    appender.setEncoder(encoder);

    SizeAndTimeBasedRollingPolicy<ILoggingEvent> policy = new SizeAndTimeBasedRollingPolicy<>();
    policy.setContext(context);
    policy.setParent(appender);
    policy.setFileNamePattern(
        logFile.getParent().resolve("jmxsh.%d{yyyy-MM-dd}.%i.log").toString());
    policy.setMaxFileSize(FileSize.valueOf(MAX_FILE_SIZE));
    policy.setMaxHistory(MAX_HISTORY_DAYS);
    policy.setTotalSizeCap(FileSize.valueOf(TOTAL_SIZE_CAP));
    policy.start();

    if (!policy.isStarted()) {
      encoder.stop();
      System.err.println("jmxsh: failed to configure log rotation — file logging is disabled");
      return;
    }

    appender.setRollingPolicy(policy);
    appender.start();

    if (!appender.isStarted()) {
      policy.stop();
      encoder.stop();
      System.err.println("jmxsh: failed to open log file " + logFile + " — file logging is disabled");
      return;
    }

    root.addAppender(appender);
  }
}
