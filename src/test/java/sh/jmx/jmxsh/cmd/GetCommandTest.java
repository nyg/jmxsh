package sh.jmx.jmxsh.cmd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.SimpleType;

import sh.jmx.jmxsh.MockSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test case of {@link GetCommand}
 *
 */
class GetCommandTest {

  private static String randomAlphabetic(int length) {
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append((char) ('a' + ThreadLocalRandom.current().nextInt(26)));
    }
    return sb.toString();
  }

  private GetCommand command;

  private StringWriter output;

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
    command.setAttributes(Arrays.asList(attribute));
    command.setSimpleFormat(true);
    command.setSingleLine(singleLine);
    command.setDelimiter(delimiter);

    String[] attributePath = attribute.split("\\.");

    MBeanServerConnection con = mock(MBeanServerConnection.class);
    MBeanInfo beanInfo = mock(MBeanInfo.class);
    MBeanAttributeInfo attributeInfo = mock(MBeanAttributeInfo.class);
    try {
      when(con.getDomains())
          .thenReturn(new String[] {domain, randomAlphabetic(5)});
      when(con.getMBeanInfo(new ObjectName(expectedBean))).thenReturn(beanInfo);
      when(beanInfo.getAttributes()).thenReturn(new MBeanAttributeInfo[] {attributeInfo});
      when(attributeInfo.getName()).thenReturn(attributePath[0]);
      when(attributeInfo.isReadable()).thenReturn(true);
      when(con.getAttribute(new ObjectName(expectedBean), attributePath[0]))
          .thenReturn(expectedValue);

      command.setSession(new MockSession(output, con));
      command.execute();

      Object nestedExpectedValue = expectedValue;

      if (expectedValue instanceof CompositeDataSupport support) {
        nestedExpectedValue = support.get(attributePath[1]);
      }

      assertThat(output.toString())
          .isEqualTo(
              nestedExpectedValue.toString()
                  + delimiter
                  + (singleLine ? "" : System.lineSeparator()));
    } catch (JMException e) {
      throw new RuntimeException("Test failed for unexpected JMException", e);
    } catch (IOException e) {
      throw new RuntimeException("Test failed for unexpected IOException", e);
    }
  }

  /** Set up class to test */
  @BeforeEach
  void setUp() {
    command = new GetCommand();
    output = new StringWriter();
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
    CompositeType compositeType = mock(CompositeType.class);
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
}
