package org.cyclopsgroup.jmxterm.cc;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Test of {@link JPMFactory}
 *
 */
class JPMFactoryTest {
  /** Verify JPMFactory can create process manager */
  @Test
  void load() {
    assertThat(JPMFactory.createProcessManager()).isNotNull();
  }
}
