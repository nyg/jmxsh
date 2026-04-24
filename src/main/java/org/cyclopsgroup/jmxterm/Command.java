package org.cyclopsgroup.jmxterm;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import javax.management.JMException;

import lombok.extern.slf4j.Slf4j;

import picocli.CommandLine.Option;

/**
 * Base class of all commands. Command is executed in single thread. Extending classes don't need to
 * worry about concurrency. Command is transient, every command in console creates a new instance of
 * Command object which is disposed after execution finishes.
 *
 */
@Slf4j
public abstract class Command implements Completable {
  private boolean help;

  private Session session;

  /**
   * Provide a list of possible arguments for auto completion. This method returns list of
   * arguments(not option) and is called when user presses tab key.
   *
   * @return List of possible arguments used by auto completion or NULL
   * @throws IOException IO errors
   * @throws JMException JMX problemo
   */
  protected List<String> doSuggestArgument() throws IOException, JMException {
    return null;
  }

  /**
   * Provide a list of possible option values for auto completion
   *
   * @param optionName Name of option
   * @return List of possible arguments used by auto completion or NULL
   * @throws IOException Network communication errors
   * @throws JMException JMX errors
   */
  protected List<String> doSuggestOption(String optionName) throws IOException, JMException {
    return null;
  }

  /**
   * Execute command
   *
   * @throws IOException IO errors
   * @throws JMException JMX errors
   */
  public abstract void execute() throws IOException, JMException;

  /** @return Session where command runs */
  public final Session getSession() {
    return session;
  }

  /** @return True if help option is on */
  public final boolean isHelp() {
    return help;
  }

  /** @param help True to display usage */
  @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display usage")
  public final void setHelp(boolean help) {
    this.help = help;
  }

  /** @param session Session where command runs */
  public final void setSession(Session session) {
    Objects.requireNonNull(session, "Session can't be NULL");
    this.session = session;
  }

  public final List<String> suggestArgument(String partialArg) {
    if (partialArg != null) {
      return null;
    }
    try {
      return doSuggestArgument();
    } catch (IOException | JMException e) {
      if (log.isDebugEnabled()) {
        log.debug("Couldn't suggest argument", e);
      }
      return null;
    }
  }

  public final List<String> suggestOption(String name, String partialValue) {
    if (partialValue != null) {
      return null;
    }
    try {
      return doSuggestOption(name);
    } catch (IOException | JMException e) {
      if (log.isDebugEnabled()) {
        log.debug("Couldn't suggest option", e);
      }
      return null;
    }
  }
}
