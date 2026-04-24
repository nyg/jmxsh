package org.cyclopsgroup.jmxterm.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Test case of {@link ValueFormat}
 *
 * @version $Revision$ in $Change$ submitted at $DateTime$
 */
class ValueFormatTest {
  /** Test parse method */
  @Test
  void parse() {
    assertThat(ValueFormat.parseValue("null")).isNull();
    assertThat(ValueFormat.parseValue(null)).isNull();
    assertThat(ValueFormat.parseValue("")).isNull();
    assertThat(ValueFormat.parseValue("\"\"")).isEqualTo("");
    assertThat(ValueFormat.parseValue("abc")).isEqualTo("abc");
    assertThat(ValueFormat.parseValue("\"abc\"")).isEqualTo("abc");
    assertThat(ValueFormat.parseValue("ab c")).isEqualTo("ab c");
    assertThat(ValueFormat.parseValue("ab\\nc")).isEqualTo("ab\nc");
    assertThat(ValueFormat.parseValue("ab\\u3160c")).isEqualTo("ab\u3160c");
  }
}
