package sh.jmx.jmxsh.utils;

/**
 * This is an utility to parse string value from input. It's only to parse a value such as MBean
 * attribute value or parameter of operation. It's NOT designed to parse MBean name or other type of
 * input.
 *
 */
public final class ValueFormat {
  public static final String NULL = "null";

  private ValueFormat() {}

  public static boolean isNullLiteral(String s) {
    return NULL.equalsIgnoreCase(s) || "*".equals(s);
  }

  public static String parseValue(String value) {
    if (value == null || value.isEmpty()) {
      return null;
    }
    if (NULL.equals(value)) {
      return null;
    }
    String s;
    if (value.charAt(0) == '\"' && value.charAt(value.length() - 1) == '\"') {
      s = value.substring(1, value.length() - 1);
    } else {
      s = value;
    }
    return translateUnicodeEscapes(s).translateEscapes();
  }

  /**
   * Pre-process {@code \}{@code uXXXX} Unicode escape sequences which
   * {@link String#translateEscapes()} does not handle. Escaped backslashes are preserved for
   * translateEscapes() to process.
   */
  static String translateUnicodeEscapes(String input) {
    if (!input.contains("\\u")) {
      return input;
    }
    StringBuilder sb = new StringBuilder(input.length());
    int i = 0;
    while (i < input.length()) {
      char ch = input.charAt(i);
      if (ch == '\\' && i + 1 < input.length()) {
        i = appendEscape(input, i, sb);
      } else {
        sb.append(ch);
        i++;
      }
    }
    return sb.toString();
  }

  private static int appendEscape(String input, int i, StringBuilder sb) {
    char next = input.charAt(i + 1);
    if (next == '\\') {
      sb.append("\\\\");
      return i + 2;
    }
    if (next == 'u') {
      return appendUnicodeEscape(input, i, sb);
    }
    sb.append(input.charAt(i));
    return i + 1;
  }

  private static int appendUnicodeEscape(String input, int i, StringBuilder sb) {
    if (i + 6 <= input.length()) {
      try {
        sb.append((char) Integer.parseInt(input.substring(i + 2, i + 6), 16));
        return i + 6;
      } catch (NumberFormatException _) {
        sb.append("\\\\u");
        return i + 2;
      }
    }
    sb.append("\\\\u");
    return i + 2;
  }
}
