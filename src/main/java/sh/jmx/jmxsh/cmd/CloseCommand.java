package sh.jmx.jmxsh.cmd;

import java.io.IOException;
import sh.jmx.jmxsh.Command;

import picocli.CommandLine;
import lombok.extern.slf4j.Slf4j;

/**
 * Command to close current connection
 *
 */
@CommandLine.Command(name = "close", description = "Close current JMX connection")
@Slf4j
public class CloseCommand extends Command {
  @Override
  public void execute() throws IOException {
    log.info("closing JMX connection");
    getSession().disconnect();
    getSession().getOutput().printMessage("disconnected");
  }
}
