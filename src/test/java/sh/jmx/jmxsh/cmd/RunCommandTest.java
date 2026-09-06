package sh.jmx.jmxsh.cmd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.StringWriter;
import java.util.List;

import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
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
class RunCommandTest {
  @Mock
  private Session session;
  @Mock
  private Connection connection;
  @Mock
  private MBeanServerConnection con;

  private RunCommand command;
  private StringWriter writer;

  /** Setup objects to test */
  @BeforeEach
  void setUp() throws Exception {
    command = new RunCommand();
    writer = new StringWriter();
    lenient().when(session.getOutput()).thenReturn(new WriterCommandOutput(writer, null));
    lenient().when(session.getConnection()).thenReturn(connection);
    lenient().when(connection.getServerConnection()).thenReturn(con);
    lenient().when(con.isRegistered(new ObjectName("a:type=x"))).thenReturn(true);
  }

  /** @throws Exception */
  @Test
  void executeNormally() throws Exception {
    command.setBean("a:type=x");
    command.setParameters(List.of("exe", "33"));

    MBeanInfo beanInfo = mock(MBeanInfo.class);
    MBeanOperationInfo opInfo = mock(MBeanOperationInfo.class);
    MBeanParameterInfo paramInfo = mock(MBeanParameterInfo.class);
    when(con.getMBeanInfo(new ObjectName("a:type=x"))).thenReturn(beanInfo);
    when(beanInfo.getOperations()).thenReturn(new MBeanOperationInfo[] {opInfo});
    when(opInfo.getName()).thenReturn("exe");
    when(opInfo.getSignature()).thenReturn(new MBeanParameterInfo[] {paramInfo});
    when(paramInfo.getType()).thenReturn("int");
    when(con.invoke(new ObjectName("a:type=x"), "exe", new Object[] {33}, new String[] {"int"}))
        .thenReturn("bingo");
    command.setSession(session);
    command.execute();
    assertThat(writer.toString().trim()).isEqualTo("bingo");
  }

  /** @throws Exception */
  @Test
  void executeOverloadedMethod() throws Exception {
    command.setBean("a:type=x");
    command.setTypes("java.lang.String");
    command.setParameters(List.of("exe", "33"));

    MBeanInfo beanInfo = mock(MBeanInfo.class);
    MBeanOperationInfo opInfo1 = mock(MBeanOperationInfo.class);
    MBeanParameterInfo paramInfoInt = mock(MBeanParameterInfo.class);
    MBeanOperationInfo opInfo2 = mock(MBeanOperationInfo.class);
    MBeanParameterInfo paramInfoString = mock(MBeanParameterInfo.class);
    when(con.getMBeanInfo(new ObjectName("a:type=x"))).thenReturn(beanInfo);
    when(beanInfo.getOperations()).thenReturn(new MBeanOperationInfo[] {opInfo1, opInfo2});
    // exe <int>
    when(opInfo1.getName()).thenReturn("exe");
    when(opInfo1.getSignature()).thenReturn(new MBeanParameterInfo[] {paramInfoInt});
    when(paramInfoInt.getType()).thenReturn("int");
    // exe <java.lang.String>
    when(opInfo2.getName()).thenReturn("exe");
    when(opInfo2.getSignature()).thenReturn(new MBeanParameterInfo[] {paramInfoString});
    when(paramInfoString.getType()).thenReturn("java.lang.String");
    when(con.invoke(
            new ObjectName("a:type=x"),
            "exe",
            new Object[] {"33"},
            new String[] {"java.lang.String"}))
        .thenReturn("bingo-string");
    command.setSession(session);
    command.execute();
    verify(con, never())
        .invoke(new ObjectName("a:type=x"), "exe", new Object[] {33}, new String[] {"int"});
    assertThat(writer.toString().trim()).isEqualTo("bingo-string");
  }

  @Test
  void should_print_invocation_latency_when_measure_is_enabled() throws Exception {
    // Given
    RunCommand unit = new RunCommand();
    StringWriter messageWriter = new StringWriter();
    when(session.getOutput()).thenReturn(new WriterCommandOutput(messageWriter));
    unit.setBean("a:type=x");
    unit.setMeasure(true);
    unit.setParameters(List.of("exe"));
    MBeanInfo beanInfo = mock(MBeanInfo.class);
    MBeanOperationInfo opInfo = mock(MBeanOperationInfo.class);
    when(con.getMBeanInfo(new ObjectName("a:type=x"))).thenReturn(beanInfo);
    when(beanInfo.getOperations()).thenReturn(new MBeanOperationInfo[] {opInfo});
    when(opInfo.getName()).thenReturn("exe");
    when(opInfo.getSignature()).thenReturn(new MBeanParameterInfo[0]);
    when(con.invoke(new ObjectName("a:type=x"), "exe", new Object[0], new String[0]))
        .thenReturn("bingo");
    unit.setSession(session);

    // When
    unit.execute();

    // Then
    assertThat(messageWriter.toString()).contains("Invocation took").contains("ms.");
  }

  @Test
  void suggestArgumentWithNoBean() {
    command.setSession(session);
    assertThat(command.suggestArgument(null)).isEmpty();
  }

