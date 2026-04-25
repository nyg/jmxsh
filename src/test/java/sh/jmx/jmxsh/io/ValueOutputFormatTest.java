package sh.jmx.jmxsh.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
