package com.gnt.elearning.core.graphql.resolver.crud;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.fasterxml.jackson.databind.JsonNode;
import com.gnt.elearning.core.ebean.query.QueryParser;
import com.gnt.elearning.core.graphql.GQLUtils;
import com.gnt.elearning.core.model.BaseEntity;
import com.gnt.elearning.core.model.Pagination;
import com.gnt.elearning.core.persistent.EntityManager;
import graphql.schema.DataFetchingEnvironment;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.PagedList;
import io.ebean.Query;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author nghi
 * @since 2/25/18
 */
@Component
public class CrudQueryResolver implements GraphQLQueryResolver {

    @Autowired
    private EntityManager entityManager;

    public List<? extends BaseEntity> findList(JsonNode where, String orderBy, int limit, int skip, boolean includeInactive, DataFetchingEnvironment environment) {
        String queryTypeName = GQLUtils.getQueryTypeName(environment);
        if (queryTypeName == null) {
            throw new IllegalStateException("Please send type name");
        }

        //find class
        Class<? extends BaseEntity> clazz = entityManager.getClassBySimpleName(queryTypeName);
        //find database name
        String persistentContext = entityManager.getPersistentContext(clazz);

        EbeanServer ebeanServer = Ebean.getServer(persistentContext);
        // parser graphql
        Query<? extends BaseEntity> crudQuery = QueryParser.parser(where, ebeanServer.createQuery(clazz));
        if(includeInactive){
            crudQuery.setIncludeSoftDeletes();
        }
        crudQuery.setFirstRow(skip);
        crudQuery.setMaxRows(limit);
        crudQuery.order(QueryParser.orderByConvert(orderBy));
        return crudQuery.findList();
    }

    public Pagination findPage(JsonNode where, String orderBy, int limit, int skip, boolean includeInactive, DataFetchingEnvironment environment) {
        String queryTypeName = GQLUtils.getQueryTypeName(environment);
        if (queryTypeName == null) {
            throw new IllegalStateException("Please send type name");
        }

        Class<? extends BaseEntity> clazz = entityManager.getClassBySimpleName(queryTypeName);
        String persistentContext = entityManager.getPersistentContext(clazz);

        EbeanServer ebeanServer = Ebean.getServer(persistentContext);
        Query<? extends BaseEntity> crudQuery = QueryParser.parser(where, ebeanServer.createQuery(clazz));
        if(includeInactive){
            crudQuery.setIncludeSoftDeletes();
        }
        crudQuery.setFirstRow(skip);
        crudQuery.setMaxRows(limit);
        crudQuery.order(QueryParser.orderByConvert(orderBy));

        PagedList<? extends BaseEntity> pagedList = crudQuery.findPagedList();
        List<? extends BaseEntity> list = pagedList.getList();
        pagedList.loadCount();

        Pagination pagination = new Pagination(list, limit, skip);
        pagination.setTotalCount(pagedList.getTotalCount());
        return pagination;
    }

    public BaseEntity findOne(Object id, DataFetchingEnvironment environment) {
        String queryTypeName = GQLUtils.getQueryTypeName(environment);
        if (queryTypeName == null) {
            throw new IllegalStateException("Please send type name");
        }

        Class<? extends BaseEntity> clazz = entityManager.getClassBySimpleName(queryTypeName);
        String persistentContext = entityManager.getPersistentContext(clazz);

        EbeanServer ebeanServer = Ebean.getServer(persistentContext);
        BaseEntity object = ebeanServer.createQuery(clazz).where().idEq(id).setIncludeSoftDeletes().findOne();

        return object;

    }
}
