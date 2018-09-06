package com.gnt.elearning.core.graphql.resolver.crud;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gnt.elearning.core.ebean.query.QueryParser;
import com.gnt.elearning.core.graphql.GQLUtils;
import com.gnt.elearning.core.model.BaseEntity;
import com.gnt.elearning.core.model.BatchPayLoad;
import com.gnt.elearning.core.persistent.EntityManager;
import graphql.schema.DataFetchingEnvironment;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Query;
import io.ebean.annotation.Transactional;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author nghi
 * @since 2/28/18
 */
@Component
public class CrudMutationResolver implements GraphQLMutationResolver {

  private EntityManager entityManager;
  private ObjectMapper objectMapper;

  @Autowired
  public CrudMutationResolver(ObjectMapper objectMapper, EntityManager entityManager) {
    this.objectMapper = objectMapper;
    this.entityManager = entityManager;
  }

  /**
   * @param data
   * @param environment
   * @return
   * @throws IOException
   */
  @Transactional
  public BaseEntity createOne(JsonNode data, DataFetchingEnvironment environment) throws IOException {
    String queryTypeName = GQLUtils.getQueryTypeName(environment);
    Class<? extends BaseEntity> clazz = entityManager.getClassBySimpleName(queryTypeName);
    String persistentContext = entityManager.getPersistentContext(clazz);

    EbeanServer ebeanServer = Ebean.getServer(persistentContext);

    BaseEntity object = objectMapper.readValue(data.toString(), clazz);
    ebeanServer.insert(object);
    return object;
  }

  /**
   * @param data
   * @param environment
   * @return
   * @throws IOException
   */
  @Transactional
  public BaseEntity updateOne(JsonNode data, DataFetchingEnvironment environment) throws IOException {
    String queryTypeName = GQLUtils.getQueryTypeName(environment);
    Class<? extends BaseEntity> clazz = entityManager.getClassBySimpleName(queryTypeName);
    String persistentContext = entityManager.getPersistentContext(clazz);

    EbeanServer ebeanServer = Ebean.getServer(persistentContext);
    BaseEntity crudType = ebeanServer.find(clazz, data.get("id").asLong());
    objectMapper.readerForUpdating(crudType).readValue(data);
    ebeanServer.update(crudType);

    return crudType;
  }

  /**
   * @param id
   * @param environment
   * @return
   */
  @Transactional
  public BaseEntity deleteOne(Long id, DataFetchingEnvironment environment) {
    String queryTypeName = GQLUtils.getQueryTypeName(environment);
    Class<? extends BaseEntity> clazz = entityManager.getClassBySimpleName(queryTypeName);
    String persistentContext = entityManager.getPersistentContext(clazz);

    EbeanServer ebeanServer = Ebean.getServer(persistentContext);
    BaseEntity object = ebeanServer.find(clazz, id);
    ebeanServer.delete(object);

    return object;
  }

  @Transactional
  public BaseEntity activeOne(Long id, DataFetchingEnvironment environment) {
    String queryTypeName = GQLUtils.getQueryTypeName(environment);
    Class<? extends BaseEntity> clazz = entityManager.getClassBySimpleName(queryTypeName);
    String persistentContext = entityManager.getPersistentContext(clazz);

    EbeanServer ebeanServer = Ebean.getServer(persistentContext);
    BaseEntity object = ebeanServer.find(clazz).where().idEq(id).setIncludeSoftDeletes().findOne();
    object.setInActive(false);
    ebeanServer.save(object);
    return object;
  }

  @Transactional
  public BaseEntity deActiveOne(Long id, DataFetchingEnvironment environment) {
    String queryTypeName = GQLUtils.getQueryTypeName(environment);
    Class<? extends BaseEntity> clazz = entityManager.getClassBySimpleName(queryTypeName);
    String persistentContext = entityManager.getPersistentContext(clazz);

    EbeanServer ebeanServer = Ebean.getServer(persistentContext);
    BaseEntity object = ebeanServer.find(clazz).where().idEq(id).setIncludeSoftDeletes().findOne();
    object.setInActive(true);
    ebeanServer.save(object);
    return object;
  }


  /**
   * @param data
   * @param where
   * @param environment
   * @return
   */
  @Transactional
  public BatchPayLoad updateMany(JsonNode data, JsonNode where, DataFetchingEnvironment environment) throws IOException {
    String queryTypeName = GQLUtils.getQueryTypeName(environment);
    Class<? extends BaseEntity> clazz = entityManager.getClassBySimpleName(queryTypeName);
    String persistentContext = entityManager.getPersistentContext(clazz);

    EbeanServer ebeanServer = Ebean.getServer(persistentContext);
    Query<? extends BaseEntity> query = QueryParser.parser(where, ebeanServer.find(clazz));
    List<? extends BaseEntity> list = query.findList();

    for (Object o : list) {
      Object value = objectMapper.readerForUpdating(o).readValue(data);
      ebeanServer.update(value);
    }

    BatchPayLoad batchPayLoad = new BatchPayLoad();
    batchPayLoad.setList(list);
    batchPayLoad.setCount(list.size());
    return batchPayLoad;
  }
}
