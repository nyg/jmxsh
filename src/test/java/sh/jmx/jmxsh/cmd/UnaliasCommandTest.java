package sh.jmx.jmxsh.cmd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.io.StringWriter;
import java.util.List;
import java.util.TreeSet;

import sh.jmx.jmxsh.Session;
import sh.jmx.jmxsh.io.WriterCommandOutput;
import sh.jmx.jmxsh.utils.AliasStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UnaliasCommandTest {

  @Mock
  private Session session;

  @Mock
  private AliasStore aliasStore;

  @Test
  void should_remove_alias_when_defined() throws Exception {
    // Given
    UnaliasCommand unit = new UnaliasCommand();
    StringWriter writer = new StringWriter();
    when(session.getOutput()).thenReturn(new WriterCommandOutput(writer));
    when(session.getAliasStore()).thenReturn(aliasStore);
    when(aliasStore.remove("my_server")).thenReturn(true);
    unit.setSession(session);
    unit.setName("my_server");

    // When
    unit.execute();

    // Then
    assertThat(writer).hasToString("Alias my_server is removed");
  }

  @Test
  void should_throw_when_alias_unknown() throws Exception {
    // Given
    UnaliasCommand unit = new UnaliasCommand();
    when(session.getAliasStore()).thenReturn(aliasStore);
    when(aliasStore.remove("nope")).thenReturn(false);
    unit.setSession(session);
    unit.setName("nope");

    // When / Then
    assertThatThrownBy(unit::execute)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Alias nope is not defined");
  }

  @Test
  void should_suggest_alias_names_when_completing() {
    // Given
    UnaliasCommand unit = new UnaliasCommand();
    when(session.getAliasStore()).thenReturn(aliasStore);
    when(aliasStore.names()).thenReturn(new TreeSet<>(List.of("a", "b")));
    unit.setSession(session);

    // When
    List<String> suggestions = unit.doSuggestArgument();

    // Then
    assertThat(suggestions).containsExactly("a", "b");
  }
}
