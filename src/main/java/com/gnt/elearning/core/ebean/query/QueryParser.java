package com.gnt.elearning.core.ebean.query;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import io.ebean.ExpressionList;
import io.ebean.Junction;
import io.ebean.Query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.StringTokenizer;

public class QueryParser {

  private static List<String> stopWords = Arrays.asList(/*"rau", "trai", "qua"*/);
  private static Map<String, String> replaceWords = ImmutableMap.of("d", "đ");

  public static <T> Query<T> parser(JsonNode filters, Query<T> query) {
    ExpressionList<T> where = query.where();
    applyFilter(filters, where);
    return query;
  }

  public static String orderByConvert(String orderBy) {
    return orderBy.replace("_", " ");
  }

  private static void applyFilter(JsonNode filters, ExpressionList where) {
    if (filters == null) {
      return;
    }
    if (filters.has("AND")) {
      JsonNode ands = filters.get("AND");
      if (ands.isArray()) {
        Junction andClause = where.and();
        ands.forEach(jsonNode -> {
          applyFilter(jsonNode, andClause);
        });
        andClause.endAnd();
      }
    }

    if (filters.has("OR")) {
      JsonNode ors = filters.get("OR");
      if (ors.isArray()) {
        Junction orClause = where.or();
        ors.forEach(jsonNode -> {
          applyFilter(jsonNode, orClause);
        });
        orClause.endOr();
      }
    }

    Iterator<Map.Entry<String, JsonNode>> fields = filters.fields();
    while (fields.hasNext()) {
      Map.Entry<String, JsonNode> nodeEntry = fields.next();
      String fieldName = nodeEntry.getKey();
      JsonNode fieldValue = nodeEntry.getValue();

      if (fieldName.equals("AND") || fieldName.equals("OR")) {
        continue;
      }

      if (fieldName.contains("_")) {
        if (fieldName.contains("_not_")) {
          String[] not_s = fieldName.split("_not_");
          where = where.not();
          applyOperator(not_s[0], not_s[1], getValue(fieldValue), where);
        } else {
          int i = fieldName.indexOf("_");
          String field = fieldName.substring(0, i);
          String operator = fieldName.substring(i + 1, fieldName.length());
          applyOperator(field, operator, getValue(fieldValue), where);
        }
      } else {
        where.eq(fieldName, getValue(fieldValue));
      }
    }
  }

  private static Object getValue(JsonNode value) {
    JsonNodeType nodeType = value.getNodeType();
    switch (nodeType) {
      case NULL:
        return null;
      case NUMBER:
        return value.asLong();
      case BOOLEAN:
        return value.asBoolean();
      case ARRAY:
        ArrayNode arrayNode = (ArrayNode) value;
        List<Object> list = new ArrayList<>();
        arrayNode.forEach(jsonNode -> {
          list.add(getValue(jsonNode));
        });
        return list;
      default:
        return value.asText();
    }
  }

  private static void applyOperator(String fieldName, String operator, Object value, ExpressionList where) {
    switch (operator) {
      case "eq":
        where.eq(fieldName, value);
        break;
      case "gt":
        where.gt(fieldName, value);
        break;
      case "gte":
        where.ge(fieldName, value);
        break;
      case "lt":
        where.lt(fieldName, value);
        break;
      case "lte":
        where.le(fieldName, value);
        break;
      case "ilike":
        where.ilike(fieldName, String.format("%%%s%%", value.toString()));
        break;
      case "fulltext":
        String searchKey = buildSearchKey(value.toString());
        where.raw(String.format("MATCH (%s) AGAINST ('%s' IN BOOLEAN MODE) ", fieldName, searchKey));
        break;
      case "like":
        where.like(fieldName, String.format("%%%s%%", value.toString()));
        break;
      case "in":
        where.in(fieldName, value);
        break;
      case "starts_with":
        where.startsWith(fieldName, value.toString());
        break;
      case "ends_with":
        where.endsWith(fieldName, value.toString());
      case "contains":
        where.contains(fieldName, value.toString());
        break;
      default:
        where.eq(fieldName, value);

    }
  }

  private static String buildSearchKey(String text) {
    if (text.length() > 255) {
      return "";
    }

    // ignore mysql fulltext search operators
    text = text.replaceAll("[+-@><()~*\"']", " ");

    final StringJoiner searchKey = new StringJoiner(" ");
    StringTokenizer stringTokenizer = new StringTokenizer(text.toLowerCase(), " \t\n\r\f", true);

    while (stringTokenizer.hasMoreTokens()) {
      String token = stringTokenizer.nextToken();
      if (!Strings.isNullOrEmpty(token.trim()) && !stopWords.contains(token)) {
        replaceWords.forEach((key, value) -> {
          if (token.contains(key)) {
            searchKey.add("+(");
            // search partial for the last word
            if (stringTokenizer.hasMoreTokens()) {
              searchKey.add(token);
              searchKey.add(token.replace(key, value));
            } else {
              searchKey.add(token + "*");
              searchKey.add(token.replace(key, value) + "*");
            }
            searchKey.add(")");
          } else {
            // search partial for the last word
            if (stringTokenizer.hasMoreTokens()) {
              searchKey.add("+" + token);
            } else {
              searchKey.add("+" + token + "*");
            }
          }
        });
      }
    }

    return searchKey.toString();
  }


}
