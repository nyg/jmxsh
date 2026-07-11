package sh.jmx.jmxsh.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import javax.management.remote.JMXServiceURL;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sh.jmx.jmxsh.Connection;
import sh.jmx.jmxsh.Session;

@ExtendWith(MockitoExtension.class)
class PromptTemplateTest {

  @Mock
  private Session session;

  @Test
  void optionalBlockHiddenWhenAllVarsEmpty() {
    when(session.isConnected()).thenReturn(false);
    assertThat(PromptTemplate.resolve("{?[{server}] }> ", session)).isEqualTo("> ");
  }

  @Test
  void optionalBlockShownWhenVarNonEmpty() throws Exception {
    when(session.isConnected()).thenReturn(true);
    when(session.getConnection())
        .thenReturn(new Connection(null, new JMXServiceURL("service:jmx:jmxmp://srv:9010")));
    assertThat(PromptTemplate.resolve("{?[{server}] }> ", session)).isEqualTo("[srv:9010] > ");
  }

  @Test
  void optionalBlocksComposed() throws Exception {
    when(session.isConnected()).thenReturn(true);
    when(session.getConnection())
        .thenReturn(new Connection(null, new JMXServiceURL("service:jmx:jmxmp://srv:9010")));
    when(session.getDomain()).thenReturn("java.lang");
    when(session.getBean()).thenReturn("java.lang:type=Memory");
    assertThat(PromptTemplate.resolve("{?[{server}] }{?{domain}}{?/{bean}}> ", session))
        .isEqualTo("[srv:9010] java.lang/java.lang:type=Memory> ");
  }

  @Test
  void optionalBlockPartiallyEmptyStillShown() {
    when(session.isConnected()).thenReturn(false);
    when(session.getDomain()).thenReturn("java.lang");
    when(session.getBean()).thenReturn(null);
    // {?[{server}] } hidden (server empty), {?{domain}} shown, {?/{bean}} hidden (bean empty)
    assertThat(PromptTemplate.resolve("{?[{server}] }{?{domain}}{?/{bean}}> ", session))
        .isEqualTo("java.lang> ");
  }

  @Test
  void unclosedOptionalBlockTreatedAsLiteral() {
    when(session.isConnected()).thenReturn(false);
    when(session.getDomain()).thenReturn(null);
    when(session.getBean()).thenReturn(null);
    // unclosed {? — the {? characters are emitted literally
    assertThat(PromptTemplate.resolve("{?[{server}]> ", session)).isEqualTo("{?[]> ");
  }

  @Test
  void nullSessionReturnsTemplateUnchanged() {
    assertThat(PromptTemplate.resolve("[{server}]> ", null)).isEqualTo("[{server}]> ");
  }

  @Test
  void staticPromptUnchanged() {
    assertThat(PromptTemplate.resolve("> ", session)).isEqualTo("> ");
  }

  @Test
  void serverVariableEmptyWhenNotConnected() {
    when(session.isConnected()).thenReturn(false);
    assertThat(PromptTemplate.resolve("[{server}]> ", session)).isEqualTo("[]> ");
  }

  @Test
  void serverVariableHostPort() throws Exception {
    when(session.isConnected()).thenReturn(true);
    when(session.getConnection())
        .thenReturn(new Connection(null, new JMXServiceURL("service:jmx:jmxmp://myhost:1234")));
    assertThat(PromptTemplate.resolve("[{server}]> ", session)).isEqualTo("[myhost:1234]> ");
  }

  @Test
  void serverVariableFallsBackToFullUrlWhenNoHost() throws Exception {
    when(session.isConnected()).thenReturn(true);
    JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:9010/jmxrmi");
    when(session.getConnection()).thenReturn(new Connection(null, url));
    assertThat(PromptTemplate.resolve("[{server}]> ", session))
        .isEqualTo("[" + url.toString() + "]> ");
  }

  @Test
  void domainVariableEmptyWhenNotSet() {
    when(session.getDomain()).thenReturn(null);
    assertThat(PromptTemplate.resolve("{domain}> ", session)).isEqualTo("> ");
  }

  @Test
  void domainVariableResolved() {
    when(session.getDomain()).thenReturn("java.lang");
    assertThat(PromptTemplate.resolve("{domain}> ", session)).isEqualTo("java.lang> ");
  }

  @Test
  void beanVariableEmptyWhenNotSet() {
    when(session.getBean()).thenReturn(null);
    assertThat(PromptTemplate.resolve("{bean}> ", session)).isEqualTo("> ");
  }

  @Test
  void beanVariableResolved() {
    when(session.getBean()).thenReturn("type=Memory");
    assertThat(PromptTemplate.resolve("{bean}> ", session)).isEqualTo("type=Memory> ");
  }

  @Test
  void unknownVariableLeftAsLiteral() {
    assertThat(PromptTemplate.resolve("{unknown}> ", session)).isEqualTo("{unknown}> ");
  }

  @Test
  void unclosedVariableLeftAsLiteral() {
    assertThat(PromptTemplate.resolve("{server", session)).isEqualTo("{server");
  }

  @Test
  void allVariablesResolved() throws Exception {
    when(session.isConnected()).thenReturn(true);
    when(session.getConnection())
        .thenReturn(new Connection(null, new JMXServiceURL("service:jmx:jmxmp://srv:5000")));
    when(session.getDomain()).thenReturn("java.lang");
    when(session.getBean()).thenReturn("type=Memory");
    assertThat(PromptTemplate.resolve("[{server} {domain}/{bean}]> ", session))
        .isEqualTo("[srv:5000 java.lang/type=Memory]> ");
  }
}
