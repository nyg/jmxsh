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

@Slf4j
public class CommandCenter {
  private static final DefaultParser PARSER = new DefaultParser().eofOnUnclosedQuote(true);

  private static final String COMMAND_DELIMITER = "&&";
  static final String ESCAPE_CHAR_REGEX = "(?<!\\\\)#";

  final CommandFactory commandFactory;

  private final Lock lock = new ReentrantLock();

  private final JavaProcessManager processManager;

  final Session session;

  public CommandCenter(CommandOutput output, CommandInput input) {
    this(output, input, new PredefinedCommandFactory());
  }

  /** This constructor is for testing purpose only. */
  public CommandCenter(CommandOutput output, CommandInput input, CommandFactory commandFactory) {
    Objects.requireNonNull(output, "Output can't be NULL");
    Objects.requireNonNull(commandFactory, "Command factory can't be NULL");
    this.processManager = new JavaProcessManager();
    this.session = new Session(output, input, processManager);
    this.commandFactory = commandFactory;
  }

  public void close() {
    session.close();
  }

  public void connect(JMXServiceURL url, Map<String, Object> env) throws IOException {
    Objects.requireNonNull(url, "URL can't be NULL");
    session.connect(url, env);
  }

  private void doExecute(String command) throws JMException {
    command = (command == null || command.isBlank()) ? null : command.trim();
    if (command == null) {
      return;
    }
    if (command.startsWith("#")) {
      return;
    }
    // Note: this allows people to set properties to values with # (e.g.: set AttributeA
    // /a/\\#something)
    command =
        command.split(ESCAPE_CHAR_REGEX)[0] // take out all commented out sections
            .replace("\\#", "#"); // fix escaped to non-escaped comment charaters
    if (command.contains(COMMAND_DELIMITER)) {
      String[] commands = command.split(COMMAND_DELIMITER);
      for (String c : commands) {
        execute(c);
      }
      return;
    }

    final List<String> args;
    try {
      args = new ArrayList<>(PARSER.parse(command, command.length(), ParseContext.ACCEPT_LINE).words());
    } catch (SyntaxError e) {
      throw new IllegalArgumentException("Malformed command (check quotes): " + e.getMessage(), e);
    }
    String commandName = args.removeFirst();
    String[] commandArgs = args.toArray(String[]::new);
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
    if (cl.isUsageHelpRequested()) {
      StringWriter sw = new StringWriter();
      cl.usage(new PrintWriter(sw, true));
      session.getOutput().printMessage(sw.toString());
      return;
    }
    cmd.setSession(session);
    lock.lock();
    try {
      cmd.execute();
    } finally {
      lock.unlock();
    }
  }

  public boolean execute(String command) {
    try {
      doExecute(command);
      return true;
    } catch (JMException | RuntimeException e) {
      session.getOutput().printError(e);
      return false;
    }
  }

  public Set<String> getCommandNames() {
    return commandFactory.getCommandNames();
  }

  public Command createCommand(String name) {
    return commandFactory.createCommand(name);
  }

  public final JavaProcessManager getProcessManager() {
    return processManager;
  }

  public boolean isClosed() {
    return session.isClosed();
  }

  public void setOutputMode(OutputMode outputMode) {
    session.setOutputMode(outputMode);
  }
}
