package sh.jmx.jmxsh.boot;

import java.io.File;
import java.util.Objects;

import lombok.Getter;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Options for main class
 *
 */
@Command(
    name = "jmxterm",
    description = "Main executable of JMX terminal CLI tool",
    mixinStandardHelpOptions = false,
    versionProvider = VersionProvider.class,
    footer = {
        "Without any option, this command opens an interactive command line based console. "
            + "With a given input file, commands in file will be executed and process ends after "
            + "file is processed.",
        "",
        "Config file: ${sys:jmxsh.config.file}"
    })
@Getter
public class CliMainOptions {
  /** Constant <code>stderr</code> that identifies standard error output */
  public static final String STDERR = "stderr";

  /** Constant <code>stdin</code> that identifies standard input */
  public static final String STDIN = "stdin";

  /** Constant <code>stdout</code> that identifies standard output */
  public static final String STDOUT = "stdout";

  private boolean exitOnFailure;

  private String input = STDIN;

  private boolean nonInteractive;

  private boolean appendToOutput;

  private boolean helpRequested;

  private boolean quiet;

  private boolean versionRequested;

  private String output = STDOUT;

  private String password;

  private String url;

  private String user;

  private boolean isSecureRmiRegistry;

  /** @param exitOnFailure True if terminal exits on any failure */
  @Option(
      names = {"-e", "--exitonfailure"},
      description = "With this flag, terminal exits for any Exception")
  public final void setExitOnFailure(boolean exitOnFailure) {
    this.exitOnFailure = exitOnFailure;
  }

  /** @param file Input script path or <code>stdin</code> as default value for console input */
  @Option(
      names = {"-i", "--input"},
      description =
          "Input script file. There can only be one input file. \"stdin\" is the default value which means console input")
  public final void setInput(String file) {
    Objects.requireNonNull(file, "Input file can't be NULL");
    if (!new File(file).isFile()) {
      throw new IllegalArgumentException("File " + file + " doesn't exist");
    }
    this.input = file;
  }

  /** @param nonInteractive True if CLI runs without user interaction, such as piped input */
  @Option(
      names = {"-n", "--noninteract"},
      description =
          "Non interactive mode. Use this mode if input doesn't come from human or jmxterm is embedded")
  public final void setNonInteractive(boolean nonInteractive) {
    this.nonInteractive = nonInteractive;
  }

  /** @param outputFile It can be a file or {@link #STDERR} or {@link #STDERR} */
  @Option(
      names = {"-o", "--output"},
      description = "Output file, stdout or stderr. Default value is stdout")
  public final void setOutput(String outputFile) {
    Objects.requireNonNull(outputFile, "Output file can't be NULL");
    this.output = outputFile;
  }

  /** @param password Password for user/password authentication */
  @Option(
      names = {"-p", "--password"},
      description = "Password for user/password authentication")
  public final void setPassword(String password) {
    Objects.requireNonNull(password, "Password can't be NULL");
    this.password = password;
  }

  /** @param url MBean server URL */
  @Option(
      names = {"-l", "--url"},
      description = "Location of MBean service. It can be <host>:<port>, jmxmp://<host>:<port>, or full service URL.")
  public final void setUrl(String url) {
    Objects.requireNonNull(url, "URL can't be NULL");
    this.url = url;
  }

  /** @param user User name for user/password authentication */
  @Option(names = {"-u", "--user"}, description = "User name for user/password authentication")
  public final void setUser(String user) {
    Objects.requireNonNull(user, "User can't be NULL");
    this.user = user;
  }

  /** @param quiet True to suppress all messages (silent mode) */
  @Option(
      names = {"-q", "--quiet"},
      description = "Quiet mode: suppress all messages, only output command results")
  public final void setQuiet(boolean quiet) {
    this.quiet = quiet;
  }

  /** @param helpRequested True if user requested help */
  @Option(
      names = {"-h", "--help"},
      usageHelp = true,
      description = "Display this help message")
  public final void setHelpRequested(boolean helpRequested) {
    this.helpRequested = helpRequested;
  }

  /** @param versionRequested True if user requested version info */
  @Option(
      names = {"-v", "--version"},
      versionHelp = true,
      description = "Print version information")
  public final void setVersionRequested(boolean versionRequested) {
    this.versionRequested = versionRequested;
  }

  /** @param appendToOutput True if outputfile is preserved */
  @Option(
      names = {"-a", "--appendtooutput"},
      description = "With this flag, the outputfile is preserved and content is appended to it")
  public final void setAppendToOutput(boolean appendToOutput) {
    this.appendToOutput = appendToOutput;
  }

  /**
   * @param isSecureRmiRegistry Whether the server's RMI registry is protected with SSL/TLS
   *     (com.sun.management.jmxremote.registry.ssl=true)
   */
  @Option(
      names = {"-s", "--sslrmiregistry"},
      description = "Whether the server's RMI registry is protected with SSL/TLS")
  public final void setSecureRmiRegistry(final boolean isSecureRmiRegistry) {
    this.isSecureRmiRegistry = isSecureRmiRegistry;
  }
}
