package org.blaazin.centaur.data.util;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.blaazin.centaur.data.dto.CentaurEntity;
import org.blaazin.centaur.data.dto.MapEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntityTranslator {
    private static final Logger log = LoggerFactory.getLogger(EntityTranslator.class);

    public <T extends CentaurEntity> Entity toEntity(final T object) {
        return toEntity(object, null);
    }

    @SuppressWarnings("unchecked")
    public <T extends CentaurEntity> Entity toEntity(final T object, Key parentKey) {
        if (null == object) {
            return null;
        }
        if (log.isTraceEnabled()) {
            log.trace("Attempting to convert " + object + " to an Entity: ");
        }
        if (StringUtils.isEmpty(object.getKind())) {
            throw new IllegalArgumentException("Kind is missing");
        }

        Entity entity;

        if (null == object.getAppEngineKey()) {
            entity = new Entity(object.getKind(), parentKey);
        } else {
            entity = new Entity(object.getAppEngineKey());
        }

        if (object instanceof MapEntity) {
            entity.setProperty("name", object.getName());
            Map<String, Object> mapValues = (Map) object;
            for (Map.Entry<String, Object> entry : mapValues.entrySet()) {
                entity.setProperty(entry.getKey(), entry.getValue());
            }
        } else {
            PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(object);
            for (PropertyDescriptor descriptor : propertyDescriptors) {
                String propertyName = descriptor.getName();
                if (!"appEngineKey".equals(propertyName) && !"key".equals(propertyName) && !"kind".equals(propertyName)) {
                    try {
                        if (log.isTraceEnabled()) {
                            log.trace("Processing property '" + propertyName + "'");
                        }
                        Method getterMethod = PropertyUtils.getReadMethod(descriptor);
                        if (null == getterMethod) {
                            log.warn("No getter method found for property '" + propertyName + "'");
                        } else {
                            Object value = getterMethod.invoke(object);
                            entity.setProperty(propertyName, value);
                        }
                    } catch (Exception e) {
                        log.warn(e.getMessage(), e);
                    }

                }

            }
        }

        return entity;
    }

    @SuppressWarnings("unchecked")
    public <T extends CentaurEntity> T fromEntity(final Entity entity, Class<?> klass) {
        if (null == entity) {
            return null;
        }
        if (log.isTraceEnabled()) {
            log.trace("Attempting to convert an Entity to " + klass);
        }
        T object;
        try {
            object = (T) klass.newInstance();
        } catch (Exception e) {
            log.error("Error creating an instance of " + klass.getName(), e);
            return null;
        }

        object.setAppEngineKey(entity.getKey());

        for (Map.Entry<String, Object> entry : entity.getProperties().entrySet()) {
            if (object instanceof MapEntity) {
                ((MapEntity) object).put(entry.getKey(), entry.getValue());
                continue;
            }
            String propertyName = entry.getKey();
            if (!"key".equals(propertyName) && !"kind".equals(propertyName) && !"appEngineKey".equals(propertyName)) {
                try {
                    if (log.isTraceEnabled()) {
                        log.trace("Processing property '" + propertyName + "'");
                    }
                    Object value = entry.getValue();
                    PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(object, propertyName);
                    Method setterMethod = PropertyUtils.getWriteMethod(descriptor);
                    Method getterMethod = PropertyUtils.getReadMethod(descriptor);

                    if (null == setterMethod) {
                        log.warn("No setter method found for property '" + propertyName + "'");
                    } else {
                        // Cast the value to the correct type for this field
                        //Field field = getField(object, propertyName);
                        Object castValue = value;
                        if (value != null) {
                            if (Integer.class.equals(descriptor.getPropertyType()) ||
                                    int.class.equals(descriptor.getPropertyType())) {
                                castValue = ((Long) value).intValue();
                            } else if (List.class.equals(descriptor.getPropertyType())) {
                                // Field is a List class.  Let's see if we have to convert the values

                                if (getterMethod.getGenericReturnType() instanceof ParameterizedType) {
                                    ParameterizedType pType = (ParameterizedType) getterMethod.getGenericReturnType();
                                    if (Integer.class.equals(pType.getActualTypeArguments()[0])) {
                                        List<Integer> integerList = new ArrayList<>();

                                        for (Long longValue : (List<Long>) value) {
                                            integerList.add(longValue.intValue());
                                        }

                                        castValue = integerList;
                                    }
                                }
                            }
                        }
                        setterMethod.invoke(object, castValue);
                    }
                } catch (NoSuchMethodException e) {
                    log.warn("No getter method found for field '" + propertyName);
                    log.warn(e.getMessage(), e);
                } catch (Exception e) {
                    log.warn(e.getMessage(), e);
                }
            }
        }
        return object;
    }
}
