package org.cyclopsgroup.jmxterm.cmd;

import java.io.IOException;
import org.cyclopsgroup.jmxterm.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine;

/**
 * Command to close current connection
 *
 * @author <a href="mailto:jiaqi.guo@gmail.com">Jiaqi Guo</a>
 */
@CommandLine.Command(name = "close", description = "Close current JMX connection")
public class CloseCommand extends Command {
  private static final Logger LOG = LoggerFactory.getLogger(CloseCommand.class);
  @Override
  public void execute() throws IOException {
    LOG.info("closing JMX connection");
    getSession().disconnect();
    getSession().getOutput().printMessage("disconnected");
  }
}
