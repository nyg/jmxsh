package sh.jmx.jmxsh.jdk9;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import sh.jmx.jmxsh.JavaProcess;
import org.junit.jupiter.api.Test;

/**
 * Test case of {@link Jdk9JavaProcessManager}
 *
 */
class Jdk9JavaProcessManagerTest {

  @Test
  void construction() {
    Jdk9JavaProcessManager jpm = new Jdk9JavaProcessManager();
    List<JavaProcess> ps = jpm.list();
    assertThat(ps).isNotEmpty();
  }
}
