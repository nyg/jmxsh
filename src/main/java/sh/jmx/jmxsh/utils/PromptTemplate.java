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
 *
 * <p>Optional blocks: wrap any content in {@code {?...}} to render it only when at least one
 * variable inside resolves to a non-empty value. Blocks may be nested.
 * <pre>
 *   {?[{server}] }{?{domain}}{?/{bean}}> 
 * </pre>
 * renders as {@code > } when nothing is selected, and as
 * {@code [h:9010] java.lang/type=Memory> } when fully connected.
 */
public final class PromptTemplate {

  private PromptTemplate() {}

  /**
   * Resolves all template variables and optional blocks in {@code template} using the current
   * state of {@code session}. Returns {@code template} unchanged when {@code session} is
   * {@code null}.
   */
  public static String resolve(String template, Session session) {
    if (session == null) {
      return template;
    }
    String server = resolveServer(session);
    String domain = session.getDomain() != null ? session.getDomain() : "";
    String bean = session.getBean() != null ? session.getBean() : "";
    return processTemplate(template, server, domain, bean);
  }

  private static String processTemplate(String template, String server, String domain, String bean) {
    StringBuilder result = new StringBuilder();
    int i = 0;
    while (i < template.length()) {
      char c = template.charAt(i);
      if (c == '{' && i + 1 < template.length()) {
        char next = template.charAt(i + 1);
        if (next == '?') {
          int blockEnd = findBlockEnd(template, i + 2);
          if (blockEnd >= 0) {
            String blockContent = template.substring(i + 2, blockEnd);
            if (hasAnyNonEmptyVar(blockContent, server, domain, bean)) {
              result.append(substituteVars(blockContent, server, domain, bean));
            }
            i = blockEnd + 1;
            continue;
          }
        } else {
          int end = template.indexOf('}', i + 1);
          if (end >= 0) {
            String varName = template.substring(i + 1, end);
            switch (varName) {
              case "server" -> result.append(server);
              case "domain" -> result.append(domain);
              case "bean"   -> result.append(bean);
              default -> {
                result.append('{');
                result.append(varName);
                result.append('}');
              }
            }
            i = end + 1;
            continue;
          }
        }
      }
      result.append(c);
      i++;
    }
    return result.toString();
  }

  /**
   * Finds the index of the closing {@code }} that balances the opening {@code {?} already
   * consumed. Searches from {@code start}, tracking brace depth.
   *
   * @return index of the matching {@code }}, or {@code -1} if not found
   */
  static int findBlockEnd(String template, int start) {
    int depth = 1;
    for (int i = start; i < template.length(); i++) {
      char c = template.charAt(i);
      if (c == '{') {
        depth++;
      } else if (c == '}') {
        depth--;
        if (depth == 0) {
          return i;
        }
      }
    }
    return -1;
  }

  private static boolean hasAnyNonEmptyVar(String content, String server, String domain, String bean) {
    if (content.contains("{server}") && !server.isEmpty()) return true;
    if (content.contains("{domain}") && !domain.isEmpty()) return true;
    if (content.contains("{bean}") && !bean.isEmpty()) return true;
    return false;
  }

  private static String substituteVars(String template, String server, String domain, String bean) {
    return processTemplate(template, server, domain, bean);
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
