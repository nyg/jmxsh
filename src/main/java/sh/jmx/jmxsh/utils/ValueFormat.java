package sh.jmx.jmxsh.utils;

/**
 * This is an utility to parse string value from input. It's only to parse a value such as MBean
 * attribute value or parameter of operation. It's NOT designed to parse MBean name or other type of
 * input.
 *
 */
public final class ValueFormat {
  /** Keyword that identifies NULL pointer <code>null</code> */
  public static final String NULL = "null";

  private ValueFormat() {}

  /**
   * Parse given syntax of string
   *
   * @param value String value
   * @return Escaped string value
   */
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
    var sb = new StringBuilder(input.length());
    for (int i = 0; i < input.length(); i++) {
      char ch = input.charAt(i);
      if (ch == '\\' && i + 1 < input.length()) {
        char next = input.charAt(i + 1);
        if (next == '\\') {
          sb.append("\\\\");
          i++;
        } else if (next == 'u') {
          if (i + 6 <= input.length()) {
            try {
              sb.append((char) Integer.parseInt(input.substring(i + 2, i + 6), 16));
              i += 5;
            } catch (NumberFormatException e) {
              sb.append("\\\\u");
              i++;
            }
          } else {
            sb.append("\\\\u");
            i++;
          }
        } else {
          sb.append(ch);
        }
      } else {
        sb.append(ch);
      }
    }
    return sb.toString();
  }
}
