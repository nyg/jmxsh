package sh.jmx.jmxsh.boot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.rmi.ssl.SslRMIClientSocketFactory;

import sh.jmx.jmxsh.Session;
import sh.jmx.jmxsh.JmxUrl;
import sh.jmx.jmxsh.cc.CommandCenter;
import sh.jmx.jmxsh.cc.ConsoleCompleter;
import sh.jmx.jmxsh.io.CommandInput;
import sh.jmx.jmxsh.io.CommandOutput;
import sh.jmx.jmxsh.io.FileCommandInput;
import sh.jmx.jmxsh.io.FileCommandOutput;
import sh.jmx.jmxsh.io.InputStreamCommandInput;
import sh.jmx.jmxsh.io.JlineCommandInput;
import sh.jmx.jmxsh.io.PrintStreamCommandOutput;
import sh.jmx.jmxsh.io.OutputMode;
import sh.jmx.jmxsh.utils.AppConfig;
import sh.jmx.jmxsh.utils.PromptTemplate;
import sh.jmx.jmxsh.utils.XdgDirectories;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import lombok.extern.slf4j.Slf4j;

import picocli.CommandLine;

/**
 * Main class invoked directly from command line
 *
 */
@Slf4j
public class CliMain {
  private static final PrintWriter STDOUT_WRITER = new PrintWriter(System.out, true);

  static void main(String[] args) {
    try {
      System.exit(new CliMain().execute(args));
    } catch (Exception e) {
      String message = e.getMessage() != null ? e.getMessage() : e.toString();
      System.err.println(message);
      log.error("Fatal error", e);
      System.exit(1);
    }
  }

  /**
   * Execute main class
   *
   * @param args Command line arguments
   * @return Exit code
   * @throws Exception Allow any exceptions
   */
  int execute(String[] args) throws Exception {
    AppConfig appConfig = AppConfig.load(XdgDirectories.INSTANCE);
    AppConfig.createDefaultIfMissing(XdgDirectories.INSTANCE.getConfigFile());
    LoggingConfigurator.configure(appConfig, XdgDirectories.INSTANCE);
    System.setProperty("jmxsh.config.file", XdgDirectories.INSTANCE.getConfigFile().toString());
    CliMainOptions options = new CliMainOptions();
    CommandLine cl = new CommandLine(options);
    try {
      cl.parseArgs(args);
    } catch (CommandLine.ParameterException e) {
      STDOUT_WRITER.println(e.getMessage());
      cl.usage(STDOUT_WRITER);
      return 1;
    }
    if (cl.isUsageHelpRequested()) {
      cl.usage(STDOUT_WRITER);
      return 0;
    }
    if (cl.isVersionHelpRequested()) {
      cl.printVersionHelp(STDOUT_WRITER);
      return 0;
    }

    OutputMode outputMode = options.isQuiet() ? OutputMode.SILENT : OutputMode.BRIEF;

    CommandOutput output;
    if (CliMainOptions.STDOUT.equals(options.getOutput())) {
      output = new PrintStreamCommandOutput(System.out, System.err);
    } else {
      output = new FileCommandOutput(Path.of(options.getOutput()), options.isAppendToOutput());
    }
    try (output) {
      Session[] sessionRef = {null};
      CommandInput input = createInput(options, appConfig, sessionRef);
      try {
        CommandCenter commandCenter = new CommandCenter(output, input);
        try {
          if (input instanceof JlineCommandInput commandInput) {
            sessionRef[0] = commandCenter.getSession();
            commandInput
                .getConsole()
                .setCompleter(new ConsoleCompleter(commandCenter));
          }
          connectIfRequested(commandCenter, options, input);
          commandCenter.setOutputMode(outputMode);
          return runCommandLoop(input, output, commandCenter, options);
        } finally {
          commandCenter.close();
        }
      } finally {
        input.close();
      }
    } catch (Exception e) {
      log.error("Fatal startup error", e);
      output.printError(e);
      return 1;
    }
  }

  /**
   * Builds the {@link CommandInput} for the session: a file reader, a plain stdin reader in
   * non-interactive mode, or a JLine console with history and prompt rendering otherwise.
   */
  static CommandInput createInput(CliMainOptions options, AppConfig appConfig, Session[] sessionRef)
      throws IOException {
    if (!CliMainOptions.STDIN.equals(options.getInput())) {
      Path inputPath = Path.of(options.getInput());
      if (!Files.isRegularFile(inputPath)) {
        throw new FileNotFoundException("File " + inputPath + " is not a valid file");
      }
      return new FileCommandInput(inputPath);
    }
    if (options.isNonInteractive()) {
      return new InputStreamCommandInput(System.in);
    }
    return createInteractiveInput(appConfig, sessionRef);
  }

