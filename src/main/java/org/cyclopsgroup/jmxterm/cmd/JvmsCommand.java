package org.cyclopsgroup.jmxterm.cmd;

import java.io.IOException;
import java.util.List;

import javax.management.JMException;

import org.cyclopsgroup.jmxterm.Command;
import org.cyclopsgroup.jmxterm.JavaProcess;
import org.cyclopsgroup.jmxterm.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine;
import picocli.CommandLine.Option;

/**
 * Command to list all running local JVM processes
 *
 * @author <a href="mailto:jiaqi.guo@gmail.com">Jiaqi Guo</a>
 */
@CommandLine.Command(name = "jvms", description = "List all running local JVM processes")
public class JvmsCommand extends Command {
  private static final Logger LOG = LoggerFactory.getLogger(JvmsCommand.class);
  private boolean pidOnly;

  @Override
  public void execute() throws IOException, JMException {
    Session session = getSession();
    List<JavaProcess> processList = session.getProcessManager().list();
    LOG.debug("found {} running JVM processes", processList.size());
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

  /** @param pidOnly Flag to notify command to only print out PID instead of more details */
  @Option(names = {"-p", "--pidonly"}, description = "Only print out PID")
  public final void setPidOnly(boolean pidOnly) {
    this.pidOnly = pidOnly;
  }
}
