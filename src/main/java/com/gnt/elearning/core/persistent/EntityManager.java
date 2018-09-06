package com.gnt.elearning.core.persistent;

import com.gnt.elearning.core.model.BaseEntity;

/**
 * @author nghi
 * @since 1/26/18
 */
public interface EntityManager {
    String getPersistentContext(Class beanClazz);
    Class<? extends BaseEntity> getClassBySimpleName(String simpleName);
}
