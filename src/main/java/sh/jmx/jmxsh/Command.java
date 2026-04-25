package sh.jmx.jmxsh;

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

  protected List<String> doSuggestArgument() throws IOException, JMException {
    return null;
  }

  protected List<String> doSuggestOption(String optionName) throws IOException, JMException {
    return null;
  }

  public abstract void execute() throws IOException, JMException;

  public final Session getSession() {
    return session;
  }

  public final boolean isHelp() {
    return help;
  }

  @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display usage")
  public final void setHelp(boolean help) {
    this.help = help;
  }

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
