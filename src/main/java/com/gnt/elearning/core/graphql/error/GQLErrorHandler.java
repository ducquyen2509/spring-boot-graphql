package com.gnt.elearning.core.graphql.error;

import graphql.ErrorType;
import graphql.ExceptionWhileDataFetching;
import graphql.GraphQLError;
import graphql.servlet.DefaultGraphQLErrorHandler;
import graphql.servlet.GenericGraphQLError;
import graphql.servlet.GraphQLErrorHandler;
import java.util.List;
import java.util.stream.Collectors;
import org.omg.CORBA.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GQLErrorHandler implements GraphQLErrorHandler {

  public static final Logger log = LoggerFactory.getLogger(DefaultGraphQLErrorHandler.class);

  @Override
  public List<GraphQLError> processErrors(List<GraphQLError> errors) {

    final List<GraphQLError> clientErrors = filterGraphQLErrors(errors);
    if (clientErrors.size() < errors.size()) {
      errors.stream().filter(error -> !isClientError(error)).forEach(error -> {
        if (error.getErrorType() == ErrorType.DataFetchingException) {
          ExceptionWhileDataFetching exceptionWhileDataFetching = (ExceptionWhileDataFetching) error;
          Throwable exception = exceptionWhileDataFetching.getException();
          clientErrors.add(new GenericGraphQLError(error.getMessage()));
          log.error("Error executing query ({}): {}", error.getClass().getSimpleName(), error.getMessage());
        }

      });
    }
    return clientErrors;
  }

  protected List<GraphQLError> filterGraphQLErrors(List<GraphQLError> errors) {
    return errors.stream().filter(this::isClientError).collect(Collectors.toList());
  }

  protected boolean isClientError(GraphQLError error) {
    if (error instanceof ExceptionWhileDataFetching) {
      return ((ExceptionWhileDataFetching) error).getException() instanceof GraphQLError;
    }
    return !(error instanceof Throwable);
  }
}
