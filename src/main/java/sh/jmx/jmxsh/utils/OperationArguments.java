package sh.jmx.jmxsh.utils;

import java.util.Arrays;
import java.util.List;

import javax.management.MBeanParameterInfo;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

public sealed interface OperationArguments
    permits OperationArguments.Positional,
        OperationArguments.JsonArray,
        OperationArguments.JsonObject {

  static OperationArguments ofPositional(List<String> expressions, MBeanValueParser parser) {
    return new Positional(List.copyOf(expressions), parser);
  }

  static OperationArguments ofJson(String json, MBeanValueParser parser) {
    JsonNode root = MBeanValueParser.readTree(json);
    return switch (root) {
      case ArrayNode array -> new JsonArray(array, parser);
      case ObjectNode object -> new JsonObject(object, parser);
      default -> throw new IllegalArgumentException(
          "JSON parameters must be an array or an object but were %s".formatted(root.getNodeType()));
    };
  }

  int size();

  boolean fits(MBeanParameterInfo[] signature);

  Object[] bind(MBeanParameterInfo[] signature);

  record Positional(List<String> expressions, MBeanValueParser parser)
      implements OperationArguments {

    @Override
    public int size() {
      return expressions.size();
    }

    @Override
    public boolean fits(MBeanParameterInfo[] signature) {
      return signature.length == expressions.size();
    }

    @Override
    public Object[] bind(MBeanParameterInfo[] signature) {
      Object[] params = new Object[signature.length];
      for (int i = 0; i < signature.length; i++) {
        params[i] =
            parser.parse(ValueFormat.parseValue(expressions.get(i)), signature[i].getType());
      }
      return params;
    }
  }

  record JsonArray(ArrayNode values, MBeanValueParser parser) implements OperationArguments {

    @Override
    public int size() {
      return values.size();
    }

    @Override
    public boolean fits(MBeanParameterInfo[] signature) {
      return signature.length == values.size();
    }

    @Override
    public Object[] bind(MBeanParameterInfo[] signature) {
      Object[] params = new Object[signature.length];
      for (int i = 0; i < signature.length; i++) {
        params[i] = parser.parseNode(values.get(i), signature[i].getType());
      }
      return params;
    }
  }

  record JsonObject(ObjectNode values, MBeanValueParser parser) implements OperationArguments {

    @Override
    public int size() {
      return values.size();
    }

    @Override
    public boolean fits(MBeanParameterInfo[] signature) {
      return signature.length == values.size()
          && Arrays.stream(signature).allMatch(parameter -> values.has(parameter.getName()));
    }

    @Override
    public Object[] bind(MBeanParameterInfo[] signature) {
      Object[] params = new Object[signature.length];
      for (int i = 0; i < signature.length; i++) {
        params[i] = parser.parseNode(values.get(signature[i].getName()), signature[i].getType());
      }
      return params;
    }
  }
}
