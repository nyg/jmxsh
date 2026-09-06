package sh.jmx.jmxsh.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.node.IntNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.MissingNode;
import tools.jackson.databind.node.NullNode;
import tools.jackson.databind.node.StringNode;

@ExtendWith(MockitoExtension.class)
class MBeanValueParserTest {

  @InjectMocks
  private MBeanValueParser unit;

  @Test
  void should_return_expression_when_type_is_string() {
    // When
    Object result = unit.parse("x", "java.lang.String");

    // Then
    assertThat(result).isEqualTo("x");
  }

  @Test
  void should_return_empty_string_when_expression_is_empty_and_type_is_string() {
    // When
    Object result = unit.parse("", "java.lang.String");

    // Then
    assertThat(result).isEqualTo("");
  }

  @Test
  void should_return_null_when_expression_is_empty_and_type_is_not_string() {
    // When
    Object result = unit.parse("", "java.util.Date");

    // Then
    assertThat(result).isNull();
  }

  @Test
  void should_return_null_when_expression_is_null_literal() {
    // When
    Object result = unit.parse("null", "java.lang.String");

    // Then
    assertThat(result).isNull();
  }

  @Test
  void should_return_null_when_expression_is_null() {
    // When
    Object result = unit.parse(null, "x");

    // Then
    assertThat(result).isNull();
  }

  @Test
  void should_parse_int_when_type_is_int() {
    // When / Then
    assertThat(unit.parse("3", "int")).isEqualTo(3);
    assertThat(unit.parse("3", "java.lang.Integer")).isEqualTo(3);
  }

  @Test
  void should_parse_long_when_type_is_long() {
    // When / Then
    assertThat(unit.parse("3", "long")).isEqualTo(3L);
    assertThat(unit.parse("3", "java.lang.Long")).isEqualTo(3L);
  }

  @Test
  void should_parse_boolean_when_type_is_boolean() {
    // When / Then
    assertThat(unit.parse("true", "boolean")).isEqualTo(true);
    assertThat(unit.parse("false", "java.lang.Boolean")).isEqualTo(false);
  }

