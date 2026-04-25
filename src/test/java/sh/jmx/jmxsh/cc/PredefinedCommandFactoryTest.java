package sh.jmx.jmxsh.cc;

import static org.assertj.core.api.Assertions.assertThat;

import sh.jmx.jmxsh.cmd.QuitCommand;
import org.junit.jupiter.api.Test;

class PredefinedCommandFactoryTest {

  @Test
  void construction() {
    PredefinedCommandFactory f = new PredefinedCommandFactory();
    assertThat(f.getCommandNames()).contains("help", "open", "close", "quit", "beans");
    assertThat(f.createCommand("help")).isInstanceOf(HelpCommand.class);
  }

  @Test
  void aliasesResolveToSameCommand() {
    PredefinedCommandFactory f = new PredefinedCommandFactory();
    assertThat(f.getCommandNames()).contains("exit", "bye", "quit");
    assertThat(f.createCommand("exit")).isInstanceOf(QuitCommand.class);
    assertThat(f.createCommand("bye")).isInstanceOf(QuitCommand.class);
    assertThat(f.createCommand("quit")).isInstanceOf(QuitCommand.class);
  }
}
