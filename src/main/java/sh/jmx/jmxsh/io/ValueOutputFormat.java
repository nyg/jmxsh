package sh.jmx.jmxsh.io;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import javax.management.openmbean.CompositeData;



public class ValueOutputFormat {
  private final int indentSize;

  private final boolean showDescription;

  private final boolean showQuotationMarks;

  public ValueOutputFormat() {
    this(2, true, true);
  }

  public ValueOutputFormat(int indentSize, boolean showDescription, boolean showQuotationMarks) {
    if (indentSize < 0) {
      throw new IllegalArgumentException("Invalid indent size value " + indentSize);
    }
    this.indentSize = indentSize;
    this.showDescription = showDescription;
    this.showQuotationMarks = showQuotationMarks;
  }

  public void printExpression(CommandOutput output, Object name, Object value, String description) {
    printExpression(output, name, value, description, 0);
  }

  private void printExpression(
      CommandOutput output, Object name, Object value, String description, int indent) {
    output.print(" ".repeat(indent));
    printValue(output, name, indent);
    output.print(" = ");
    printValue(output, value, indent);
    output.print(";");
    if (showDescription && description != null) {
      output.print(" (" + description + ")");
    }
    output.println("");
  }

  public void printValue(CommandOutput output, Object value) {
    printValue(output, value, 0);
  }

  private void printValue(CommandOutput output, Object value, int indent) {
    if (value == null) {
      output.print("null");
    } else if (value.getClass().isArray()) {
      printArray(output, value, indent);
    } else if (value instanceof Collection<?> collection) {
      printCollection(output, collection, indent);
    } else if (value instanceof Map<?, ?> map) {
      printMap(output, map, indent);
    } else if (value instanceof CompositeData data) {
      printCompositeData(output, data, indent);
    } else if (value instanceof String && showQuotationMarks) {
      output.print("\"" + value + "\"");
    } else {
      output.print(value.toString());
    }
  }

  private void printArray(CommandOutput output, Object value, int indent) {
    int length = Array.getLength(value);
    output.print("[ ");
    for (int i = 0; i < length; i++) {
      if (i != 0) {
        output.print(", ");
      }
      printValue(output, Array.get(value, i), indent);
    }
    output.print(" ]");
  }

  private void printCollection(CommandOutput output, Collection<?> collection, int indent) {
    boolean start = true;
    output.print("( ");
    for (Object obj : collection) {
      if (!start) {
        output.print(", ");
      }
      start = false;
      printValue(output, obj, indent);
    }
    output.print(" )");
  }

  private void printMap(CommandOutput output, Map<?, ?> map, int indent) {
    output.println("{ ");
    for (Map.Entry<?, ?> entry : map.entrySet()) {
      printExpression(output, entry.getKey(), entry.getValue(), null, indent + indentSize);
    }
    output.print(" ".repeat(indent) + " }");
  }

  private void printCompositeData(CommandOutput output, CompositeData data, int indent) {
    output.println("{ ");
    for (String key : data.getCompositeType().keySet()) {
      printExpression(
          output,
          key,
          data.get(key),
          data.getCompositeType().getDescription(key),
          indent + indentSize);
    }
    output.print(" ".repeat(indent) + " }");
  }
}
