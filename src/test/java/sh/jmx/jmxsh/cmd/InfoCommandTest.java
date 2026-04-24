package sh.jmx.jmxsh.cmd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.StringWriter;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import sh.jmx.jmxsh.MockSession;
import sh.jmx.jmxsh.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link InfoCommand}
 *
 */
class InfoCommandTest {
  private InfoCommand command;

  private StringWriter output;

  /** Set up objects to test */
  @BeforeEach
  void setUp() {
    command = new InfoCommand();
    output = new StringWriter();
  }

  /**
   * Test how attributes are displayed
   *
   * @throws Exception
   */
  @Test
  void executeWithShowingAttributes() throws Exception {
    command.setBean("a:type=x");
    command.setType("a");
    MBeanServerConnection con = mock(MBeanServerConnection.class);
    MBeanInfo beanInfo = mock(MBeanInfo.class);
    MBeanAttributeInfo attributeInfo = mock(MBeanAttributeInfo.class);
    Session session = new MockSession(output, con);
    when(con.getMBeanInfo(new ObjectName("a:type=x"))).thenReturn(beanInfo);
    when(beanInfo.getClassName()).thenReturn("bogus class");
    when(beanInfo.getAttributes()).thenReturn(new MBeanAttributeInfo[] {attributeInfo});
    when(attributeInfo.isReadable()).thenReturn(true);
    when(attributeInfo.isWritable()).thenReturn(false);
    when(attributeInfo.getName()).thenReturn("b");
    when(attributeInfo.getType()).thenReturn("int");
    when(attributeInfo.getDescription()).thenReturn("bingo");
    command.setSession(session);
    command.execute();
    assertThat(output.toString().trim())
        .isEqualTo("# attributes" + System.lineSeparator() + "  %0   - b (int, r)");
  }

  /**
   * Test execution and show available options
   *
   * @throws Exception
   */
  @Test
  void executeWithShowingOperations() throws Exception {
    command.setBean("a:type=x");
    command.setType("o");
    MBeanServerConnection con = mock(MBeanServerConnection.class);
    MBeanInfo beanInfo = mock(MBeanInfo.class);
    MBeanOperationInfo opInfo = mock(MBeanOperationInfo.class);
    MBeanParameterInfo paramInfo = mock(MBeanParameterInfo.class);
    Session session = new MockSession(output, con);
    when(con.getMBeanInfo(new ObjectName("a:type=x"))).thenReturn(beanInfo);
    when(beanInfo.getClassName()).thenReturn("bogus class");
    when(beanInfo.getOperations()).thenReturn(new MBeanOperationInfo[] {opInfo});
    when(opInfo.getDescription()).thenReturn("bingo");
    when(opInfo.getSignature()).thenReturn(new MBeanParameterInfo[] {paramInfo});
    when(paramInfo.getType()).thenReturn(String.class.getName());
    when(paramInfo.getName()).thenReturn("a");
    when(paramInfo.getDescription()).thenReturn("a-desc");
    when(opInfo.getReturnType()).thenReturn("int");
    when(opInfo.getName()).thenReturn("x");
    command.setSession(session);
    command.execute();
    assertThat(output.toString().trim())
        .isEqualTo(
            "# operations" + System.lineSeparator() + "  %0   - int x(java.lang.String a)");
  }

  /**
   * Test execution and show available options
   *
   * @throws Exception
   */
  @Test
  void executeWithShowingSpecificOperation() throws Exception {
    command.setBean("a:type=x");
    command.setOperation("x");
    MBeanServerConnection con = mock(MBeanServerConnection.class);
    MBeanInfo beanInfo = mock(MBeanInfo.class);
    MBeanOperationInfo opInfo = mock(MBeanOperationInfo.class);
    MBeanParameterInfo paramInfo = mock(MBeanParameterInfo.class);
    Session session = new MockSession(output, con);
    when(con.getMBeanInfo(new ObjectName("a:type=x"))).thenReturn(beanInfo);
    when(beanInfo.getClassName()).thenReturn("bogus class");
    when(beanInfo.getOperations()).thenReturn(new MBeanOperationInfo[] {opInfo});
    when(opInfo.getDescription()).thenReturn("bingo");
    when(opInfo.getSignature()).thenReturn(new MBeanParameterInfo[] {paramInfo});
    when(paramInfo.getType()).thenReturn(String.class.getName());
    when(paramInfo.getName()).thenReturn("myfakeparameter");
    when(paramInfo.getDescription()).thenReturn("My param description");
    when(opInfo.getReturnType()).thenReturn("int");
    when(opInfo.getName()).thenReturn("x");
    command.setSession(session);
    command.execute();
    StringBuilder result = new StringBuilder("# operations").append(System.lineSeparator());
    result
        .append("  %0   - int x(java.lang.String myfakeparameter), bingo")
        .append(System.lineSeparator());
    result.append("             parameters:").append(System.lineSeparator());
    result.append("                 + myfakeparameter      : My param description");
    assertThat(output.toString().trim()).isEqualTo(result.toString());
  }

