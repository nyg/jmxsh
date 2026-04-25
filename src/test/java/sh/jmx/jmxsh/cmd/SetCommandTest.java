package sh.jmx.jmxsh.cmd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import javax.management.Attribute;
import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import sh.jmx.jmxsh.Connection;
import sh.jmx.jmxsh.Session;
import sh.jmx.jmxsh.io.WriterCommandOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SetCommandTest {
  @Mock
  private Session session;
  @Mock
  private Connection connection;
  @Mock
  private MBeanServerConnection con;

  private SetCommand command;
  private StringWriter writer;

  /** Set up objects to test */
  @BeforeEach
  void setUp() throws IOException {
    command = new SetCommand();
    writer = new StringWriter();
    lenient().when(session.getOutput()).thenReturn(new WriterCommandOutput(writer, null));
    lenient().when(session.getConnection()).thenReturn(connection);
    lenient().when(connection.getServerConnection()).thenReturn(con);
  }

  private void setValueAndVerify(String expr, String type, Object expected) {
    command.setBean("a:type=x");
    command.setArguments(Arrays.asList("var", expr));

    MBeanInfo beanInfo = mock(MBeanInfo.class);
    MBeanAttributeInfo attributeInfo = mock(MBeanAttributeInfo.class);
    AtomicReference<Attribute> setAttribute = new AtomicReference<>();
    try {
      when(con.getMBeanInfo(new ObjectName("a:type=x"))).thenReturn(beanInfo);
      when(beanInfo.getAttributes()).thenReturn(new MBeanAttributeInfo[] {attributeInfo});
      when(attributeInfo.getName()).thenReturn("var");
      when(attributeInfo.getType()).thenReturn(type);
      when(attributeInfo.isWritable()).thenReturn(true);
      doAnswer(invocation -> {
        setAttribute.set(invocation.getArgument(1));
        return null;
      }).when(con).setAttribute(eq(new ObjectName("a:type=x")), any(Attribute.class));

      command.setSession(session);
      command.execute();
    } catch (IOException | JMException e) {
      throw new RuntimeException(e);
    }

    assertThat(setAttribute.get()).isNotNull();
    assertThat(setAttribute.get().getName()).isEqualTo("var");
    assertThat(setAttribute.get().getValue()).isEqualTo(expected);
  }

  /** Test setting an integer */
  @Test
  void executeNormally() {
    setValueAndVerify("33", "int", 33);
  }

  /** Test setting an empty string */
  @Test
  void executeWithAnEmptyString() {
    setValueAndVerify("\"\"", String.class.getName(), "");
  }

  /** Test setting string with control character */
  @Test
  void executeWithControlCharacter() {
    setValueAndVerify("hello\\n", String.class.getName(), "hello\n");
  }

  /** Test with negative number */
  @Test
  void executeWithNegativeNumber() {
    setValueAndVerify("-2", "int", -2);
  }

  /** Test setting NULL string */
  @Test
  void executeWithNullString() {
    setValueAndVerify("null", String.class.getName(), null);
  }

  /** Test with quoted negative number */
  @Test
  void executeWithQuotedNegativeNumber() {
    setValueAndVerify("\"-2\"", "int", -2);
  }
}
