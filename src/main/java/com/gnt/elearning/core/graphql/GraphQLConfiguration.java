package com.gnt.elearning.core.graphql;

import com.coxautodev.graphql.tools.SchemaParserDictionary;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gnt.elearning.core.graphql.scala.GraphQLLocalDate;
import com.gnt.elearning.core.graphql.scala.GraphQLLocalDateTime;
import com.gnt.elearning.core.model.BaseEntity;
import graphql.servlet.ObjectMapperConfigurer;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.MappedSuperclass;
import org.reflections.Reflections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphQLConfiguration {

  @Bean
  public SchemaParserDictionary schemaParserDictionary() {
    SchemaParserDictionary schemaParserDictionary = new SchemaParserDictionary();
    Reflections reflections = new Reflections("com.gnt.elearning");
    Set<Class<? extends BaseEntity>> allClasses = reflections.getSubTypesOf(BaseEntity.class);
    allClasses = allClasses.stream().filter(aClass -> !aClass.isAnnotationPresent(MappedSuperclass.class)).collect(Collectors.toSet());
    schemaParserDictionary.add(allClasses);
    return schemaParserDictionary;
  }

  @Bean
  public GraphQLLocalDateTime graphQLLocalDateTime() {
    return new GraphQLLocalDateTime();
  }

  @Bean
  public GraphQLLocalDate graphQLLocalDate() {
    return new GraphQLLocalDate();
  }

  @Bean
  public ObjectMapperConfigurer objectMapperConfigurer() {
    return new ObjectMapperConfigurer() {
      @Override
      public void configure(ObjectMapper mapper) {
        mapper.findAndRegisterModules();
      }
    };
  }


}