  @Test
  void should_throw_when_boolean_expression_is_invalid() {
    // When / Then
    assertThatThrownBy(() -> unit.parse("x", "boolean"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot convert \"x\" to boolean");
  }

  @Test
  void should_parse_byte_when_type_is_byte() {
    // When / Then
    assertThat(unit.parse("3", "byte")).isEqualTo((byte) 3);
    assertThat(unit.parse("3", "java.lang.Byte")).isEqualTo((byte) 3);
  }

  @Test
  void should_parse_char_when_type_is_char() {
    // When / Then
    assertThat(unit.parse("A", "char")).isEqualTo('A');
    assertThat(unit.parse("A", "java.lang.Character")).isEqualTo('A');
  }

  @Test
  void should_throw_when_char_expression_has_multiple_characters() {
    // When / Then
    assertThatThrownBy(() -> unit.parse("AB", "char"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot convert \"AB\" to char");
  }

  @Test
  void should_parse_short_when_type_is_short() {
    // When / Then
    assertThat(unit.parse("7", "short")).isEqualTo((short) 7);
    assertThat(unit.parse("7", "java.lang.Short")).isEqualTo((short) 7);
  }

  @Test
  void should_parse_float_when_type_is_float() {
    // When / Then
    assertThat(unit.parse("1.5", "float")).isEqualTo(1.5f);
    assertThat(unit.parse("1.5", "java.lang.Float")).isEqualTo(1.5f);
  }

  @Test
  void should_parse_double_when_type_is_double() {
    // When / Then
    assertThat(unit.parse("3.14", "double")).isEqualTo(3.14);
    assertThat(unit.parse("3.14", "java.lang.Double")).isEqualTo(3.14);
  }

  @Test
  void should_parse_big_integer_when_type_is_big_integer() {
    // When
    Object result = unit.parse("999999999999", "java.math.BigInteger");

    // Then
    assertThat(result).isEqualTo(new BigInteger("999999999999"));
  }

  @Test
  void should_parse_big_decimal_when_type_is_big_decimal() {
    // When
    Object result = unit.parse("1.23456789", "java.math.BigDecimal");

    // Then
    assertThat(result).isEqualTo(new BigDecimal("1.23456789"));
  }

  @Test
  void should_throw_when_type_is_invalid() {
    // When / Then
    assertThatThrownBy(() -> unit.parse("x", "x"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Type x isn't valid");
  }

  @Test
  void should_throw_when_jackson_cannot_deserialize_type() {
    // When / Then
    assertThatThrownBy(() -> unit.parse("x", "java.lang.Runnable"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageStartingWith("Cannot convert \"x\" to type java.lang.Runnable: ");
  }

  @Test
  void should_parse_instant_when_type_is_instant() {
    // When
    Object result = unit.parse("2026-01-01T00:00:00Z", "java.time.Instant");

    // Then
    assertThat(result).isEqualTo(Instant.parse("2026-01-01T00:00:00Z"));
  }

  @Test
  void should_parse_uuid_when_type_is_uuid() {
    // When
    Object result = unit.parse("f81d4fae-7dec-11d0-a765-00a0c91e6bf6", "java.util.UUID");

    // Then
    assertThat(result).isEqualTo(UUID.fromString("f81d4fae-7dec-11d0-a765-00a0c91e6bf6"));
  }

  @Test
  void should_parse_enum_constant_when_type_is_enum() {
    // When
    Object result = unit.parse("MONDAY", "java.time.DayOfWeek");

    // Then
    assertThat(result).isEqualTo(DayOfWeek.MONDAY);
  }

  @Test
  void should_parse_int_array_when_expression_is_a_json_array() {
    // When
    Object result = unit.parse("[1, 2, 3]", "[I");

    // Then
    assertThat(result).isEqualTo(new int[] {1, 2, 3});
  }

  @Test
  void should_parse_string_array_when_expression_is_a_json_array() {
    // When
    Object result = unit.parse("[\"a\", \"b\"]", "[Ljava.lang.String;");

    // Then
    assertThat(result).isEqualTo(new String[] {"a", "b"});
  }

  @Test
  void should_parse_map_when_type_is_object_and_expression_is_a_json_document() {
    // When
    Object result = unit.parse("{\"a\": 1}", "java.lang.Object");

    // Then
    assertThat(result).isEqualTo(Map.of("a", 1));
  }

  @Test
  void should_return_expression_when_type_is_object_and_expression_is_not_a_json_document() {
    // When
    Object result = unit.parse("x", "java.lang.Object");

    // Then
    assertThat(result).isEqualTo("x");
  }

  @Test
  void should_throw_when_expression_is_a_malformed_json_document() {
    // When / Then
    assertThatThrownBy(() -> unit.parse("[1, ", "[I"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageStartingWith("Cannot convert \"[1, \" to type [I: ");
  }

  @Test
  void should_return_null_when_node_is_null() {
    // When / Then
    assertThat(unit.parseNode(null, "int")).isNull();
    assertThat(unit.parseNode(NullNode.getInstance(), "int")).isNull();
    assertThat(unit.parseNode(MissingNode.getInstance(), "int")).isNull();
  }

  @Test
  void should_parse_int_when_node_is_a_number() {
    // When
    Object result = unit.parseNode(IntNode.valueOf(3), "int");

    // Then
    assertThat(result).isEqualTo(3);
  }

  @Test
  void should_return_text_verbatim_when_node_is_a_string_and_type_is_string() {
    // When
    Object result = unit.parseNode(StringNode.valueOf("null"), "java.lang.String");

    // Then
    assertThat(result).isEqualTo("null");
  }

  @Test
  void should_parse_instant_when_node_is_a_string() {
    // When
    Object result = unit.parseNode(StringNode.valueOf("2026-01-01T00:00:00Z"), "java.time.Instant");

    // Then
    assertThat(result).isEqualTo(Instant.parse("2026-01-01T00:00:00Z"));
  }

  @Test
  void should_parse_int_array_when_node_is_an_array() {
    // When
    Object result =
        unit.parseNode(JsonNodeFactory.instance.arrayNode().add(1).add(2), "[I");

    // Then
    assertThat(result).isEqualTo(new int[] {1, 2});
  }

  @Test
  void should_throw_when_node_cannot_be_converted_to_type() {
    // Given
    StringNode node = StringNode.valueOf("x");

    // When / Then
    assertThatThrownBy(() -> unit.parseNode(node, "int"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageStartingWith("Cannot convert \"\"x\"\" to type int: ");
  }
}
