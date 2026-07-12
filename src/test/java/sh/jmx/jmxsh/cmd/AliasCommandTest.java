package sh.jmx.jmxsh.cmd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import sh.jmx.jmxsh.Session;
import sh.jmx.jmxsh.io.WriterCommandOutput;
import sh.jmx.jmxsh.utils.AliasStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AliasCommandTest {

  @Mock
  private Session session;

  @Mock
  private AliasStore aliasStore;

  @Test
  void should_list_aliases_sorted_when_no_arguments() throws Exception {
    // Given
    AliasCommand unit = new AliasCommand();
    StringWriter writer = new StringWriter();
    when(session.getOutput()).thenReturn(new WriterCommandOutput(writer));
    when(session.getAliasStore()).thenReturn(aliasStore);
    TreeMap<String, String> aliases = new TreeMap<>();
    aliases.put("zebra", "zhost:1");
    aliases.put("apple", "ahost:2");
    when(aliasStore.asMap()).thenReturn(Collections.unmodifiableSortedMap(aliases));
    unit.setSession(session);

    // When
    unit.execute();

    // Then
    assertThat(writer)
        .hasToString(
            "apple = ahost:2" + System.lineSeparator() + "zebra = zhost:1" + System.lineSeparator());
  }

  @Test
  void should_print_message_when_no_aliases_defined() throws Exception {
    // Given
    AliasCommand unit = new AliasCommand();
    StringWriter writer = new StringWriter();
    when(session.getOutput()).thenReturn(new WriterCommandOutput(writer));
    when(session.getAliasStore()).thenReturn(aliasStore);
    when(aliasStore.asMap()).thenReturn(Collections.emptySortedMap());
    unit.setSession(session);

    // When
    unit.execute();

    // Then
    assertThat(writer).hasToString("No aliases defined." + System.lineSeparator());
  }

  @Test
  void should_show_alias_when_name_given() throws Exception {
    // Given
    AliasCommand unit = new AliasCommand();
    StringWriter writer = new StringWriter();
    when(session.getOutput()).thenReturn(new WriterCommandOutput(writer));
    when(session.getAliasStore()).thenReturn(aliasStore);
    TreeMap<String, String> aliases = new TreeMap<>();
    aliases.put("my_server", "myserver:1234");
    when(aliasStore.asMap()).thenReturn(Collections.unmodifiableSortedMap(aliases));
    unit.setSession(session);
    unit.setName("my_server");

    // When
    unit.execute();

    // Then
    assertThat(writer).hasToString("my_server = myserver:1234" + System.lineSeparator());
  }

  @Test
  void should_throw_when_shown_alias_unknown() {
    // Given
    AliasCommand unit = new AliasCommand();
    when(session.getAliasStore()).thenReturn(aliasStore);
    when(aliasStore.asMap()).thenReturn(Collections.emptySortedMap());
    unit.setSession(session);
    unit.setName("nope");

    // When / Then
    assertThatThrownBy(unit::execute)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Alias nope is not defined");
  }

  @Test
  void should_define_alias_when_name_and_target_given() throws Exception {
    // Given
    AliasCommand unit = new AliasCommand();
    StringWriter writer = new StringWriter();
    when(session.getOutput()).thenReturn(new WriterCommandOutput(writer));
    when(session.getAliasStore()).thenReturn(aliasStore);
    unit.setSession(session);
    unit.setName("my_server");
    unit.setTarget("myserver:1234");

    // When
    unit.execute();

    // Then
    verify(aliasStore).put("my_server", "myserver:1234");
    assertThat(writer).hasToString("Alias my_server is set to myserver:1234." + System.lineSeparator());
  }

  @Test
  void should_suggest_alias_names_when_completing() {
    // Given
    AliasCommand unit = new AliasCommand();
    when(session.getAliasStore()).thenReturn(aliasStore);
    when(aliasStore.names()).thenReturn(new TreeSet<>(List.of("a", "b")));
    unit.setSession(session);

    // When
    List<String> suggestions = unit.doSuggestArgument();

    // Then
    assertThat(suggestions).containsExactly("a", "b");
  }
}
