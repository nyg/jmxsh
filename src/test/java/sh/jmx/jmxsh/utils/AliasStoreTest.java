package sh.jmx.jmxsh.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AliasStoreTest {

  @TempDir
  private Path tempDir;

  @Test
  void should_return_stored_value_when_alias_defined() throws Exception {
    // Given
    AliasStore unit = new AliasStore(tempDir.resolve("aliases.properties"));
    unit.put("my_server", "myserver:1234");

    // When
    String resolved = unit.resolve("my_server");

    // Then
    assertThat(resolved).isEqualTo("myserver:1234");
  }

  @Test
  void should_return_input_unchanged_when_alias_unknown() {
    // Given
    AliasStore unit = new AliasStore(tempDir.resolve("aliases.properties"));

    // When
    String resolved = unit.resolve("localhost:9991");

    // Then
    assertThat(resolved).isEqualTo("localhost:9991");
  }

  @Test
  void should_return_null_when_resolving_null() {
    // Given
    AliasStore unit = new AliasStore(tempDir.resolve("aliases.properties"));

    // When
    String resolved = unit.resolve(null);

    // Then
    assertThat(resolved).isNull();
  }

  @Test
  void should_persist_sorted_entries_when_put_called() throws Exception {
    // Given
    Path file = tempDir.resolve("aliases.properties");
    AliasStore unit = new AliasStore(file);

    // When
    unit.put("zebra", "zhost:1");
    unit.put("apple", "ahost:2");

    // Then
    assertThat(file)
        .hasContent(
            """
            # jmxsh connection aliases; managed by the "alias" command, hand-editing is OK
            apple=ahost:2
            zebra=zhost:1""");
  }

  @Test
  void should_load_hand_edited_file_when_present() throws Exception {
    // Given
    Path file = tempDir.resolve("aliases.properties");
    Files.writeString(file, "# a comment\nmy_server=service:jmx:rmi:///jndi/rmi://h:1/jmxrmi\n");

    // When
    AliasStore unit = new AliasStore(file);

    // Then
    assertThat(unit.resolve("my_server")).isEqualTo("service:jmx:rmi:///jndi/rmi://h:1/jmxrmi");
  }

  @Test
  void should_skip_invalid_keys_when_loading() throws Exception {
    // Given
    Path file = tempDir.resolve("aliases.properties");
    Files.writeString(file, "1234=host:1\nvalid_name=host:2\n");

    // When
    AliasStore unit = new AliasStore(file);

    // Then
    assertThat(unit.names()).containsExactly("valid_name");
  }

  @Test
  void should_throw_when_name_is_all_digits() {
    // Given
    AliasStore unit = new AliasStore(tempDir.resolve("aliases.properties"));

    // When / Then
    assertThatThrownBy(() -> unit.put("1234", "host:1"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("1234");
  }

  @Test
  void should_throw_when_name_contains_colon() {
    // Given
    AliasStore unit = new AliasStore(tempDir.resolve("aliases.properties"));

    // When / Then
    assertThatThrownBy(() -> unit.put("host:1", "host:1"))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void should_throw_when_name_contains_slash() {
    // Given
    AliasStore unit = new AliasStore(tempDir.resolve("aliases.properties"));

    // When / Then
    assertThatThrownBy(() -> unit.put("jmxmp://h", "host:1"))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void should_accept_valid_names_when_validating() {
    assertThat(AliasStore.isValidName("my_server")).isTrue();
    assertThat(AliasStore.isValidName("_x")).isTrue();
    assertThat(AliasStore.isValidName("a.b-c")).isTrue();
    assertThat(AliasStore.isValidName("Server1")).isTrue();
  }

  @Test
  void should_reject_invalid_names_when_validating() {
    assertThat(AliasStore.isValidName(null)).isFalse();
    assertThat(AliasStore.isValidName("")).isFalse();
    assertThat(AliasStore.isValidName("1abc")).isFalse();
    assertThat(AliasStore.isValidName("a b")).isFalse();
  }

  @Test
  void should_return_false_when_removing_unknown_alias() throws Exception {
    // Given
    Path file = tempDir.resolve("aliases.properties");
    AliasStore unit = new AliasStore(file);

    // When
    boolean removed = unit.remove("nope");

    // Then
    assertThat(removed).isFalse();
    assertThat(file).doesNotExist();
  }

  @Test
  void should_persist_removal_when_alias_exists() throws Exception {
    // Given
    Path file = tempDir.resolve("aliases.properties");
    AliasStore unit = new AliasStore(file);
    unit.put("my_server", "host:1");

    // When
    boolean removed = unit.remove("my_server");

    // Then
    assertThat(removed).isTrue();
    assertThat(new AliasStore(file).names()).isEmpty();
  }

  @Test
  void should_start_empty_when_file_missing() {
    // When
    AliasStore unit = new AliasStore(tempDir.resolve("does-not-exist.properties"));

    // Then
    assertThat(unit.asMap()).isEmpty();
  }

  @Test
  void should_round_trip_backslashes_when_saving() throws Exception {
    // Given
    Path file = tempDir.resolve("aliases.properties");
    AliasStore unit = new AliasStore(file);

    // When
    unit.put("weird", "value\\with\\backslashes");

    // Then
    assertThat(new AliasStore(file).resolve("weird")).isEqualTo("value\\with\\backslashes");
  }
}
