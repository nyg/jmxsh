package org.cyclopsgroup.jmxterm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

/**
 * Test case of {@link SyntaxUtils}
 *
 */
class SyntaxUtilsTest {
  /**
   * Test how getUrl() figure out MBeanServer URL based on various pattern of input
   *
   * @throws IOException Thrown when syntax is invalid
   */
  @Test
  void getUrl() throws Exception {
    assertThat(SyntaxUtils.getUrl("xyz-host.cyclopsgroup.org:12345", null).getURLPath())
        .isEqualTo("/jndi/rmi://xyz-host.cyclopsgroup.org:12345/jmxrmi");
    assertThat(
        SyntaxUtils.getUrl(
                "service:jmx:rmi:///jndi/rmi://xyz-host.cyclopsgroup.org:12345/jmxrmi", null)
            .getURLPath())
        .isEqualTo("/jndi/rmi://xyz-host.cyclopsgroup.org:12345/jmxrmi");
  }

  @Test
  void getUrlWithJmxmpShorthand() throws Exception {
    assertThat(SyntaxUtils.getUrl("jmxmp://localhost:9999", null))
        .satisfies(
            url -> {
              assertThat(url.getProtocol()).isEqualTo("jmxmp");
              assertThat(url.getHost()).isEqualTo("localhost");
              assertThat(url.getPort()).isEqualTo(9999);
            });
  }

  @Test
  void getUrlWithJmxmpShorthandDottedHost() throws Exception {
    assertThat(SyntaxUtils.getUrl("jmxmp://my-host.example.com:5555", null))
        .satisfies(
            url -> {
              assertThat(url.getProtocol()).isEqualTo("jmxmp");
              assertThat(url.getHost()).isEqualTo("my-host.example.com");
              assertThat(url.getPort()).isEqualTo(5555);
            });
  }

  @Test
  void getUrlWithFullJmxmpServiceUrl() throws Exception {
    assertThat(SyntaxUtils.getUrl("service:jmx:jmxmp://localhost:9999", null))
        .satisfies(
            url -> {
              assertThat(url.getProtocol()).isEqualTo("jmxmp");
              assertThat(url.getHost()).isEqualTo("localhost");
              assertThat(url.getPort()).isEqualTo(9999);
            });
  }

  /** Verify string expression of type is correctly parsed */
  @Test
  void parseNormally() {
    assertThat(SyntaxUtils.parse("x", "java.lang.String")).isEqualTo("x");
    assertThat(SyntaxUtils.parse("3", "int")).isEqualTo(3);
    assertThat(SyntaxUtils.parse("3", "long")).isEqualTo(3L);
    assertThat(SyntaxUtils.parse("", "java.lang.String")).isEqualTo("");
    assertThat(SyntaxUtils.parse("", "java.util.Date")).isNull();
    assertThat(SyntaxUtils.parse("null", "java.lang.String")).isNull();
  }

  /** Verify that Exception is thrown when type is wrong */
  @Test
  void parseWithWrongType() {
    assertThatThrownBy(() -> SyntaxUtils.parse("x", "x"))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void parseBoolean() {
    assertThat(SyntaxUtils.parse("true", "boolean")).isEqualTo(true);
    assertThat(SyntaxUtils.parse("false", "java.lang.Boolean")).isEqualTo(false);
  }

  @Test
  void parseByte() {
    assertThat(SyntaxUtils.parse("3", "byte")).isEqualTo((byte) 3);
    assertThat(SyntaxUtils.parse("3", "java.lang.Byte")).isEqualTo((byte) 3);
  }

  @Test
  void parseChar() {
    assertThat(SyntaxUtils.parse("A", "char")).isEqualTo('A');
    assertThat(SyntaxUtils.parse("A", "java.lang.Character")).isEqualTo('A');
  }

  @Test
  void parseCharRejectsMultipleChars() {
    assertThatThrownBy(() -> SyntaxUtils.parse("AB", "char"))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void parseShort() {
    assertThat(SyntaxUtils.parse("7", "short")).isEqualTo((short) 7);
    assertThat(SyntaxUtils.parse("7", "java.lang.Short")).isEqualTo((short) 7);
  }

  @Test
  void parseFloat() {
    assertThat(SyntaxUtils.parse("1.5", "float")).isEqualTo(1.5f);
    assertThat(SyntaxUtils.parse("1.5", "java.lang.Float")).isEqualTo(1.5f);
  }

  @Test
  void parseDouble() {
    assertThat(SyntaxUtils.parse("3.14", "double")).isEqualTo(3.14);
    assertThat(SyntaxUtils.parse("3.14", "java.lang.Double")).isEqualTo(3.14);
  }

  @Test
  void parseBigInteger() {
    assertThat(SyntaxUtils.parse("999999999999", "java.math.BigInteger"))
        .isEqualTo(new BigInteger("999999999999"));
  }

  @Test
  void parseBigDecimal() {
    assertThat(SyntaxUtils.parse("1.23456789", "java.math.BigDecimal"))
        .isEqualTo(new BigDecimal("1.23456789"));
  }

  @Test
  void parseUnsupportedType() {
    assertThatThrownBy(() -> SyntaxUtils.parse("x", "java.lang.Object"))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
