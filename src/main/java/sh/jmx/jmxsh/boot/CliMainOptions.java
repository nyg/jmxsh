package sh.jmx.jmxsh.boot;

import java.nio.file.Files;
import java.nio.file.Path;

import lombok.Getter;
import lombok.NonNull;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "jmxsh",
    description = "Interactive JMX shell",
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
  public static final String STDERR = "stderr";

  public static final String STDIN = "stdin";

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

  @Option(
      names = {"-e", "--exitonfailure"},
      description = "With this flag, terminal exits for any Exception")
  public final void setExitOnFailure(boolean exitOnFailure) {
    this.exitOnFailure = exitOnFailure;
  }

  @Option(
      names = {"-i", "--input"},
      description =
          "Input script file. There can only be one input file. \"stdin\" is the default value which means console input")
  public final void setInput(@NonNull String file) {
    if (!Files.isRegularFile(Path.of(file))) {
      throw new IllegalArgumentException("File " + file + " doesn't exist");
    }
    this.input = file;
  }

  @Option(
      names = {"-n", "--noninteract"},
      description =
          "Non interactive mode. Use this mode if input doesn't come from human or jmxsh is embedded")
  public final void setNonInteractive(boolean nonInteractive) {
    this.nonInteractive = nonInteractive;
  }

  @Option(
      names = {"-o", "--output"},
      description = "Output file, stdout or stderr. Default value is stdout")
  public final void setOutput(@NonNull String outputFile) {
    this.output = outputFile;
  }

  @Option(
      names = {"-p", "--password"},
      description = "Password for user/password authentication")
  public final void setPassword(@NonNull String password) {
    this.password = password;
  }

  @Option(
      names = {"-l", "--url"},
      description = "Location of MBean service. It can be <host>:<port>, jmxmp://<host>:<port>, or full service URL.")
  public final void setUrl(@NonNull String url) {
    this.url = url;
  }

  @Option(names = {"-u", "--user"}, description = "User name for user/password authentication")
  public final void setUser(@NonNull String user) {
    this.user = user;
  }

  @Option(
      names = {"-q", "--quiet"},
      description = "Quiet mode: suppress all messages, only output command results")
  public final void setQuiet(boolean quiet) {
    this.quiet = quiet;
  }

  @Option(
      names = {"-h", "--help"},
      usageHelp = true,
      description = "Display this help message")
  public final void setHelpRequested(boolean helpRequested) {
    this.helpRequested = helpRequested;
  }

  @Option(
      names = {"-v", "--version"},
      versionHelp = true,
      description = "Print version information")
  public final void setVersionRequested(boolean versionRequested) {
    this.versionRequested = versionRequested;
  }

  @Option(
      names = {"-a", "--appendtooutput"},
      description = "With this flag, the outputfile is preserved and content is appended to it")
  public final void setAppendToOutput(boolean appendToOutput) {
    this.appendToOutput = appendToOutput;
  }

  @Option(
      names = {"-s", "--sslrmiregistry"},
      description = "Whether the server's RMI registry is protected with SSL/TLS")
  public final void setSecureRmiRegistry(final boolean isSecureRmiRegistry) {
    this.isSecureRmiRegistry = isSecureRmiRegistry;
  }
}
