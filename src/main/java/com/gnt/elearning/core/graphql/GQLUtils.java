package com.gnt.elearning.core.graphql;

import graphql.language.Field;
import graphql.language.Node;
import graphql.language.TypeName;
import graphql.schema.DataFetchingEnvironment;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author nghi
 * @since 2/25/18
 */
public class GQLUtils {

    private static Stream<Node> getStream(Node node){
        Stream<Node> stream = Stream.of(node);
        if(!node.getChildren().isEmpty()){
            for(int i = 0 ; i < node.getChildren().size(); i ++){
                stream = Stream.concat(stream,getStream(node.getChildren().get(i)));
            }
        }
        return stream;
    }

    public static String getQueryTypeName(DataFetchingEnvironment environment) {
        String fieldName = environment.getFieldTypeInfo().getFieldDefinition().getName();
        Optional<Field> matchedField = environment.getFields().stream().filter(field -> {
            return field.getName().equals(fieldName);
        }).findFirst();
        if(matchedField.isPresent()){
            Field field = matchedField.get();
            Optional<Node> first = field.getChildren().stream().flatMap(node -> {
                return getStream(node);
            }).filter(node -> {
                return node instanceof TypeName;
            }).findFirst();
            if (first.isPresent()) {
                TypeName typeName = (TypeName) first.get();
                return typeName.getName();
            }
        }
        return null;
    }
}
