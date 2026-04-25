package sh.jmx.jmxsh.cc;

import static org.assertj.core.api.Assertions.assertThat;

import sh.jmx.jmxsh.cmd.QuitCommand;
import org.junit.jupiter.api.Test;

class PredefinedCommandFactoryTest {

  @Test
  void construction() {
    PredefinedCommandFactory f = new PredefinedCommandFactory();
    assertThat(f.getCommandTypes()).containsKey("help");
    assertThat(f.getCommandTypes()).containsKey("open");
    assertThat(f.getCommandTypes()).containsKey("close");
    assertThat(f.getCommandTypes()).containsKey("quit");
    assertThat(f.getCommandTypes()).containsKey("beans");
    assertThat(f.createCommand("help")).isInstanceOf(HelpCommand.class);
  }

  @Test
  void aliasesResolveToSameCommand() {
    PredefinedCommandFactory f = new PredefinedCommandFactory();
    assertThat(f.getCommandTypes()).containsKey("exit");
    assertThat(f.getCommandTypes()).containsKey("bye");
    assertThat(f.getCommandTypes().get("exit")).isEqualTo(QuitCommand.class);
    assertThat(f.getCommandTypes().get("bye")).isEqualTo(QuitCommand.class);
    assertThat(f.getCommandTypes().get("quit")).isEqualTo(QuitCommand.class);
  }
}
