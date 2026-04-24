package sh.jmx.jmxsh.boot;

import picocli.CommandLine.IVersionProvider;

/** Reads the application version from the JAR manifest's Implementation-Version. */
public class VersionProvider implements IVersionProvider {

  @Override
  public String[] getVersion() {
    String version = getClass().getPackage().getImplementationVersion();
    return new String[] {"jmxsh " + (version != null ? version : "dev")};
  }
}
