package sh.jmx.jmxsh.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.function.Function;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.StringNode;

public final class MBeanValueParser {

  private static final ObjectMapper MAPPER = JsonMapper.builder().build();

  private static final Map<String, Class<?>> PRIMITIVE_TYPES = Map.ofEntries(
      Map.entry("boolean", boolean.class),
      Map.entry("byte", byte.class),
      Map.entry("char", char.class),
      Map.entry("short", short.class),
      Map.entry("int", int.class),
      Map.entry("long", long.class),
      Map.entry("float", float.class),
      Map.entry("double", double.class));

  private static final Map<Class<?>, Function<String, Object>> CONVERTERS = Map.ofEntries(
      Map.entry(boolean.class, MBeanValueParser::parseBooleanStrict),
      Map.entry(Boolean.class, MBeanValueParser::parseBooleanStrict),
      Map.entry(byte.class, Byte::parseByte),
      Map.entry(Byte.class, Byte::parseByte),
      Map.entry(char.class, MBeanValueParser::parseChar),
      Map.entry(Character.class, MBeanValueParser::parseChar),
      Map.entry(short.class, Short::parseShort),
      Map.entry(Short.class, Short::parseShort),
      Map.entry(int.class, Integer::parseInt),
      Map.entry(Integer.class, Integer::parseInt),
      Map.entry(long.class, Long::parseLong),
      Map.entry(Long.class, Long::parseLong),
      Map.entry(float.class, Float::parseFloat),
      Map.entry(Float.class, Float::parseFloat),
      Map.entry(double.class, Double::parseDouble),
      Map.entry(Double.class, Double::parseDouble),
      Map.entry(BigInteger.class, BigInteger::new),
      Map.entry(BigDecimal.class, BigDecimal::new));

  static JsonNode readTree(String json) {
    try {
      return MAPPER.readTree(json);
    } catch (JacksonException e) {
      throw new IllegalArgumentException(
          "Invalid JSON parameters: %s".formatted(e.getOriginalMessage()), e);
    }
  }

  public Object parse(String expression, String type) {
    if (expression == null || ValueFormat.NULL.equalsIgnoreCase(expression)) {
      return null;
    }
    Class<?> targetType = resolveClass(type);
    if (targetType == String.class) {
      return expression;
    }
    if (expression.isEmpty()) {
      return null;
    }
    Function<String, Object> converter = CONVERTERS.get(targetType);
    if (converter != null) {
      return converter.apply(expression);
    }
    return convertExpression(expression, targetType);
  }

  public Object parseNode(JsonNode node, String type) {
    if (node == null || node.isNull() || node.isMissingNode()) {
      return null;
    }
    return convertNode(node, resolveClass(type));
  }

  private static Object convertExpression(String expression, Class<?> targetType) {
    try {
      return isJsonDocument(expression)
          ? MAPPER.readValue(expression, targetType)
          : MAPPER.convertValue(StringNode.valueOf(expression), targetType);
    } catch (JacksonException e) {
      throw cannotConvert(expression, targetType, e);
    }
  }

  private static Object convertNode(JsonNode node, Class<?> targetType) {
    try {
      return MAPPER.convertValue(node, targetType);
    } catch (JacksonException e) {
      throw cannotConvert(node.toString(), targetType, e);
    }
  }

  private static boolean isJsonDocument(String expression) {
    char first = expression.charAt(0);
    return first == '[' || first == '{';
  }

  private static IllegalArgumentException cannotConvert(
      String expression, Class<?> targetType, JacksonException cause) {
    return new IllegalArgumentException(
        "Cannot convert \"%s\" to type %s: %s"
            .formatted(expression, targetType.getName(), cause.getOriginalMessage()),
        cause);
  }

  private static Class<?> resolveClass(String type) {
    Class<?> primitive = PRIMITIVE_TYPES.get(type);
    if (primitive != null) {
      return primitive;
    }
    try {
      return Class.forName(type);
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException("Type %s isn't valid".formatted(type), e);
    }
  }

  private static boolean parseBooleanStrict(String expression) {
    if ("true".equalsIgnoreCase(expression)) {
      return true;
    }
    if ("false".equalsIgnoreCase(expression)) {
      return false;
    }
    throw new IllegalArgumentException("Cannot convert \"%s\" to boolean".formatted(expression));
  }

  private static char parseChar(String expression) {
    if (expression.length() != 1) {
      throw new IllegalArgumentException("Cannot convert \"%s\" to char".formatted(expression));
    }
    return expression.charAt(0);
  }
}
