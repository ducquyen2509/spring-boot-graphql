package com.gnt.elearning.core.graphql.scala;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import graphql.language.ArrayValue;
import graphql.language.BooleanValue;
import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.language.ObjectValue;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GraphQLJson implements FactoryBean<GraphQLScalarType> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public GraphQLScalarType getObject() {
        GraphQLScalarType jsonType = new GraphQLScalarType("Json", "Json", new Coercing<JsonNode, JsonNode>() {
            @Override
            public JsonNode serialize(Object dataFetcherResult) {
                return objectMapper.convertValue(dataFetcherResult, JsonNode.class);
            }

            @Override
            public JsonNode parseValue(Object input) {
                return objectMapper.convertValue(input, JsonNode.class);
            }

            @Override
            public JsonNode parseLiteral(Object input) {
                if (input instanceof ObjectValue) {
                    ObjectNode objectNode = objectMapper.createObjectNode();
                    ((ObjectValue) input).getObjectFields().forEach(objectField -> {
                        objectNode.set(objectField.getName(), parseLiteral(objectField.getValue()));
                    });
                    return objectNode;
                } else if (input instanceof ArrayValue) {
                    ArrayNode jsonNodes = objectMapper.createArrayNode();
                    ((ArrayValue) input).getValues().forEach(value -> {
                        JsonNode jsonNode = parseLiteral(value);
                        jsonNodes.add(jsonNode);
                    });
                    return jsonNodes;
                } else if (input instanceof IntValue) {
                    return BigIntegerNode.valueOf(((IntValue) input).getValue());
                } else if (input instanceof StringValue) {
                    return TextNode.valueOf(((StringValue) input).getValue());
                } else if (input instanceof BooleanValue) {
                    return BooleanNode.valueOf(((BooleanValue) input).isValue());
                } else if (input instanceof FloatValue) {
                    return DecimalNode.valueOf(((FloatValue) input).getValue());
                }
                return null;
            }
        });
        return jsonType;
    }

    @Override
    public Class<?> getObjectType() {
        return GraphQLScalarType.class;
    }
}
