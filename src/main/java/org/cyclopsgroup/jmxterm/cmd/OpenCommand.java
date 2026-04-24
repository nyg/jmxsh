package org.cyclopsgroup.jmxterm.cmd;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.rmi.ssl.SslRMIClientSocketFactory;

import org.cyclopsgroup.jmxterm.Command;
import org.cyclopsgroup.jmxterm.Connection;
import org.cyclopsgroup.jmxterm.Session;
import org.cyclopsgroup.jmxterm.SyntaxUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Command to open JMX connection
 *
 * @author <a href="mailto:jiaqi.guo@gmail.com">Jiaqi Guo</a>
 */
@CommandLine.Command(
    name = "open",
    description = "Open JMX session or display current connection",
    footer =
        """
        Without argument this command display current connection. \
        URL can be a <PID>, <hostname>:<port> or full qualified JMX service URL. \
        For JMXMP connections, use jmxmp://<hostname>:<port>. For example
         open localhost:9991,
         open jmxmp://localhost:9991,
         open service:jmx:jmxmp://localhost:9991,
         open jmx:service:...""")
public class OpenCommand extends Command {
  private static final Logger LOG = LoggerFactory.getLogger(OpenCommand.class);
  private String password;

  private String url;

  private String user;

  private boolean isSecureRmiRegistry;

  @Override
  public void execute() throws IOException {
    Session session = getSession();
    if (url != null) {
      LOG.info("opening JMX connection to {}", url);
    }
    if (url == null) {
      Connection con = session.getConnection();
      if (con == null) {
        session.getOutput().printMessage("not connected");
        session.getOutput().println(SyntaxUtils.NULL);
      } else {
        session.getOutput().println("%s,%s".formatted(con.getConnectorId(), con.getUrl()));
      }
      return;
    }
    Map<String, Object> env = new HashMap<>();
    if (user != null) {
      if (password == null) {
        password = session.getInput().readMaskedString("Credential password: ");
      }
      String[] credentials = {user, password};
      env.put(JMXConnector.CREDENTIALS, credentials);
    }
    if (isSecureRmiRegistry) {
      // Required to prevent "java.rmi.ConnectIOException: non-JRMP server at remote endpoint" error
      env.put("com.sun.jndi.rmi.factory.socket", new SslRMIClientSocketFactory());
    }
    try {
      session.connect(
          SyntaxUtils.getUrl(url, session.getProcessManager()), env.isEmpty() ? null : env);
      session.getOutput().printMessage("Connection to " + url + " is opened");
    } catch (IOException e) {
      if (SyntaxUtils.isDigits(url)) {
        session.getOutput().printMessage(
            "Couldn't connect to PID "
                + url
                + ", it's likely that your version of JDK doesn't allow to connect to a process directly");
      }
      throw e;
    }
  }

  /** @param password Password for user authentication */
  @Option(
      names = {"-p", "--password"},
      description = "Password for user/password authentication")
  public final void setPassword(String password) {
    this.password = password;
  }

  /** @param url URL of MBean service to open */
  @Parameters(paramLabel = "url", description = "URL, <host>:<port>, jmxmp://<host>:<port>, or a PID to connect to", arity = "0..1")
  public final void setUrl(String url) {
    this.url = url;
  }

  /** @param user User name for user authentication */
  @Option(names = {"-u", "--user"}, description = "User name for user/password authentication")
  public final void setUser(String user) {
    this.user = user;
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
