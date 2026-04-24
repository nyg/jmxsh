package org.cyclopsgroup.jmxterm.cmd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.StringWriter;
import java.util.Arrays;

import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.cyclopsgroup.jmxterm.MockSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RunCommand}
 *
 */
class RunCommandTest {
  private RunCommand command;

  private StringWriter output;

  /** Setup objects to test */
  @BeforeEach
  void setUp() {
    command = new RunCommand();
    output = new StringWriter();
  }

  /** @throws Exception */
  @Test
  void executeNormally() throws Exception {
    command.setBean("a:type=x");
    command.setParameters(Arrays.asList("exe", "33"));

    MBeanServerConnection con = mock(MBeanServerConnection.class);
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
    command.setSession(new MockSession(output, con));
    command.execute();
    assertThat(output.toString().trim()).isEqualTo("bingo");
  }

  /** @throws Exception */
  @Test
  void executeOverloadedMethod() throws Exception {
    command.setBean("a:type=x");
    command.setTypes("java.lang.String");
    command.setParameters(Arrays.asList("exe", "33"));

    MBeanServerConnection con = mock(MBeanServerConnection.class);
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
    command.setSession(new MockSession(output, con));
    command.execute();
    verify(con, never())
        .invoke(new ObjectName("a:type=x"), "exe", new Object[] {33}, new String[] {"int"});
    assertThat(output.toString().trim()).isEqualTo("bingo-string");
  }
}
