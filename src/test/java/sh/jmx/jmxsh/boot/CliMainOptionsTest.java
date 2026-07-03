package sh.jmx.jmxsh.boot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class CliMainOptionsTest {

  @Test
  void setInputThrowsWhenNull() {
    CliMainOptions options = new CliMainOptions();
    assertThatThrownBy(() -> options.setInput(null)).isInstanceOf(NullPointerException.class);
  }

  @Test
  void setInputAcceptsExistingFile() {
    CliMainOptions options = new CliMainOptions();
    options.setInput("src/test/resources/testscript.jmx");
    assertThat(options.getInput()).isEqualTo("src/test/resources/testscript.jmx");
  }

  @Test
  void setOutputThrowsWhenNull() {
    CliMainOptions options = new CliMainOptions();
    assertThatThrownBy(() -> options.setOutput(null)).isInstanceOf(NullPointerException.class);
  }

  @Test
  void setOutputAcceptsValue() {
    CliMainOptions options = new CliMainOptions();
    options.setOutput("stdout");
    assertThat(options.getOutput()).isEqualTo("stdout");
  }

  @Test
  void setPasswordThrowsWhenNull() {
    CliMainOptions options = new CliMainOptions();
    assertThatThrownBy(() -> options.setPassword(null)).isInstanceOf(NullPointerException.class);
  }

  @Test
  void setPasswordAcceptsValue() {
    CliMainOptions options = new CliMainOptions();
    options.setPassword("secret");
    assertThat(options.getPassword()).isEqualTo("secret");
  }

  @Test
  void setUrlThrowsWhenNull() {
    CliMainOptions options = new CliMainOptions();
    assertThatThrownBy(() -> options.setUrl(null)).isInstanceOf(NullPointerException.class);
  }

  @Test
  void setUrlAcceptsValue() {
    CliMainOptions options = new CliMainOptions();
    options.setUrl("localhost:9991");
    assertThat(options.getUrl()).isEqualTo("localhost:9991");
  }

  @Test
  void setUserThrowsWhenNull() {
    CliMainOptions options = new CliMainOptions();
    assertThatThrownBy(() -> options.setUser(null)).isInstanceOf(NullPointerException.class);
  }

  @Test
  void setUserAcceptsValue() {
    CliMainOptions options = new CliMainOptions();
    options.setUser("admin");
    assertThat(options.getUser()).isEqualTo("admin");
  }
}
