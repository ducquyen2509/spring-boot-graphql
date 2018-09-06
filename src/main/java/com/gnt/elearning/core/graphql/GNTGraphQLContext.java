package com.gnt.elearning.core.graphql;

import graphql.servlet.GraphQLContext;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GNTGraphQLContext extends GraphQLContext {

  private String userId;

  public GNTGraphQLContext(Optional<HttpServletRequest> request, Optional<HttpServletResponse> response) {
    super(request, response);
  }
}
