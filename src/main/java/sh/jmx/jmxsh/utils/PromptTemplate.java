package sh.jmx.jmxsh.utils;

import javax.management.remote.JMXServiceURL;

import sh.jmx.jmxsh.Session;

/**
 * Resolves template variables in the REPL prompt string against current session state.
 *
 * <p>Supported variables:
 * <ul>
 *   <li>{@code {server}} — connected JMX server ({@code host:port} when the URL carries a host,
 *       otherwise the full URL string); empty when not connected
 *   <li>{@code {domain}} — currently selected domain; empty when none is selected
 *   <li>{@code {bean}} — currently selected bean; empty when none is selected
 * </ul>
 */
public final class PromptTemplate {

  private PromptTemplate() {}

  /**
   * Resolves all template variables in {@code template} using the current state of {@code session}.
   * Returns {@code template} unchanged when {@code session} is {@code null}.
   */
  public static String resolve(String template, Session session) {
    if (session == null) {
      return template;
    }
    String server = resolveServer(session);
    String domain = session.getDomain() != null ? session.getDomain() : "";
    String bean = session.getBean() != null ? session.getBean() : "";
    return template
        .replace("{server}", server)
        .replace("{domain}", domain)
        .replace("{bean}", bean);
  }

  private static String resolveServer(Session session) {
    if (!session.isConnected()) {
      return "";
    }
    JMXServiceURL url = session.getConnection().url();
    String host = url.getHost();
    if (host != null && !host.isBlank()) {
      return host + ":" + url.getPort();
    }
    return url.toString();
  }
}
