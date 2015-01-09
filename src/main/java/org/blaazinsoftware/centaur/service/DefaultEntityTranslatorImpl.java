package org.blaazinsoftware.centaur.service;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.blaazinsoftware.centaur.CentaurException;
import org.blaazinsoftware.centaur.data.dto.MapEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of <code>EntityTranslator</code>
 *
 * @author Randy May
 */
public class DefaultEntityTranslatorImpl implements EntityTranslator {
    private static final Logger log = LoggerFactory.getLogger(DefaultEntityTranslatorImpl.class);

    public <T> Entity toEntity(final T object) throws CentaurException {
        return toEntity(object, null);
    }

    @SuppressWarnings("unchecked")
    public <T> Entity toEntity(final T object, Key parentKey) throws CentaurException {
        if (null == object) {
            return null;
        }
        if (log.isTraceEnabled()) {
            log.trace("Attempting to convert " + object + " to an Entity: ");
        }

        Entity entity;

        Key key = CentaurServiceUtils.getKey(object);

        if (null == key) {
            String kindValue = CentaurServiceUtils.getKindValue(object);

            if (StringUtils.isEmpty(kindValue)) {
                throw new IllegalArgumentException("Kind is missing");
            }

            entity = new Entity(kindValue, parentKey);
        } else {
            entity = new Entity(key);
        }

        if (object instanceof Map) {
            entity.setProperty("name", CentaurServiceUtils.getNameValue(object));
            Map<String, Object> mapValues = (Map) object;
            for (Map.Entry<String, Object> entry : mapValues.entrySet()) {
                entity.setProperty(entry.getKey(), entry.getValue());
            }
        } else {
            PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(object);
            for (PropertyDescriptor descriptor : propertyDescriptors) {
                String propertyName = descriptor.getName();
                if (!"appEngineKey".equals(propertyName) &&
                        !"key".equals(propertyName) &&
                        !"kind".equals(propertyName) &&
                        !"class".equals(propertyName)) {
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
    public <T> T fromEntity(final Entity entity, Class<?> klass) throws CentaurException {
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

        CentaurServiceUtils.setKey(object, entity.getKey());

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
                            } else if (Float.class.equals(descriptor.getPropertyType()) ||
                                    float.class.equals(descriptor.getPropertyType())) {
                                castValue = ((Double) value).floatValue();
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
