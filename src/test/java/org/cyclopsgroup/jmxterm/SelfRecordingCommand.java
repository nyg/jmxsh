package org.cyclopsgroup.jmxterm;

import java.util.List;

import picocli.CommandLine;
import picocli.CommandLine.Parameters;

/**
 * A command for testing that records parameters passed in
 *
 */
@CommandLine.Command(name = "test", description = "desc")
public class SelfRecordingCommand extends Command {
  private List<String> arguments;

  private final List<Command> records;

  /** @param records List of commands that gets passed in */
  public SelfRecordingCommand(List<Command> records) {
    this.records = records;
  }

  @Override
  public void execute() {
    records.add(this);
  }

  /** @return Arguments */
  public String getArgs() {
    return String.join(" ", arguments);
  }

  /** @return Array of arguments */
  public List<String> getArguments() {
    return arguments;
  }

  /** @param arguments Arguments */
  @Parameters(arity = "0..*")
  public void setArguments(List<String> arguments) {
    this.arguments = arguments;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + ":" + getArgs();
  }
}
