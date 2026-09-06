package sh.jmx.jmxsh.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;

import javax.management.MBeanParameterInfo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OperationArgumentsTest {

  @Test
  void should_bind_by_position_when_arguments_are_positional() {
    // Given
    OperationArguments unit =
        OperationArguments.ofPositional(List.of("3", "hello"), new MBeanValueParser());
    MBeanParameterInfo[] signature = {
        new MBeanParameterInfo("p1", "int", ""),
        new MBeanParameterInfo("p2", "java.lang.String", "")};

    // When / Then
    assertThat(unit.size()).isEqualTo(2);
    assertThat(unit.fits(signature)).isTrue();
    assertThat(unit.bind(signature)).containsExactly(3, "hello");
  }

  @Test
  void should_bind_by_position_when_json_is_an_array() {
    // Given
    OperationArguments unit =
        OperationArguments.ofJson("[3, \"2026-01-01T00:00:00Z\"]", new MBeanValueParser());
    MBeanParameterInfo[] signature = {
        new MBeanParameterInfo("p1", "int", ""),
        new MBeanParameterInfo("p2", "java.time.Instant", "")};

    // When / Then
    assertThat(unit.size()).isEqualTo(2);
    assertThat(unit.fits(signature)).isTrue();
    assertThat(unit.bind(signature))
        .containsExactly(3, Instant.parse("2026-01-01T00:00:00Z"));
  }

  @Test
  void should_bind_by_name_when_json_is_an_object() {
    // Given
    OperationArguments unit =
        OperationArguments.ofJson("{\"p2\": 5, \"p1\": 3}", new MBeanValueParser());
    MBeanParameterInfo[] signature = {
        new MBeanParameterInfo("p1", "int", ""),
        new MBeanParameterInfo("p2", "int", "")};

    // When / Then
    assertThat(unit.size()).isEqualTo(2);
    assertThat(unit.fits(signature)).isTrue();
    assertThat(unit.bind(signature)).containsExactly(3, 5);
  }

  @Test
  void should_not_fit_when_json_object_keys_do_not_match_signature() {
    // Given
    OperationArguments unit =
        OperationArguments.ofJson("{\"a\": 3, \"b\": 5}", new MBeanValueParser());
    MBeanParameterInfo[] signature = {
        new MBeanParameterInfo("p1", "int", ""),
        new MBeanParameterInfo("p2", "int", "")};

    // When / Then
    assertThat(unit.fits(signature)).isFalse();
  }

  @Test
  void should_not_fit_when_argument_count_differs_from_signature() {
    // Given
    MBeanValueParser parser = new MBeanValueParser();
    MBeanParameterInfo[] signature = {new MBeanParameterInfo("p1", "int", "")};

    // When / Then
    assertThat(OperationArguments.ofPositional(List.of("3", "5"), parser).fits(signature))
        .isFalse();
    assertThat(OperationArguments.ofJson("[3, 5]", parser).fits(signature)).isFalse();
    assertThat(OperationArguments.ofJson("{\"p1\": 3, \"p2\": 5}", parser).fits(signature))
        .isFalse();
  }

  @Test
  void should_bind_null_when_json_array_element_is_null() {
    // Given
    OperationArguments unit = OperationArguments.ofJson("[null]", new MBeanValueParser());
    MBeanParameterInfo[] signature = {new MBeanParameterInfo("p1", "java.lang.String", "")};

    // When / Then
    assertThat(unit.bind(signature)).containsExactly((Object) null);
  }

  @Test
  void should_throw_when_json_is_neither_an_array_nor_an_object() {
    // When / Then
    assertThatThrownBy(() -> OperationArguments.ofJson("\"x\"", new MBeanValueParser()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("JSON parameters must be an array or an object but were STRING");
  }

  @Test
  void should_throw_when_json_is_malformed() {
    // When / Then
    assertThatThrownBy(() -> OperationArguments.ofJson("[3,", new MBeanValueParser()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageStartingWith("Invalid JSON parameters: ");
  }
}
