package sh.jmx.jmxsh.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * Unit test case for class {@link InputStreamCommandInput}
 *
 */
class InputStreamCommandInputTest {
  /**
   * Read from string line by line and verify result
   *
   * @throws IOException If stream operation fails
   */
  @Test
  void read() throws Exception {
    String input = "aaaa\nbbbb";
    InputStreamCommandInput in =
        new InputStreamCommandInput(new ByteArrayInputStream(input.getBytes()));
    assertThat(in.readLine()).isEqualTo("aaaa");
    assertThat(in.readLine()).isEqualTo("bbbb");
  }
}
