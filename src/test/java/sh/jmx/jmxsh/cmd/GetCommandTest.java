package sh.jmx.jmxsh.cmd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.RuntimeMBeanException;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.SimpleType;

import sh.jmx.jmxsh.Connection;
import sh.jmx.jmxsh.Session;
import sh.jmx.jmxsh.io.WriterCommandOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetCommandTest {

  private static String randomAlphabetic(int length) {
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append((char) ('a' + ThreadLocalRandom.current().nextInt(26)));
    }
    return sb.toString();
  }

  @Mock
  private Session session;
  @Mock
  private Connection connection;
  @Mock
  private MBeanServerConnection con;

  private GetCommand command;
  private StringWriter writer;

  @BeforeEach
  void setUp() throws IOException {
    command = new GetCommand();
    writer = new StringWriter();
    lenient().when(session.getOutput()).thenReturn(new WriterCommandOutput(writer, null));
    lenient().when(session.getConnection()).thenReturn(connection);
    lenient().when(connection.getServerConnection()).thenReturn(con);
  }

  private void getAttributeAndVerify(
      String domain,
      String bean,
      String attribute,
      String expectedBean,
      Object expectedValue,
      boolean singleLine,
      String delimiter) {
    command.setDomain(domain);
    command.setBean(bean);
    command.setAttributes(List.of(attribute));
    command.setSimpleFormat(true);
    command.setSingleLine(singleLine);
    command.setDelimiter(delimiter);

    String[] attributePath = attribute.split("\\.");

    MBeanInfo beanInfo = org.mockito.Mockito.mock(MBeanInfo.class);
    MBeanAttributeInfo attributeInfo = org.mockito.Mockito.mock(MBeanAttributeInfo.class);
    try {
      when(con.getDomains())
          .thenReturn(new String[] {domain, randomAlphabetic(5)});
      when(con.isRegistered(new ObjectName(expectedBean))).thenReturn(true);
      when(con.getMBeanInfo(new ObjectName(expectedBean))).thenReturn(beanInfo);
      when(beanInfo.getAttributes()).thenReturn(new MBeanAttributeInfo[] {attributeInfo});
      when(attributeInfo.getName()).thenReturn(attributePath[0]);
      when(attributeInfo.isReadable()).thenReturn(true);
      when(con.getAttributes(new ObjectName(expectedBean), new String[] {attributePath[0]}))
          .thenReturn(new AttributeList(List.of(new Attribute(attributePath[0], expectedValue))));

      command.setSession(session);
      command.execute();

      Object nestedExpectedValue = expectedValue;

      if (expectedValue instanceof CompositeDataSupport support) {
        nestedExpectedValue = support.get(attributePath[1]);
      }

      assertThat(writer).hasToString(
              nestedExpectedValue.toString()
                  + delimiter
                  + (singleLine ? "" : System.lineSeparator()));
    } catch (JMException e) {
      throw new RuntimeException("Test failed for unexpected JMException", e);
    } catch (IOException e) {
      throw new RuntimeException("Test failed for unexpected IOException", e);
    }
  }

  @Test
  void should_print_message_when_attribute_is_not_readable() throws Exception {
    // Given
    GetCommand unit = new GetCommand();
    StringWriter messageWriter = new StringWriter();
    when(session.getOutput()).thenReturn(new WriterCommandOutput(messageWriter));
    unit.setDomain("a");
    unit.setBean("type=x");
    unit.setAttributes(List.of("a"));
    MBeanInfo beanInfo = org.mockito.Mockito.mock(MBeanInfo.class);
    MBeanAttributeInfo attributeInfo = org.mockito.Mockito.mock(MBeanAttributeInfo.class);
    when(con.getDomains()).thenReturn(new String[] {"a"});
    when(con.isRegistered(new ObjectName("a:type=x"))).thenReturn(true);
    when(con.getMBeanInfo(new ObjectName("a:type=x"))).thenReturn(beanInfo);
    when(beanInfo.getAttributes()).thenReturn(new MBeanAttributeInfo[] {attributeInfo});
    when(attributeInfo.getName()).thenReturn("a");
    when(attributeInfo.isReadable()).thenReturn(false);
    unit.setSession(session);

    // When
    unit.execute();

    // Then
    assertThat(messageWriter.toString()).contains("Attribute a is not readable.");
  }

  /** Test normal execution */
  @Test
  void executeNormally() {
    getAttributeAndVerify("a", "type=x", "a", "a:type=x", "bingo", false, "");
  }

  /** Verify non string type is formatted into string */
  @Test
  void executeWithNonStringType() {
    getAttributeAndVerify("a", "type=x", "a", "a:type=x", Integer.valueOf(10), false, "");
  }

  @Test
  void executeWithSlashInDomainName() {
    getAttributeAndVerify("a/b", "type=c", "a", "a/b:type=c", "bingo", false, "");
  }

  /**
   * Verify attribute name with dash, underline and dot is acceptable
   *
   * @throws OpenDataException
   */
  @Test
  void executeWithStrangeAttributeName() throws Exception {
    Map<String, Object> entries = new HashMap<>();
    entries.put("d", "bingo");
    CompositeType compositeType = org.mockito.Mockito.mock(CompositeType.class);
    when(compositeType.keySet()).thenReturn(entries.keySet());
    doReturn(SimpleType.STRING).when(compositeType).getType("d");
    Object expectedValue = new CompositeDataSupport(compositeType, entries);
    getAttributeAndVerify("a", "type=x", "a_b-c.d", "a:type=x", expectedValue, false, "");
  }

  /** Verify unusual bean name and domain name is acceptable */
  @Test
  void executeWithUnusualDomainAndBeanName() {
    getAttributeAndVerify("a-a", "a.b-c_d=x-y.z", "a", "a-a:a.b-c_d=x-y.z", "bingo", false, "");
  }

  /** Verify that delimiters are working */
  @Test
  void executeWithDelimiters() {
    getAttributeAndVerify("a", "type=x", "a", "a:type=x", "bingo", false, ",");
  }

  /** Verify that single line output is working */
  @Test
  void executeForSingleLineOutput() {
    getAttributeAndVerify("a", "type=x", "a", "a:type=x", "bingo", true, "");
  }

  @Test
  void fetchesMultipleAttributesInSingleBulkCall() throws Exception {
    ObjectName name = new ObjectName("a:type=x");
    MBeanInfo beanInfo = org.mockito.Mockito.mock(MBeanInfo.class);
    MBeanAttributeInfo attr1 = org.mockito.Mockito.mock(MBeanAttributeInfo.class);
    MBeanAttributeInfo attr2 = org.mockito.Mockito.mock(MBeanAttributeInfo.class);
    when(con.getDomains()).thenReturn(new String[] {"a"});
    when(con.isRegistered(name)).thenReturn(true);
    when(con.getMBeanInfo(name)).thenReturn(beanInfo);
    when(beanInfo.getAttributes()).thenReturn(new MBeanAttributeInfo[] {attr1, attr2});
    when(attr1.getName()).thenReturn("x");
    when(attr1.isReadable()).thenReturn(true);
    when(attr2.getName()).thenReturn("y");
    when(attr2.isReadable()).thenReturn(true);
    when(con.getAttributes(name, new String[] {"x", "y"}))
        .thenReturn(new AttributeList(
            List.of(new Attribute("x", "1"), new Attribute("y", "2"))));

    command.setDomain("a");
    command.setBean("type=x");
    command.setAttributes(List.of("x", "y"));
    command.setSimpleFormat(true);
    command.setSession(session);
    command.execute();

    org.mockito.Mockito.verify(con).getAttributes(name, new String[] {"x", "y"});
    org.mockito.Mockito.verify(con, org.mockito.Mockito.never())
        .getAttribute(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    assertThat(writer).hasToString("1" + System.lineSeparator() + "2" + System.lineSeparator());
  }

  @Test
  void fallsBackToSingleGetWhenAttributeMissingFromBulkResult() throws Exception {
    StringWriter messageWriter = new StringWriter();
    when(session.getOutput()).thenReturn(new WriterCommandOutput(writer, messageWriter));
    ObjectName name = new ObjectName("a:type=x");
    MBeanInfo beanInfo = org.mockito.Mockito.mock(MBeanInfo.class);
    MBeanAttributeInfo attributeInfo = org.mockito.Mockito.mock(MBeanAttributeInfo.class);
    when(con.getDomains()).thenReturn(new String[] {"a"});
    when(con.isRegistered(name)).thenReturn(true);
    when(con.getMBeanInfo(name)).thenReturn(beanInfo);
    when(beanInfo.getAttributes()).thenReturn(new MBeanAttributeInfo[] {attributeInfo});
    when(attributeInfo.getName()).thenReturn("x");
    when(attributeInfo.isReadable()).thenReturn(true);
    when(con.getAttributes(name, new String[] {"x"})).thenReturn(new AttributeList());
    when(con.getAttribute(name, "x"))
        .thenThrow(new RuntimeMBeanException(new IllegalStateException("boom"), "failed"));

    command.setDomain("a");
    command.setBean("type=x");
    command.setAttributes(List.of("x"));
    command.setSimpleFormat(true);
    command.setSession(session);
    command.execute();

    assertThat(messageWriter.toString()).contains("Could not get attribute x");
    assertThat(writer).hasToString("null" + System.lineSeparator());
  }

  @Test
  void suggestArgumentWithNoBean() {
    command.setSession(session);
    assertThat(command.suggestArgument(null)).isEmpty();
  }

  @Test
  void suggestOptionWithDomain() throws Exception {
    when(con.getDomains()).thenReturn(new String[] {"a", "b"});
    command.setSession(session);
    assertThat(command.suggestOption("d", null)).containsExactly("a", "b");
  }

  @Test
  void suggestOptionWithBean() throws Exception {
    when(con.queryNames(null, null)).thenReturn(java.util.Set.of());
    command.setSession(session);
    assertThat(command.suggestOption("b", null)).isEmpty();
  }

  @Test
  void suggestOptionUnknown() {
    command.setSession(session);
    assertThat(command.suggestOption("x", null)).isEmpty();
  }

  @Test
  void setAttributesThrowsWhenNull() {
    assertThatThrownBy(() -> command.setAttributes(null))
        .isInstanceOf(NullPointerException.class);
  }
}
