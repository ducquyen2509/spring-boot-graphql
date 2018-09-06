package com.gnt.elearning.core.persistent.impl;

import com.gnt.elearning.core.persistent.EntityManager;
import io.ebean.EbeanServer;
import io.ebean.plugin.BeanType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author nghi
 * @since 1/26/18
 */
@Component
public class EbeanEntityManager implements EntityManager {
    private Map<String,String> persistContext = new HashMap<>();
    private Map<String,Class> simpleNameClassContext = new HashMap<>();

    @Autowired
    public EbeanEntityManager(EbeanServer ebeanServer){
        List<? extends BeanType<?>> beanTypes = ebeanServer.getPluginApi().getBeanTypes();
        beanTypes.stream().forEach(beanType -> {
            Class clazz = beanType.getBeanType();
            persistContext.put(clazz.getCanonicalName(), ebeanServer.getName());
            simpleNameClassContext.put(clazz.getSimpleName(), clazz);
        });
    }

    @Override
    public String getPersistentContext(Class beanClazz) {
        return persistContext.get(beanClazz.getCanonicalName());
    }

    @Override
    public Class getClassBySimpleName(String simpleName) {
        return simpleNameClassContext.get(simpleName);
    }

}
