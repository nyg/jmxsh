package sh.jmx.jmxsh.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Splits an input string into tokens delimited by whitespace, supporting backslash escaping.
 * Replaces the external {@code caff} library's {@code EscapingValueTokenizer}.
 */
public final class EscapingTokenizer {

  private EscapingTokenizer() {}

  /**
   * Tokenize the input string by splitting on whitespace. Backslash ({@code \}) escapes the next
   * character, allowing spaces and backslashes to appear inside tokens.
   *
   * @param input the command line string to tokenize
   * @return list of tokens, never null
   */
  public static List<String> tokenize(CharSequence input) {
    List<String> tokens = new ArrayList<>();
    StringBuilder word = null;
    boolean escaping = false;

    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);
      if (escaping) {
        if (word == null) {
          word = new StringBuilder();
        }
        word.append(c);
        escaping = false;
      } else if (c == '\\') {
        escaping = true;
        if (word == null) {
          word = new StringBuilder();
        }
      } else if (c == ' ') {
        if (word != null) {
          tokens.add(word.toString());
          word = null;
        }
      } else {
        if (word == null) {
          word = new StringBuilder();
        }
        word.append(c);
      }
    }
    if (word != null) {
      tokens.add(word.toString());
    }
    return tokens;
  }
}
