package org.cyclopsgroup.jmxterm.boot;

import java.nio.file.Path;

import org.cyclopsgroup.jmxterm.utils.AppConfig;
import org.cyclopsgroup.jmxterm.utils.XdgDirectories;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
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
    if (root.getAppender("FILE") != null) {
      return;
    }
    Path logFile = xdg.getLogFile();

    PatternLayoutEncoder encoder = new PatternLayoutEncoder();
    encoder.setContext(context);
    encoder.setPattern(LOG_PATTERN);
    encoder.start();

    SizeAndTimeBasedRollingPolicy<ILoggingEvent> policy = new SizeAndTimeBasedRollingPolicy<>();
    policy.setContext(context);
    policy.setMaxHistory(MAX_HISTORY_DAYS);
    policy.setTotalSizeCap(FileSize.valueOf(TOTAL_SIZE_CAP));

    RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
    appender.setContext(context);
    appender.setName("FILE");
    appender.setFile(logFile.toString());
    policy.setFileNamePattern(
        logFile.getParent().resolve("jmxsh.%d{yyyy-MM-dd}.%i.log").toString());
    policy.setParent(appender);
    policy.start();
    appender.setRollingPolicy(policy);
    appender.setEncoder(encoder);
    appender.start();

    root.addAppender(appender);
  }
}
