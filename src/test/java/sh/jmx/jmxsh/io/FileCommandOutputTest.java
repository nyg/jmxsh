package sh.jmx.jmxsh.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FileCommandOutputTest {
  private Path testFile;

  /** prepare for test output file */
  @BeforeEach
  void setUpTestFile() {
    testFile = Path.of(
        System.getProperty("java.io.tmpdir"),
        "test-" + randomAlphabetic(20) + ".txt");
  }

  /**
   * Delete test file
   *
   * @throws IOException If file operation fails
   */
  @AfterEach
  void tearDownTestFile() throws IOException {
    Files.deleteIfExists(testFile);
  }

  /**
   * Writes out some output and verify result
   *
   * @throws IOException If file IO fails
   */
  @Test
  void write() throws Exception {
    FileCommandOutput output = new FileCommandOutput(testFile, false);
    output.println("helloworld");
    output.printMessage("say hello");
    output.close();

    assertThat(Files.readString(testFile, StandardCharsets.UTF_8).trim())
        .isEqualTo("helloworld");
  }

  /**
   * Writes out some output and verify result
   *
   * @throws IOException If file IO fails
   */
  @Test
  void writeMultipleTimes() throws Exception {
    FileCommandOutput output = new FileCommandOutput(testFile, false);
    output.println("helloworld");
    output.printMessage("say hello");
    output.close();

    FileCommandOutput output2 = new FileCommandOutput(testFile, true);
    output2.println("helloworld2");
    output2.printMessage("say hello2");
    output2.close();

    assertThat(Files.readString(testFile, StandardCharsets.UTF_8).trim())
        .isEqualTo("helloworld" + System.lineSeparator() + "helloworld2");
  }

  private static String randomAlphabetic(int length) {
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append((char) ('a' + ThreadLocalRandom.current().nextInt(26)));
    }
    return sb.toString();
  }
}
