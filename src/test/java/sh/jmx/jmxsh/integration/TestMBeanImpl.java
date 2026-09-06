package sh.jmx.jmxsh.integration;

import java.time.Instant;
import java.util.Arrays;

/** Implementation of {@link TestMBean} for integration testing. */
public class TestMBeanImpl implements TestMBean {
  private String name = "default";
  private int count = 0;

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
    this.count++;
  }

  @Override
  public int getCount() {
    return count;
  }

  @Override
  public String echo(String input) {
    return "echo:" + input;
  }

  @Override
  public int add(int a, int b) {
    return a + b;
  }

  @Override
  public String at(Instant when) {
    return "at:" + when;
  }

  @Override
  public int sum(int[] values) {
    return Arrays.stream(values).sum();
  }

  @Override
  public void reset() {
    name = "default";
    count = 0;
  }
}
