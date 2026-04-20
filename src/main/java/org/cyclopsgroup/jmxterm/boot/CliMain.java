package org.cyclopsgroup.jmxterm.boot;

import java.io.File;
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

import org.cyclopsgroup.jmxterm.SyntaxUtils;
import org.cyclopsgroup.jmxterm.cc.CommandCenter;
import org.cyclopsgroup.jmxterm.cc.ConsoleCompletor;
import org.cyclopsgroup.jmxterm.io.CommandInput;
import org.cyclopsgroup.jmxterm.io.CommandOutput;
import org.cyclopsgroup.jmxterm.io.FileCommandInput;
import org.cyclopsgroup.jmxterm.io.FileCommandOutput;
import org.cyclopsgroup.jmxterm.io.InputStreamCommandInput;
import org.cyclopsgroup.jmxterm.io.JlineCommandInput;
import org.cyclopsgroup.jmxterm.io.PrintStreamCommandOutput;
import org.cyclopsgroup.jmxterm.io.VerboseLevel;
import org.cyclopsgroup.jmxterm.utils.AppConfig;
import org.cyclopsgroup.jmxterm.utils.XdgDirectories;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.LineReaderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine;

/**
 * Main class invoked directly from command line
 *
 * @author <a href="mailto:jiaqi.guo@gmail.com">Jiaqi Guo</a>
 */
public class CliMain {
  private static final Logger LOG = LoggerFactory.getLogger(CliMain.class);
  private static final PrintWriter STDOUT_WRITER = new PrintWriter(System.out, true);

  private static final String COMMAND_PROMPT = "$> ";

  public static void main(String[] args) throws Exception {
    System.exit(new CliMain().execute(args));
  }

  /**
   * Execute main class
   *
   * @param args Command line arguments
   * @return Exit code
   * @throws Exception Allow any exceptions
   */
  int execute(String[] args) throws Exception {
    LoggingConfigurator.configure(AppConfig.load(XdgDirectories.INSTANCE), XdgDirectories.INSTANCE);
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

    VerboseLevel verboseLevel;
    if (options.getVerboseLevel() != null) {
      verboseLevel = VerboseLevel.valueOf(options.getVerboseLevel().toUpperCase());
    } else {
      verboseLevel = null;
    }

    CommandOutput output;
    if (CliMainOptions.STDOUT.equals(options.getOutput())) {
      output = new PrintStreamCommandOutput(System.out, System.err);
    } else {
      File outputFile = new File(options.getOutput());
      output = new FileCommandOutput(outputFile, options.isAppendToOutput());
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
          File historyFile = historyPath.toFile();
          consoleReader.setVariable(LineReader.HISTORY_FILE, historyFile);
          History history = consoleReader.getHistory();
          history.load();
          Runtime.getRuntime()
              .addShutdownHook(
                  new Thread(
                      () -> {
                        try {
                          history.save();
                        } catch (IOException e) {
                          LOG.warn("failed to flush command history", e);
                        }
                      }));
          input = new JlineCommandInput(consoleReader, COMMAND_PROMPT);
        }
      } else {
        File inputFile = new File(options.getInput());
        if (!inputFile.isFile()) {
          throw new FileNotFoundException("File " + inputFile + " is not a valid file");
        }
        input = new FileCommandInput(new File(options.getInput()));
      }
      try {
        CommandCenter commandCenter = new CommandCenter(output, input);
        if (input instanceof JlineCommandInput commandInput) {
          commandInput
              .getConsole()
              .setCompleter(new ConsoleCompletor(commandCenter));
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
        if (verboseLevel != null) {
          commandCenter.setVerboseLevel(verboseLevel);
        }
        if (verboseLevel != VerboseLevel.SILENT) {
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
        commandCenter.close();
        return exitCode;
      } finally {
        input.close();
      }
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
