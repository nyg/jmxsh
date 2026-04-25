package sh.jmx.jmxsh.cc;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.management.JMException;
import javax.management.remote.JMXServiceURL;

import sh.jmx.jmxsh.Command;
import sh.jmx.jmxsh.CommandFactory;
import sh.jmx.jmxsh.Session;
import sh.jmx.jmxsh.io.CommandInput;
import sh.jmx.jmxsh.io.CommandOutput;
import sh.jmx.jmxsh.io.RuntimeIOException;
import sh.jmx.jmxsh.io.OutputMode;
import sh.jmx.jmxsh.attach.JavaProcessManager;

import org.jline.reader.Parser.ParseContext;
import org.jline.reader.SyntaxError;
import org.jline.reader.impl.DefaultParser;
import picocli.CommandLine;
import lombok.extern.slf4j.Slf4j;

/**
 * Facade class where commands are maintained and executed
 *
 */
@Slf4j
public class CommandCenter {
  private static final DefaultParser PARSER = new DefaultParser().eofOnUnclosedQuote(true);

  private static final String COMMAND_DELIMITER = "&&";
  static final String ESCAPE_CHAR_REGEX = "(?<!\\\\)#";

  /** Command factory that creates commands */
  final CommandFactory commandFactory;

  private final Lock lock = new ReentrantLock();

  private final JavaProcessManager processManager;

  /** A handler to session */
  final Session session;

  /**
   * Constructor with given output {@link PrintWriter}
   *
   * @param output Message output. It can't be NULL
   * @param input Command line input
   * @throws IOException Thrown for file access failure
   */
  public CommandCenter(CommandOutput output, CommandInput input) throws IOException {
    this(output, input, new PredefinedCommandFactory());
  }

  /**
   * This constructor is for testing purpose only
   *
   * @param output Output result
   * @param input Command input
   * @param commandFactory Given command factory
   */
  public CommandCenter(CommandOutput output, CommandInput input, CommandFactory commandFactory) {
    Objects.requireNonNull(output, "Output can't be NULL");
    Objects.requireNonNull(commandFactory, "Command factory can't be NULL");
    this.processManager = new JavaProcessManager();
    this.session = new Session(output, input, processManager);
    this.commandFactory = commandFactory;
  }

  /** Close session */
  public void close() {
    session.close();
  }

  /**
   * @param url MBeanServer location. It can be <code>AAA:###</code> or full JMX server URL
   * @param env Environment variables
   * @throws IOException Thrown when connection can't be established
   */
  public void connect(JMXServiceURL url, Map<String, Object> env) throws IOException {
    Objects.requireNonNull(url, "URL can't be NULL");
    session.connect(url, env);
  }

  private void doExecute(String command) throws JMException {
    command = (command == null || command.isBlank()) ? null : command.trim();
    // Ignore empty line
    if (command == null) {
      return;
    }
    // Ignore line comment
    if (command.startsWith("#")) {
      return;
    }
    // Truncate command if there's # character
    // Note: this allows people to set properties to values with # (e.g.: set AttributeA
    // /a/\\#something)
    command =
        command.split(ESCAPE_CHAR_REGEX)[0] // take out all commented out sections
            .replace("\\#", "#"); // fix escaped to non-escaped comment charaters
    // If command includes multiple segments, call them one by one using recursive call
    if (command.contains(COMMAND_DELIMITER)) {
      String[] commands = command.split(COMMAND_DELIMITER);
      for (String c : commands) {
        execute(c);
      }
      return;
    }

    // Take the first argument out since it's command name
    final List<String> args;
    try {
      args = new ArrayList<>(PARSER.parse(command, command.length(), ParseContext.ACCEPT_LINE).words());
    } catch (SyntaxError e) {
      throw new IllegalArgumentException("Malformed command (check quotes): " + e.getMessage(), e);
    }
    String commandName = args.removeFirst();
    // Leave the rest of arguments for command
    String[] commandArgs = args.toArray(String[]::new);
    // Call command with parsed command name and arguments
    try {
      doExecute(commandName, commandArgs);
    } catch (IOException e) {
      throw new RuntimeIOException("Runtime IO exception: " + e.getMessage(), e);
    }
  }

  private void doExecute(String commandName, String[] commandArgs)
      throws JMException, IOException {
    log.debug("executing command: {}", commandName);
    Command cmd = commandFactory.createCommand(commandName);
    if (cmd instanceof HelpCommand command) {
      command.setCommandCenter(this);
    }
    CommandLine cl = new CommandLine(cmd);
    cl.setUnmatchedOptionsArePositionalParams(true);    try {
      cl.parseArgs(commandArgs);
    } catch (CommandLine.ParameterException e) {
      session.getOutput().printMessage(e.getMessage());
      StringWriter sw = new StringWriter();
      cl.usage(new PrintWriter(sw, true));
      session.getOutput().printMessage(sw.toString());
      return;
    }
    // Print out usage if help option is specified
    if (cl.isUsageHelpRequested()) {
      StringWriter sw = new StringWriter();
      cl.usage(new PrintWriter(sw, true));
      session.getOutput().printMessage(sw.toString());
      return;
    }
    cmd.setSession(session);
    // Make sure concurrency and run command
    lock.lock();
    try {
      cmd.execute();
    } finally {
      lock.unlock();
    }
  }

  /**
   * Execute a command. Command can be a valid full command, a comment, command followed by comment
   * or empty
   *
   * @param command String command to execute
   * @return True if successful
   */
  public boolean execute(String command) {
    try {
      doExecute(command);
      return true;
    } catch (JMException | RuntimeException e) {
      session.getOutput().printError(e);
      return false;
    }
  }

  /** @return Set of command names */
  public Set<String> getCommandNames() {
    return commandFactory.getCommandNames();
  }

  /**
   * @param name Command name
   * @return A new command instance for the given name
   */
  public Command createCommand(String name) {
    return commandFactory.createCommand(name);
  }

  /** @return Java process manager implementation */
  public final JavaProcessManager getProcessManager() {
    return processManager;
  }

  /** @return True if command center is closed */
  public boolean isClosed() {
    return session.isClosed();
  }

  /** @param outputMode New output mode */
  public void setOutputMode(OutputMode outputMode) {
    session.setOutputMode(outputMode);
  }
}
