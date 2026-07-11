package sh.jmx.jmxsh.cc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import sh.jmx.jmxsh.Command;
import sh.jmx.jmxsh.io.WriterCommandOutput;
import org.jline.reader.Candidate;
import org.jline.reader.ParsedLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import picocli.CommandLine;
import picocli.CommandLine.Option;

@ExtendWith(MockitoExtension.class)
class ConsoleCompleterTest {
  @Mock
  private CommandCenter commandCenter;

  @Test
  void constructorThrowsWhenCommandCenterNull() {
    assertThatThrownBy(() -> new ConsoleCompleter(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void constructorSucceedsWithCommandCenter() {
    when(commandCenter.getCommandNames()).thenReturn(new HashSet<>(List.of("a", "b")));
    ConsoleCompleter completer = new ConsoleCompleter(commandCenter);
    assertThat(completer).isNotNull();
  }

  @CommandLine.Command(name = "test", description = "desc")
  static class CompletableTestCommand extends Command {
    @Option(names = {"-b", "--bean"})
    private String bean;

    @Option(names = {"--count"})
    private int count;

    @Override
    public void execute() {
      throw new UnsupportedOperationException("not used in completion tests");
    }

    @Override
    protected List<String> doSuggestArgument() {
      return List.of("argA", "argB");
    }

    @Override
    protected List<String> doSuggestOption(String optionName) {
      return List.of("valX", "valY");
    }
  }

  private ConsoleCompleter completer;

  @BeforeEach
  void setUpCompleter() {
    Map<String, Supplier<Command>> commandTypes = new HashMap<>();
    commandTypes.put("test", CompletableTestCommand::new);
    CommandCenter realCenter =
        new CommandCenter(new WriterCommandOutput(new StringWriter()), null,
            new TypeMapCommandFactory(commandTypes));
    completer = new ConsoleCompleter(realCenter);
  }

  private List<String> complete(List<String> words, int wordIndex, String word) {
    ParsedLine line = mock(ParsedLine.class);
    lenient().when(line.words()).thenReturn(words);
    lenient().when(line.wordIndex()).thenReturn(wordIndex);
    lenient().when(line.word()).thenReturn(word);
    List<Candidate> candidates = new ArrayList<>();
    completer.complete(null, line, candidates);
    return candidates.stream().map(Candidate::value).toList();
  }

  @Test
  void completesCommandNamesWhenAtFirstWord() {
    assertThat(complete(List.of(""), 0, "")).containsExactly("test");
  }

  @Test
  void filtersCommandNamesByPrefix() {
    assertThat(complete(List.of("te"), 0, "te")).containsExactly("test");
    assertThat(complete(List.of("zzz"), 0, "zzz")).isEmpty();
  }

  @Test
  void completesOptionNames() {
    assertThat(complete(List.of("test", "-"), 1, "-"))
        .contains("-b", "--bean", "--count", "-h", "--help");
  }

  @Test
  void completesOptionValueForShortOption() {
    assertThat(complete(List.of("test", "-b", ""), 2, "")).containsExactly("valX", "valY");
  }

  @Test
  void completesOptionValueForLongOnlyOption() {
    assertThat(complete(List.of("test", "--count", ""), 2, "")).containsExactly("valX", "valY");
  }

  @Test
  void booleanOptionFallsBackToArgumentSuggestions() {
    assertThat(complete(List.of("test", "-h", ""), 2, "")).containsExactly("argA", "argB");
  }

  @Test
  void completesArgumentsAndFiltersByPrefix() {
    assertThat(complete(List.of("test", ""), 1, "")).containsExactly("argA", "argB");
    assertThat(complete(List.of("test", "argA"), 1, "argA")).containsExactly("argA");
  }

  @Test
  void unknownCommandIsSilentlyIgnored() {
    assertThat(complete(List.of("nope", ""), 1, "")).isEmpty();
  }
}
