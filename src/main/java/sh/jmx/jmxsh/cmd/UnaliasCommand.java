package sh.jmx.jmxsh.cmd;

import java.io.IOException;
import java.util.List;

import sh.jmx.jmxsh.Command;
import sh.jmx.jmxsh.Session;

import picocli.CommandLine;
import picocli.CommandLine.Parameters;
import lombok.extern.slf4j.Slf4j;

@CommandLine.Command(
    name = "unalias",
    description = "Remove a connection alias",
    footer = "Removes an alias defined with the alias command. eg. unalias my_server")
@Slf4j
public class UnaliasCommand extends Command {
  private String name;

  @Override
  public List<String> doSuggestArgument() {
    return List.copyOf(getSession().getAliasStore().names());
  }

  @Override
  public void execute() throws IOException {
    Session session = getSession();
    if (!session.getAliasStore().remove(name)) {
      throw new IllegalArgumentException("Alias %s is not defined".formatted(name));
    }
    log.debug("removed alias {}", name);
    session.getOutput().printMessage("Alias %s is removed.".formatted(name));
  }

  @Parameters(paramLabel = "name", description = "Name of the alias to remove", arity = "1")
  public final void setName(String name) {
    this.name = name;
  }
}