  /**
   * Test execution and show available options
   *
   * @throws Exception
   */
  @Test
  void executeWithShowingNonExistingOperation() throws Exception {
    command.setBean("a:type=x");
    command.setOperation("y");
    MBeanServerConnection con = mock(MBeanServerConnection.class);
    MBeanInfo beanInfo = mock(MBeanInfo.class);
    MBeanOperationInfo opInfo = mock(MBeanOperationInfo.class);
    Session session = new MockSession(output, con);
    when(con.getMBeanInfo(new ObjectName("a:type=x"))).thenReturn(beanInfo);
    when(beanInfo.getClassName()).thenReturn("bogus class");
    when(beanInfo.getOperations()).thenReturn(new MBeanOperationInfo[] {opInfo});
    when(opInfo.getName()).thenReturn("x");
    command.setSession(session);
    command.execute();
    assertThat(output.toString().trim()).isEqualTo("# operations");
  }

  /**
   * Test execution and show available options
   *
   * @throws Exception
   */
  @Test
  void executeWithShowingMultipleMatchingOperations() throws Exception {
    command.setBean("a:type=x");
    command.setOperation("x");
    MBeanServerConnection con = mock(MBeanServerConnection.class);
    MBeanInfo beanInfo = mock(MBeanInfo.class);
    MBeanOperationInfo opInfo1 = mock(MBeanOperationInfo.class);
    MBeanOperationInfo opInfo2 = mock(MBeanOperationInfo.class);
    MBeanParameterInfo paramInfo1 = mock(MBeanParameterInfo.class);
    MBeanParameterInfo paramInfo2 = mock(MBeanParameterInfo.class);
    Session session = new MockSession(output, con);
    when(con.getMBeanInfo(new ObjectName("a:type=x"))).thenReturn(beanInfo);
    when(beanInfo.getClassName()).thenReturn("bogus class");
    when(beanInfo.getOperations()).thenReturn(new MBeanOperationInfo[] {opInfo1, opInfo2});
    when(opInfo1.getDescription()).thenReturn("bingo");
    when(opInfo1.getSignature()).thenReturn(new MBeanParameterInfo[] {paramInfo1});
    when(paramInfo1.getType()).thenReturn(String.class.getName());
    when(paramInfo1.getName()).thenReturn("a");
    when(paramInfo1.getDescription()).thenReturn("My param description");
    when(opInfo1.getReturnType()).thenReturn("int");
    when(opInfo1.getName()).thenReturn("x");

    when(opInfo2.getDescription()).thenReturn("pilou");
    when(opInfo2.getSignature()).thenReturn(new MBeanParameterInfo[] {paramInfo2});
    when(paramInfo2.getType()).thenReturn(Double.TYPE.getName());
    when(paramInfo2.getName()).thenReturn("b");
    when(paramInfo2.getDescription()).thenReturn("My param 2 description");
    when(opInfo2.getReturnType()).thenReturn("void");
    when(opInfo2.getName()).thenReturn("x");
    command.setSession(session);
    command.execute();
    StringBuilder result = new StringBuilder("# operations").append(System.lineSeparator());
    result.append("  %0   - int x(java.lang.String a), bingo").append(System.lineSeparator());
    result.append("             parameters:").append(System.lineSeparator());
    result
        .append("                 + a                    : My param description")
        .append(System.lineSeparator())
        .append(System.lineSeparator());
    result.append("  %1   - void x(double b), pilou").append(System.lineSeparator());
    result.append("             parameters:").append(System.lineSeparator());
    result.append("                 + b                    : My param 2 description");
    assertThat(output.toString().trim()).isEqualTo(result.toString());
  }
}