  @Test
  void setParametersThrowsWhenNull() {
    assertThatThrownBy(() -> command.setParameters(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void executeThrowsWhenTypeCountDoesNotMatchParameters() {
    command.setBean("a:type=x");
    command.setParameters(List.of("exe", "33"));
    command.setTypes("int,long");
    command.setSession(session);

    assertThatThrownBy(command::execute)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Signature does not match parameter count");
  }

  @Test
  void should_invoke_operation_when_parameters_are_a_json_array() throws Exception {
    // Given
    RunCommand unit = new RunCommand();
    unit.setBean("a:type=x");
    unit.setJson("[33]");
    unit.setParameters(List.of("exe"));
    MBeanInfo beanInfo = mock(MBeanInfo.class);
    MBeanOperationInfo opInfo = mock(MBeanOperationInfo.class);
    MBeanParameterInfo paramInfo = mock(MBeanParameterInfo.class);
    when(con.getMBeanInfo(new ObjectName("a:type=x"))).thenReturn(beanInfo);
    when(beanInfo.getOperations()).thenReturn(new MBeanOperationInfo[] {opInfo});
    when(opInfo.getName()).thenReturn("exe");
    when(opInfo.getSignature()).thenReturn(new MBeanParameterInfo[] {paramInfo});
    when(paramInfo.getType()).thenReturn("int");
    when(con.invoke(new ObjectName("a:type=x"), "exe", new Object[] {33}, new String[] {"int"}))
        .thenReturn("bingo");
    unit.setSession(session);

    // When
    unit.execute();

    // Then
    assertThat(writer.toString().trim()).isEqualTo("bingo");
  }

  @Test
  void should_invoke_operation_when_parameters_are_a_json_object() throws Exception {
    // Given
    RunCommand unit = new RunCommand();
    unit.setBean("a:type=x");
    unit.setJson("{\"p1\": 33}");
    unit.setParameters(List.of("exe"));
    MBeanInfo beanInfo = mock(MBeanInfo.class);
    MBeanOperationInfo opInfo = mock(MBeanOperationInfo.class);
    MBeanParameterInfo paramInfo = mock(MBeanParameterInfo.class);
    when(con.getMBeanInfo(new ObjectName("a:type=x"))).thenReturn(beanInfo);
    when(beanInfo.getOperations()).thenReturn(new MBeanOperationInfo[] {opInfo});
    when(opInfo.getName()).thenReturn("exe");
    when(opInfo.getSignature()).thenReturn(new MBeanParameterInfo[] {paramInfo});
    when(paramInfo.getName()).thenReturn("p1");
    when(paramInfo.getType()).thenReturn("int");
    when(con.invoke(new ObjectName("a:type=x"), "exe", new Object[] {33}, new String[] {"int"}))
        .thenReturn("bingo");
    unit.setSession(session);

    // When
    unit.execute();

    // Then
    assertThat(writer.toString().trim()).isEqualTo("bingo");
  }

  @Test
  void should_list_known_signatures_when_json_object_keys_do_not_match_parameter_names()
      throws Exception {
    // Given
    RunCommand unit = new RunCommand();
    unit.setBean("a:type=x");
    unit.setJson("{\"a\": 3}");
    unit.setParameters(List.of("exe"));
    MBeanInfo beanInfo = mock(MBeanInfo.class);
    MBeanOperationInfo opInfo = mock(MBeanOperationInfo.class);
    MBeanParameterInfo paramInfo = mock(MBeanParameterInfo.class);
    when(con.getMBeanInfo(new ObjectName("a:type=x"))).thenReturn(beanInfo);
    when(beanInfo.getOperations()).thenReturn(new MBeanOperationInfo[] {opInfo});
    when(opInfo.getName()).thenReturn("exe");
    when(opInfo.getSignature()).thenReturn(new MBeanParameterInfo[] {paramInfo});
    when(paramInfo.getName()).thenReturn("p1");
    when(paramInfo.getType()).thenReturn("int");
    unit.setSession(session);

    // When / Then
    assertThatThrownBy(unit::execute)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "Operation exe with 1 parameters doesn't exist in bean a:type=x,"
                + " known signatures are exe(int p1)");
  }

  @Test
  void should_throw_when_operation_name_does_not_exist() throws Exception {
    // Given
    RunCommand unit = new RunCommand();
    unit.setBean("a:type=x");
    unit.setParameters(List.of("exe"));
    MBeanInfo beanInfo = mock(MBeanInfo.class);
    MBeanOperationInfo opInfo = mock(MBeanOperationInfo.class);
    when(con.getMBeanInfo(new ObjectName("a:type=x"))).thenReturn(beanInfo);
    when(beanInfo.getOperations()).thenReturn(new MBeanOperationInfo[] {opInfo});
    when(opInfo.getName()).thenReturn("other");
    unit.setSession(session);

    // When / Then
    assertThatThrownBy(unit::execute)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Operation exe doesn't exist in bean a:type=x");
  }

  @Test
  void should_throw_when_json_is_combined_with_positional_parameters() {
    // Given
    RunCommand unit = new RunCommand();
    unit.setBean("a:type=x");
    unit.setJson("[33]");
    unit.setParameters(List.of("exe", "33"));
    unit.setSession(session);

    // When / Then
    assertThatThrownBy(unit::execute)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "Positional parameters cannot be combined with -j, pass only the operation name");
  }

  @Test
  void should_throw_when_json_is_neither_an_array_nor_an_object() {
    // Given
    RunCommand unit = new RunCommand();
    unit.setBean("a:type=x");
    unit.setJson("33");
    unit.setParameters(List.of("exe"));
    unit.setSession(session);

    // When / Then
    assertThatThrownBy(unit::execute)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("JSON parameters must be an array or an object but were NUMBER");
  }

  @Test
  void should_throw_when_json_is_malformed() {
    // Given
    RunCommand unit = new RunCommand();
    unit.setBean("a:type=x");
    unit.setJson("[33,");
    unit.setParameters(List.of("exe"));
    unit.setSession(session);

    // When / Then
    assertThatThrownBy(unit::execute)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageStartingWith("Invalid JSON parameters: ");
  }
}
