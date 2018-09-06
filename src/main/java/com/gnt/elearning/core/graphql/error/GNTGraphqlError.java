package com.gnt.elearning.core.graphql.error;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import java.util.List;

/**
 * @author quyen.nd
 * @since 5/30/2018
 */
public class GNTGraphqlError implements GraphQLError {

  private Throwable throwable;

  public GNTGraphqlError(Throwable throwable){
    this.throwable = throwable;
  }

  @Override
  public String getMessage() {
    return throwable.getMessage();
  }

  @Override
  public List<SourceLocation> getLocations() {
    return null;
  }

  @Override
  public ErrorType getErrorType() {
    return ErrorType.DataFetchingException;
  }

  public Throwable getThrowable() {
    return throwable;
  }

  public void setThrowable(Throwable throwable) {
    this.throwable = throwable;
  }
}
