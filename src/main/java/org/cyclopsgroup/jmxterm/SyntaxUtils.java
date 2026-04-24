package org.cyclopsgroup.jmxterm;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.regex.Pattern;

import javax.management.remote.JMXServiceURL;

import org.cyclopsgroup.jmxterm.utils.ValueFormat;

/**
 * Utility class for syntax checking
 *
 */
public final class SyntaxUtils {
  /** NULL string identifier */
  public static final String NULL = ValueFormat.NULL;

  /** Null print stream to redirect std streams */
  public static final PrintStream NULL_PRINT_STREAM = new PrintStream(OutputStream.nullOutputStream(), true);

  private static final Pattern PATTERN_HOST_PORT = Pattern.compile("^(\\w|\\.|\\-)+\\:\\d+$");
  private static final Pattern PATTERN_JMXMP_URL = Pattern.compile("^jmxmp://(\\S+)$");

  private static final Map<String, Class<?>> PRIMITIVE_TYPES = Map.ofEntries(
      Map.entry("boolean", boolean.class),
      Map.entry("byte", byte.class),
      Map.entry("char", char.class),
      Map.entry("short", short.class),
      Map.entry("int", int.class),
      Map.entry("long", long.class),
      Map.entry("float", float.class),
      Map.entry("double", double.class));

  /**
   * @param url String expression of MBean server URL or abbreviation like localhost:9991
   * @param jpm Java process manager to get process URL
   * @return Parsed JMXServerURL
   * @throws IOException IO error
   */
  public static JMXServiceURL getUrl(String url, JavaProcessManager jpm) throws IOException {
    if (url == null || url.isEmpty()) {
      throw new IllegalArgumentException("Empty URL is not allowed");
    } else if (isDigits(url) && jpm != null) {
      int pid = Integer.parseInt(url);
      JavaProcess p = jpm.get(pid);
      if (p == null) {
        throw new NullPointerException("No such PID " + pid);
      }
      if (!p.isManageable()) {
        p.startManagementAgent();
        if (!p.isManageable()) {
          throw new IllegalStateException("Managed agent for PID " + pid + " couldn't start. PID " + pid + " is not manageable");
        }
      }
      return new JMXServiceURL(p.toUrl());

    } else if (PATTERN_JMXMP_URL.matcher(url).find()) {
      return new JMXServiceURL("service:jmx:" + url);
    } else if (PATTERN_HOST_PORT.matcher(url).find()) {
      return new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + url + "/jmxrmi");
    } else {
      return new JMXServiceURL(url);
    }
  }

  /**
   * Check if string value is <code>null</code>
   *
   * @param s String value
   * @return True if value is <code>null</code>
   */
  public static boolean isNull(String s) {
    return NULL.equalsIgnoreCase(s) || "*".equals(s);
  }

  /**
   * Parse given string expression to expected type of value
   *
   * @param expression String expression
   * @param type Target type
   * @return Object of value
   */
  public static Object parse(String expression, String type) {
    if (expression == null || NULL.equalsIgnoreCase(expression)) {
      return null;
    }
    Class<?> c = resolveClass(type);
    if (c == String.class) {
      return expression;
    }
    if (expression.isEmpty()) {
      return null;
    }
    return convert(expression, c);
  }

  /**
   * Check if string contains only ASCII digits
   *
   * @param s String value
   * @return True if string is non-null, non-empty, and contains only digits
   */
  public static boolean isDigits(String s) {
    if (s == null || s.isEmpty()) {
      return false;
    }
    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);
      if (ch < '0' || ch > '9') {
        return false;
      }
    }
    return true;
  }

  private static Class<?> resolveClass(String type) {
    Class<?> primitive = PRIMITIVE_TYPES.get(type);
    if (primitive != null) {
      return primitive;
    }
    try {
      return Class.forName(type);
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException("Type " + type + " isn't valid", e);
    }
  }

  private static boolean parseBooleanStrict(String expression) {
    if ("true".equalsIgnoreCase(expression)) {
      return true;
    }
    if ("false".equalsIgnoreCase(expression)) {
      return false;
    }
    throw new IllegalArgumentException("Cannot convert \"" + expression + "\" to boolean");
  }

  private static Object convert(String expression, Class<?> targetType) {
    if (targetType == boolean.class || targetType == Boolean.class) {
      return parseBooleanStrict(expression);
    }
    if (targetType == byte.class || targetType == Byte.class) {
      return Byte.parseByte(expression);
    }
    if (targetType == char.class || targetType == Character.class) {
      if (expression.length() != 1) {
        throw new IllegalArgumentException("Cannot convert \"" + expression + "\" to char");
      }
      return expression.charAt(0);
    }
    if (targetType == short.class || targetType == Short.class) {
      return Short.parseShort(expression);
    }
    if (targetType == int.class || targetType == Integer.class) {
      return Integer.parseInt(expression);
    }
    if (targetType == long.class || targetType == Long.class) {
      return Long.parseLong(expression);
    }
    if (targetType == float.class || targetType == Float.class) {
      return Float.parseFloat(expression);
    }
    if (targetType == double.class || targetType == Double.class) {
      return Double.parseDouble(expression);
    }
    if (targetType == BigInteger.class) {
      return new BigInteger(expression);
    }
    if (targetType == BigDecimal.class) {
      return new BigDecimal(expression);
    }
    throw new IllegalArgumentException(
        "Cannot convert \"" + expression + "\" to type " + targetType.getName());
  }

  private SyntaxUtils() {}
}
