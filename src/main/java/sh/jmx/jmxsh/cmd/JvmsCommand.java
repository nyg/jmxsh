package sh.jmx.jmxsh.cmd;

import java.io.IOException;
import java.util.List;

import javax.management.JMException;

import sh.jmx.jmxsh.Command;
import sh.jmx.jmxsh.Session;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import lombok.extern.slf4j.Slf4j;
import sh.jmx.jmxsh.attach.JavaProcess;

@CommandLine.Command(name = "jvms", description = "List all running local JVM processes")
@Slf4j
public class JvmsCommand extends Command {
  private boolean pidOnly;

  @Override
  public void execute() throws IOException, JMException {
    Session session = getSession();
    List<JavaProcess> processList = session.getProcessManager().list();
    log.debug("found {} running JVM processes", processList.size());
    for (JavaProcess p : processList) {
      if (pidOnly) {
        session.getOutput().println(String.valueOf(p.getProcessId()));
      } else {

        session.getOutput().println(
            "%-8d (%s) - %s".formatted(
                p.getProcessId(), p.isManageable() ? "m" : " ", p.getDisplayName()));
      }
    }
  }

  @Option(names = {"-p", "--pidonly"}, description = "Only print out PID")
  public final void setPidOnly(boolean pidOnly) {
    this.pidOnly = pidOnly;
  }
}
