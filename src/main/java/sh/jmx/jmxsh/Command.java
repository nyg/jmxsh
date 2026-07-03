package sh.jmx.jmxsh;

import java.io.IOException;
import java.util.List;

import javax.management.JMException;

import lombok.NonNull;
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

  @SuppressWarnings("java:S1130") // throws are required API contract for subclass overrides
  protected List<String> doSuggestArgument() throws IOException, JMException {
    return List.of();
  }

  @SuppressWarnings("java:S1130") // throws are required API contract for subclass overrides
  protected List<String> doSuggestOption(String optionName) throws IOException, JMException {
    return List.of();
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

  public final void setSession(@NonNull Session session) {
    this.session = session;
  }

  public final List<String> suggestArgument(String partialArg) {
    if (partialArg != null) {
      return List.of();
    }
    try {
      return doSuggestArgument();
    } catch (IOException | JMException e) {
      if (log.isDebugEnabled()) {
        log.debug("Couldn't suggest argument", e);
      }
      return List.of();
    }
  }

  public final List<String> suggestOption(String name, String partialValue) {
    if (partialValue != null) {
      return List.of();
    }
    try {
      return doSuggestOption(name);
    } catch (IOException | JMException e) {
      if (log.isDebugEnabled()) {
        log.debug("Couldn't suggest option", e);
      }
      return List.of();
    }
  }
}
