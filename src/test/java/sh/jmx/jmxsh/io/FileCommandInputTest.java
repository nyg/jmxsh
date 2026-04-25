package sh.jmx.jmxsh.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

/**
 * Test case of {@link FileCommandInput}
 *
 */
class FileCommandInputTest {
  /**
   * Read commands from given test text file and verify result
   *
   * @throws IOException If file IO is failed
   */
  @Test
  void read() throws Exception {
    Path testFile = Path.of("src/test/resources/testscript.jmx");
    try(FileCommandInput input = new FileCommandInput(testFile)) {
      assertThat(input.readLine()).isEqualTo("beans");
      assertThat(input.readLine()).isEqualTo("exit");
      assertThat(input.readLine()).isNull();
    }
  }
}
