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
      int length = Array.getLength(value);
      output.print("[ ");
      for (int i = 0; i < length; i++) {
        if (i != 0) {
          output.print(", ");
        }
        printValue(output, Array.get(value, i), indent);
      }
      output.print(" ]");
    } else if (Collection.class.isAssignableFrom(value.getClass())) {
      boolean start = true;
      output.print("( ");
      for (Object obj : ((Collection<?>) value)) {
        if (!start) {
          output.print(", ");
        }
        start = false;
        printValue(output, obj, indent);
      }
      output.print(" )");
    } else if (Map.class.isAssignableFrom(value.getClass())) {
      output.println("{ ");
      for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
        printExpression(output, entry.getKey(), entry.getValue(), null, indent + indentSize);
      }
      output.print(" ".repeat(indent) + " }");
    } else if (CompositeData.class.isAssignableFrom(value.getClass())) {
      output.println("{ ");
      CompositeData data = (CompositeData) value;
      for (String key : data.getCompositeType().keySet()) {
        Object v = data.get(key);
        printExpression(
            output,
            key,
            v,
            data.getCompositeType().getDescription(key),
            indent + indentSize);
      }
      output.print(" ".repeat(indent) + " }");
    } else if (value instanceof String && showQuotationMarks) {
      output.print("\"" + value + "\"");
    } else {
      output.print(value.toString());
    }
  }
}
