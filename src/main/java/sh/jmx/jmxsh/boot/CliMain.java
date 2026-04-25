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

import sh.jmx.jmxsh.SyntaxUtils;
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
import sh.jmx.jmxsh.utils.XdgDirectories;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.LineReaderImpl;

import lombok.extern.slf4j.Slf4j;

import picocli.CommandLine;

/**
 * Main class invoked directly from command line
 *
 */
@Slf4j
public class CliMain {
  private static final PrintWriter STDOUT_WRITER = new PrintWriter(System.out, true);

  private static final String COMMAND_PROMPT = "$> ";

  public static void main(String[] args) {
    try {
      System.exit(new CliMain().execute(args));
    } catch (Exception e) {
      String message = e.getMessage() != null ? e.getMessage() : e.toString();
      System.err.println("# " + message);
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
    try {
      CommandInput input;
      if (CliMainOptions.STDIN.equals(options.getInput())) {
        if (options.isNonInteractive()) {
          input = new InputStreamCommandInput(System.in);
        } else {
          LineReaderImpl consoleReader = (LineReaderImpl) LineReaderBuilder.builder().build();
          Path historyPath = XdgDirectories.INSTANCE.getHistoryFile();
          migrateHistory(XdgDirectories.INSTANCE.getLegacyHistoryFile(), historyPath);
          Files.createDirectories(historyPath.getParent());
          consoleReader.setVariable(LineReader.HISTORY_FILE, historyPath);
          History history = consoleReader.getHistory();
          history.load();
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
          input = new JlineCommandInput(consoleReader, COMMAND_PROMPT);
        }
      } else {
        Path inputPath = Path.of(options.getInput());
        if (!Files.isRegularFile(inputPath)) {
          throw new FileNotFoundException("File " + inputPath + " is not a valid file");
        }
        input = new FileCommandInput(inputPath);
      }
      try {
        CommandCenter commandCenter = new CommandCenter(output, input);
        try {
          if (input instanceof JlineCommandInput commandInput) {
            commandInput
                .getConsole()
                .setCompleter(new ConsoleCompleter(commandCenter));
          }
          if (options.getUrl() != null) {
            Map<String, Object> env = new HashMap<>();
            if (options.getUser() != null) {
              String password = options.getPassword();
              if (password == null) {
                password = input.readMaskedString("Authentication password: ");
              }
              String[] credentials = {options.getUser(), password};
              env.put(JMXConnector.CREDENTIALS, credentials);
            }
            if (options.isSecureRmiRegistry()) {
              // Required to prevent "java.rmi.ConnectIOException: non-JRMP server at remote endpoint"
              // error
              env.put("com.sun.jndi.rmi.factory.socket", new SslRMIClientSocketFactory());
            }
            commandCenter.connect(
                SyntaxUtils.getUrl(options.getUrl(), commandCenter.getProcessManager()),
                env.isEmpty() ? null : env);
          }
          commandCenter.setOutputMode(outputMode);
          if (!options.isQuiet()) {
            output.printMessage("Welcome to jmx.sh, type \"help\" for available commands.");
          }
          String line;
          int exitCode = 0;
          int lineNumber = 0;
          while ((line = input.readLine()) != null) {
            lineNumber++;
            if (!commandCenter.execute(line) && options.isExitOnFailure()) {
              exitCode = -lineNumber;
              break;
            }
            if (commandCenter.isClosed()) {
              break;
            }
          }
          return exitCode;
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
    } finally {
      output.close();
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
