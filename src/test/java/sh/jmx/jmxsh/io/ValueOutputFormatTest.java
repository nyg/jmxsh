package sh.jmx.jmxsh.io;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import org.junit.jupiter.api.Test;

class ValueOutputFormatTest {
  /** Print out expression and verify output */
  @Test
  void printExpression() {
    ValueOutputFormat f = new ValueOutputFormat();
    StringWriter out = new StringWriter();
    f.printExpression(new WriterCommandOutput(out), "a", "aaa", "astring");
    String s = out.toString().replaceAll("\\s", "");
    assertThat(s).isEqualTo("\"a\"=\"aaa\";(astring)");
  }

  /** Print out a list value and verify output */
  @Test
  void printList() {
    ValueOutputFormat f = new ValueOutputFormat();
    StringWriter out = new StringWriter();
    f.printValue(new WriterCommandOutput(out), List.of("abc", "xyz"));
    assertThat(out).hasToString("( \"abc\", \"xyz\" )");
  }

  @Test
  void printArray() {
    ValueOutputFormat f = new ValueOutputFormat();
    StringWriter out = new StringWriter();
    f.printValue(new WriterCommandOutput(out), new int[] {1, 2, 3});
    assertThat(out).hasToString("[ 1, 2, 3 ]");
  }

  @Test
  void printCompositeData() throws Exception {
    CompositeType type =
        new CompositeType(
            "t", "t", new String[] {"k"}, new String[] {"kd"}, new OpenType<?>[] {SimpleType.STRING});
    CompositeData data = new CompositeDataSupport(type, new String[] {"k"}, new Object[] {"v"});
    ValueOutputFormat f = new ValueOutputFormat();
    StringWriter out = new StringWriter();
    f.printValue(new WriterCommandOutput(out), data);
    String s = out.toString().replaceAll("\\s", "");
    assertThat(s).isEqualTo("{\"k\"=\"v\";(kd)}");
  }

  @Test
  void printNullAndPlainValues() {
    ValueOutputFormat f = new ValueOutputFormat(2, true, false);
    StringWriter out = new StringWriter();
    CommandOutput output = new WriterCommandOutput(out);
    f.printValue(output, null);
    f.printValue(output, 42);
    f.printValue(output, "raw");
    assertThat(out).hasToString("null42raw");
  }

  @Test
  void constructorRejectsNegativeIndent() {
    assertThatThrownBy(() -> new ValueOutputFormat(-1, true, true))
        .isInstanceOf(IllegalArgumentException.class);
  }

  /** Print out a map and verify output */
  @Test
  void printMap() {
    ValueOutputFormat f = new ValueOutputFormat();
    StringWriter out = new StringWriter();
    Map<String, String> map = new LinkedHashMap<>();
    map.put("a", "aaa");
    map.put("b", "bbb");
    f.printValue(new WriterCommandOutput(out), map);
    String s = out.toString().replaceAll("\\s", "");
    assertThat(s).isEqualTo("{\"a\"=\"aaa\";\"b\"=\"bbb\";}");
  }
}