  private static CommandInput createInteractiveInput(AppConfig appConfig, Session[] sessionRef)
      throws IOException {
    DeduplicatingHistory history = new DeduplicatingHistory();
    Terminal terminal = TerminalBuilder.builder().graphemeCluster(false).build();
    LineReaderImpl consoleReader = (LineReaderImpl) LineReaderBuilder.builder().terminal(terminal).history(history).build();
    Path historyPath = XdgDirectories.INSTANCE.getHistoryFile();
    migrateHistory(XdgDirectories.INSTANCE.getLegacyHistoryFile(), historyPath);
    Files.createDirectories(historyPath.getParent());
    consoleReader.setVariable(LineReader.HISTORY_FILE, historyPath);
    history.attach(consoleReader);
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  try {
                    history.save();
                  } catch (IOException e) {
                    log.warn("failed to flush command history", e);
                  }
                }));
    String promptTemplate = appConfig.getPrompt();
    return new JlineCommandInput(consoleReader,
        () -> PromptTemplate.resolve(promptTemplate, sessionRef[0]));
  }

  /**
   * Opens the JMX connection requested via {@code --url}, assembling the credential and secure-RMI
   * environment. Does nothing when no URL was supplied.
   */
  static void connectIfRequested(CommandCenter commandCenter, CliMainOptions options, CommandInput input)
      throws IOException {
    if (options.getUrl() == null) {
      return;
    }
    Map<String, Object> env = new HashMap<>();
    if (options.getUser() != null) {
      String password = options.getPassword();
      if (password == null) {
        password = input.readMaskedString("Authentication password: ");
      }
      env.put(JMXConnector.CREDENTIALS, new String[] {options.getUser(), password});
    }
    if (options.isSecureRmiRegistry()) {
      // Required to prevent "java.rmi.ConnectIOException: non-JRMP server at remote endpoint" error
      env.put("com.sun.jndi.rmi.factory.socket", new SslRMIClientSocketFactory());
    }
    String target = commandCenter.getSession().getAliasStore().resolve(options.getUrl());
    commandCenter.connect(
        JmxUrl.parse(target).toServiceUrl(commandCenter.getProcessManager()),
        env.isEmpty() ? null : env);
  }

  /**
   * Reads and executes commands until the input is exhausted, the user quits or a command fails
   * with {@code --exitonfailure} enabled. In an interactive session the same "Bye." message is
   * printed whether the user leaves with Ctrl+C, Ctrl+D or the quit command.
   */
  static int runCommandLoop(
      CommandInput input, CommandOutput output, CommandCenter commandCenter, CliMainOptions options)
      throws IOException {
    boolean interactive = !options.isQuiet() && !options.isNonInteractive();
    if (interactive) {
      output.printMessage("Welcome to jmx.sh, type \"help\" for available commands.");
    }
    int exitCode = 0;
    int lineNumber = 0;
    boolean running = true;
    while (running) {
      String line = readNextLine(input, output, interactive);
      if (line == null) {
        running = false;
      } else {
        lineNumber++;
        if (!commandCenter.execute(line) && options.isExitOnFailure()) {
          exitCode = -lineNumber;
          running = false;
        } else if (commandCenter.isClosed()) {
          running = false;
        }
      }
    }
    return exitCode;
  }

  /**
   * Reads the next command line, returning {@code null} when the input is exhausted or the user
   * interrupts the session (Ctrl+C / Ctrl+D). The "Bye." farewell is printed on interruption of an
   * interactive session.
   */
  private static String readNextLine(CommandInput input, CommandOutput output, boolean interactive)
      throws IOException {
    try {
      return input.readLine();
    } catch (UserInterruptException | EndOfFileException _) {
      if (interactive) {
        output.printMessage("Bye.");
      }
      return null;
    }
  }

  /**
   * Copies the legacy history file ({@code ~/.jmxterm_history}) to the XDG location if the legacy
   * file exists and the target does not.
   */
  static void migrateHistory(Path legacyPath, Path xdgPath) throws IOException {
    if (Files.isRegularFile(legacyPath) && !Files.exists(xdgPath)) {
      Files.createDirectories(xdgPath.getParent());
      Files.copy(legacyPath, xdgPath, StandardCopyOption.COPY_ATTRIBUTES);
    }
  }
}
