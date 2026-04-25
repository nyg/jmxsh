package sh.jmx.jmxsh.cc;

import static org.assertj.core.api.Assertions.assertThat;
import static sh.jmx.jmxsh.cc.CommandCenter.ESCAPE_CHAR_REGEX;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import sh.jmx.jmxsh.Command;
import sh.jmx.jmxsh.SelfRecordingCommand;
import sh.jmx.jmxsh.io.WriterCommandOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CommandCenterTest {
  private CommandCenter cc;

  private List<Command> executedCommands;

  private StringWriter output;

  private String getArgsFromList(int index) {
    return getRecordedCommand(index).getArgs();
  }

  private SelfRecordingCommand getRecordedCommand(int index) {
    return (SelfRecordingCommand) executedCommands.get(index);
  }

  private void runCommandAndVerifyArguments(String command, List<String> expectedArguments) {
    cc.execute(command);
    assertThat(getRecordedCommand(0).getArguments()).isEqualTo(expectedArguments);
  }

  /**
   * Set up objects to test
   *
   * @throws IOException
   */
  @BeforeEach
  void setUp() {
    executedCommands = new ArrayList<>();
    output = new StringWriter();

    Map<String, Supplier<Command>> commandTypes = new HashMap<>();
    commandTypes.put("test", () -> new SelfRecordingCommand(executedCommands));
    cc =
        new CommandCenter(
            new WriterCommandOutput(output),
            null,
            new TypeMapCommandFactory(commandTypes));
  }

  /** Verify the execution */
  @Test
  void execute() {
    cc.execute("test 1");
    cc.execute("test 2 a b && test 3");
    cc.execute("# test 4");
    cc.execute("test 5 # test 6");

    assertThat(executedCommands).hasSize(4);
    assertThat(getArgsFromList(0)).isEqualTo("1");
    assertThat(getArgsFromList(1)).isEqualTo("2 a b");
    assertThat(getArgsFromList(2)).isEqualTo("3");
    assertThat(getArgsFromList(3)).isEqualTo("5");
  }

  @Test
  void multipleArguments() {
    runCommandAndVerifyArguments("test a b c d", Arrays.asList("a", "b", "c", "d"));
  }

  @Test
  void multipleEscapedArguments() {
    runCommandAndVerifyArguments("test a\\ \\ b \\-3\\ ,4", Arrays.asList("a  b", "-3 ,4"));
  }

  @Test
  void singleArgumentWithEscape() {
    runCommandAndVerifyArguments("test \\-1", Arrays.asList("-1"));
  }

  @Test
  void singleArgumentWithSpace() {
    runCommandAndVerifyArguments("test a\\ b\\ c\\ d", Arrays.asList("a b c d"));
  }

  @Test
  void singleSimpleArgument() {
    runCommandAndVerifyArguments("test 1", Arrays.asList("1"));
  }

  @Test
  void regexEscapesCorrectly() {
    final String s1 = "".split(ESCAPE_CHAR_REGEX)[0];
    final String s2 = "a b c".split(ESCAPE_CHAR_REGEX)[0];
    final String s3 = "a #b c".split(ESCAPE_CHAR_REGEX)[0];
    final String s4 = "a #b c #".split(ESCAPE_CHAR_REGEX)[0];
    final String s5 = "a \\#b c #".split(ESCAPE_CHAR_REGEX)[0];
    final String s6 = "a #b c \\# something".split(ESCAPE_CHAR_REGEX)[0];

    assertThat(s1).isEqualTo("");
    assertThat(s2).isEqualTo("a b c");
    assertThat(s3).isEqualTo("a ");
    assertThat(s4).isEqualTo("a ");
    assertThat(s5).isEqualTo("a \\#b c ");
    assertThat(s6).isEqualTo("a ");
  }
}
