package sh.jmx.jmxsh.io;

/**
 * Controls how much the shell writes to the output channel.
 */
public enum OutputMode {
  /** Nothing is written except returned values */
  SILENT,
  /** Print returned values and informational messages */
  BRIEF
}
