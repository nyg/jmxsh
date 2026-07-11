package sh.jmx.jmxsh.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ValueFormatTest {
  /** Test parse method */
  @Test
  void parse() {
    assertThat(ValueFormat.parseValue("null")).isNull();
    assertThat(ValueFormat.parseValue(null)).isNull();
    assertThat(ValueFormat.parseValue("")).isNull();
    assertThat(ValueFormat.parseValue("\"\"")).isEmpty();
    assertThat(ValueFormat.parseValue("abc")).isEqualTo("abc");
    assertThat(ValueFormat.parseValue("\"abc\"")).isEqualTo("abc");
    assertThat(ValueFormat.parseValue("ab c")).isEqualTo("ab c");
    assertThat(ValueFormat.parseValue("ab\\nc")).isEqualTo("ab\nc");
    assertThat(ValueFormat.parseValue("ab\\u3160c")).isEqualTo("ab\u3160c");
  }

  @Test
  void parseWithInvalidUnicodeEscape() {
    assertThat(ValueFormat.parseValue("ab\\uGGGGc")).isEqualTo("ab\\uGGGGc");
  }

  @Test
  void translateUnicodeEscapesReturnsInputWhenNoUnicodeEscape() {
    assertThat(ValueFormat.translateUnicodeEscapes("plain\\ntext")).isEqualTo("plain\\ntext");
  }

  @Test
  void translateUnicodeEscapesKeepsEscapedBackslashBeforeU() {
    assertThat(ValueFormat.translateUnicodeEscapes("\\\\uABCD")).isEqualTo("\\\\uABCD");
  }

  @Test
  void translateUnicodeEscapesKeepsBackslashBeforeNonUnicodeChar() {
    String result = ValueFormat.translateUnicodeEscapes("\\n\\uABCD");
    assertThat(result).hasSize(3).startsWith("\\n");
    assertThat(result.codePointAt(2)).isEqualTo(0xABCD);
  }

  @Test
  void translateUnicodeEscapesKeepsTruncatedUnicodeEscape() {
    assertThat(ValueFormat.translateUnicodeEscapes("x\\u12")).isEqualTo("x\\\\u12");
  }

  @Test
  void should_return_true_when_value_is_null_literal_or_wildcard() {
    // When / Then
    assertThat(ValueFormat.isNullLiteral("null")).isTrue();
    assertThat(ValueFormat.isNullLiteral("NULL")).isTrue();
    assertThat(ValueFormat.isNullLiteral("*")).isTrue();
  }

  @Test
  void should_return_false_when_value_is_ordinary_or_null() {
    // When / Then
    assertThat(ValueFormat.isNullLiteral("abc")).isFalse();
    assertThat(ValueFormat.isNullLiteral(null)).isFalse();
  }
}
