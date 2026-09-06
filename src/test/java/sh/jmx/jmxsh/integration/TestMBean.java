package sh.jmx.jmxsh.integration;

import java.time.Instant;

/**
 * MBean interface for integration testing. Exposes readable/writable attributes and invocable
 * operations with various parameter and return types.
 */
public interface TestMBean {
  String getName();

  void setName(String name);

  int getCount();

  String echo(String input);

  int add(int a, int b);

  String at(Instant when);

  int sum(int[] values);

  void reset();
}
