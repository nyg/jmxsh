package sh.jmx.jmxsh.cmd;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import sh.jmx.jmxsh.Command;
import sh.jmx.jmxsh.Session;
import sh.jmx.jmxsh.utils.AliasStore;

import picocli.CommandLine;
import picocli.CommandLine.Parameters;
import lombok.extern.slf4j.Slf4j;

@CommandLine.Command(
    name = "alias",
    description = "Define, show or list connection aliases",
    footer =
        """
        Without arguments this command lists all aliases. With a name it shows that alias, \
        and with a name and a target it defines the alias and persists it to the aliases file. \
        A target is anything the open command accepts: a PID, <host>:<port>, jmxmp://<host>:<port> \
        or a full JMX service URL. Use "unalias <name>" to remove an alias. For example
         alias,
         alias my_server,
         alias my_server myserver:1234,
         alias my_process 5678""")
@Slf4j
public class AliasCommand extends Command {
  private String name;

  private String target;

  @Override
  public List<String> doSuggestArgument() {
    return List.copyOf(getSession().getAliasStore().names());
  }

  @Override
  public void execute() throws IOException {
    Session session = getSession();
    AliasStore aliasStore = session.getAliasStore();
    if (name == null) {
      if (aliasStore.asMap().isEmpty()) {
        session.getOutput().printMessage("no aliases defined");
        return;
      }
      for (Map.Entry<String, String> entry : aliasStore.asMap().entrySet()) {
        session.getOutput().println("%s = %s".formatted(entry.getKey(), entry.getValue()));
      }
      return;
    }
    if (target == null) {
      String value = aliasStore.asMap().get(name);
      if (value == null) {
        throw new IllegalArgumentException("Alias %s is not defined".formatted(name));
      }
      session.getOutput().println("%s = %s".formatted(name, value));
      return;
    }
    aliasStore.put(name, target);
    log.debug("defined alias {} = {}", name, target);
    session.getOutput().printMessage("Alias %s is set to %s".formatted(name, target));
  }

  @Parameters(index = "0", paramLabel = "name", description = "Name of the alias", arity = "0..1")
  public final void setName(String name) {
    this.name = name;
  }

  @Parameters(
      index = "1",
      paramLabel = "target",
      description = "Connection target: PID, <host>:<port>, jmxmp://<host>:<port> or JMX service URL",
      arity = "0..1")
  public final void setTarget(String target) {
    this.target = target;
  }
}
