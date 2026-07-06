package sh.jmx.jmxsh;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.management.remote.JMXServiceURL;

import sh.jmx.jmxsh.attach.JavaProcess;
import sh.jmx.jmxsh.attach.JavaProcessManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JmxUrlTest {

  @Mock
  private JavaProcessManager processManager;

  @Mock
  private JavaProcess process;

  @Test
  void should_build_rmi_url_when_input_is_host_and_port() throws IOException {
    // Given
    JmxUrl unit = JmxUrl.parse("xyz-host.cyclopsgroup.org:12345");

    // When
    JMXServiceURL url = unit.toServiceUrl(null);

    // Then
    assertThat(url.getURLPath()).isEqualTo("/jndi/rmi://xyz-host.cyclopsgroup.org:12345/jmxrmi");
  }

  @Test
  void should_keep_url_when_input_is_full_service_url() throws IOException {
    // Given
    JmxUrl unit = JmxUrl.parse("service:jmx:rmi:///jndi/rmi://xyz-host.cyclopsgroup.org:12345/jmxrmi");

    // When
    JMXServiceURL url = unit.toServiceUrl(null);

    // Then
    assertThat(url.getURLPath()).isEqualTo("/jndi/rmi://xyz-host.cyclopsgroup.org:12345/jmxrmi");
  }

  @ParameterizedTest
  @CsvSource({
      "jmxmp://localhost:9999, localhost, 9999",
      "jmxmp://my-host.example.com:5555, my-host.example.com, 5555",
      "service:jmx:jmxmp://localhost:9999, localhost, 9999"
  })
  void should_build_jmxmp_url_when_input_is_jmxmp_target(String input, String host, int port)
      throws IOException {
    // Given
    JmxUrl unit = JmxUrl.parse(input);

    // When
    JMXServiceURL url = unit.toServiceUrl(null);

    // Then
    assertThat(url.getProtocol()).isEqualTo("jmxmp");
    assertThat(url.getHost()).isEqualTo(host);
    assertThat(url.getPort()).isEqualTo(port);
  }

  @Test
  void should_classify_as_pid_when_input_is_digits() {
    // When
    JmxUrl unit = JmxUrl.parse("123");

    // Then
    assertThat(unit).isInstanceOf(JmxUrl.Pid.class);
  }

  @Test
  void should_throw_when_url_is_null() {
    // When / Then
    assertThatThrownBy(() -> JmxUrl.parse(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Empty URL is not allowed");
  }

  @Test
  void should_throw_when_url_is_empty() {
    // When / Then
    assertThatThrownBy(() -> JmxUrl.parse(""))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Empty URL is not allowed");
  }

  @Test
  void should_return_process_url_when_pid_is_manageable() throws IOException {
    // Given
    when(processManager.get(123)).thenReturn(process);
    when(process.isManageable()).thenReturn(true);
    when(process.toUrl()).thenReturn("service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi");
    JmxUrl unit = JmxUrl.parse("123");

    // When
    JMXServiceURL url = unit.toServiceUrl(processManager);

    // Then
    assertThat(url.getURLPath()).isEqualTo("/jndi/rmi://localhost:1099/jmxrmi");
  }

  @Test
  void should_start_management_agent_when_pid_is_not_manageable() throws IOException {
    // Given
    when(processManager.get(123)).thenReturn(process);
    when(process.isManageable()).thenReturn(false, true);
    when(process.toUrl()).thenReturn("service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi");
    JmxUrl unit = JmxUrl.parse("123");

    // When
    JMXServiceURL url = unit.toServiceUrl(processManager);

    // Then
    verify(process).startManagementAgent();
    assertThat(url.getURLPath()).isEqualTo("/jndi/rmi://localhost:1099/jmxrmi");
  }

  @Test
  void should_throw_when_pid_does_not_exist() {
    // Given
    when(processManager.get(123)).thenReturn(null);
    JmxUrl unit = JmxUrl.parse("123");

    // When / Then
    assertThatThrownBy(() -> unit.toServiceUrl(processManager))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("No such PID 123");
  }

  @Test
  void should_throw_when_management_agent_does_not_start() {
    // Given
    when(processManager.get(123)).thenReturn(process);
    when(process.isManageable()).thenReturn(false);
    JmxUrl unit = JmxUrl.parse("123");

    // When / Then
    assertThatThrownBy(() -> unit.toServiceUrl(processManager))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Managed agent for PID 123 couldn't start. PID 123 is not manageable");
  }

  @Test
  void should_throw_with_accepted_forms_when_pid_used_without_process_manager() {
    // Given
    JmxUrl unit = JmxUrl.parse("123");

    // When / Then
    assertThatThrownBy(() -> unit.toServiceUrl(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasCauseInstanceOf(MalformedURLException.class);
  }

  @Test
  void should_throw_with_accepted_forms_when_target_invalid() {
    // Given
    JmxUrl unit = JmxUrl.parse("myal");

    // When / Then
    assertThatThrownBy(() -> unit.toServiceUrl(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "Invalid connection target \"myal\". Accepted forms: a <PID>, <host>:<port>,"
                + " jmxmp://<host>:<port>, a full service:jmx:... URL, or an alias defined with"
                + " the alias command.");
  }
}
