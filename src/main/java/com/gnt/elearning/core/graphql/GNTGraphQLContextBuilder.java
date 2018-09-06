package com.gnt.elearning.core.graphql;

import graphql.servlet.GraphQLContext;
import graphql.servlet.GraphQLContextBuilder;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class GNTGraphQLContextBuilder implements GraphQLContextBuilder {

  @Override
  public GraphQLContext build(Optional<HttpServletRequest> req, Optional<HttpServletResponse> resp) {
    return new GNTGraphQLContext(req, resp);
  }
}
