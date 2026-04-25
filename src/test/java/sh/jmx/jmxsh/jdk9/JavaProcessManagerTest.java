package sh.jmx.jmxsh.jdk9;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import sh.jmx.jmxsh.attach.JavaProcess;
import sh.jmx.jmxsh.attach.JavaProcessManager;

class JavaProcessManagerTest {

  @Test
  void construction() {
    JavaProcessManager jpm = new JavaProcessManager();
    List<JavaProcess> ps = jpm.list();
    assertThat(ps).isNotEmpty();
  }
}
