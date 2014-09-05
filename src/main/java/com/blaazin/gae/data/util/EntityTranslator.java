package com.blaazin.gae.data.util;

import com.blaazin.gae.data.dto.BlaazinEntity;
import com.blaazin.gae.data.dto.MapEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntityTranslator {
    private static final Logger log = Logger.getLogger(EntityTranslator.class);

    public static <T extends BlaazinEntity> Entity toEntity(final T object) {
        return EntityTranslator.toEntity(object, null);
    }

    @SuppressWarnings("unchecked")
    public static <T extends BlaazinEntity> Entity toEntity(final T object, Key parentKey) {
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
            ReflectionUtils.doWithFields(object.getClass(), new ToEntityFieldCallback(object, entity));
        }

        return entity;
    }

    private static class ToEntityFieldCallback<T extends BlaazinEntity> implements ReflectionUtils.FieldCallback {

        private T object;
        private Entity entity;

        private ToEntityFieldCallback(T object, Entity entity) {
            this.object = object;
            this.entity = entity;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
            String propertyName = field.getName();
            if (!"key".equals(propertyName) && !"kind".equals(propertyName)) {
                try {
                    if (log.isTraceEnabled()) {
                        log.trace("Processing property '" + propertyName + "'");
                    }
                    Method getterMethod = getGetterForField(object, field);
                    if (null == getterMethod) {
                        log.warn("No getter method found for property '" + propertyName + "'");
                    } else {
                        Object value = getterMethod.invoke(object, null);
                        entity.setProperty(propertyName, value);
                    }
                } catch (Exception e) {
                    log.warn(e.getMessage(), e);
                }

            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends BlaazinEntity> T fromEntity(final Entity entity, Class<?> klass) {
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
                    Method setterMethod = getSetterForProperty(object, propertyName);

                    if (null == setterMethod) {
                        log.warn("No setter method found for property '" + propertyName + "'");
                    } else {
                        // Cast the value to the correct type for this field
                        Field field = getField(object, propertyName);
                        Object castValue = value;
                        if (value != null) {
                            if (Integer.class.equals(field.getType()) ||
                                    int.class.equals(field.getType())) {
                                castValue = ((Long) value).intValue();
                            } else if (List.class.equals(field.getType())) {
                                // Field is a List class.  Let's see if we have to convert the values

                                if (field.getGenericType() instanceof ParameterizedType) {
                                    ParameterizedType pType = (ParameterizedType) field.getGenericType();
                                    if (Integer.class.equals(pType.getActualTypeArguments()[0])) {
                                        List<Integer> integerList = new ArrayList<Integer>();

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

    private static Method getGetterForField(Object object, Field field) throws NoSuchMethodException {
        if (boolean.class.equals(field.getType())) {
            String getterFieldName = "is" + StringUtils.capitalize(field.getName());

            Method isMethod = ReflectionUtils.findMethod(object.getClass(), getterFieldName);
            if (isMethod != null) {
                return isMethod;
            }
        }

        String getterFieldName = "get" + StringUtils.capitalize(field.getName());

        return ReflectionUtils.findMethod(object.getClass(), getterFieldName);
    }

    private static Method getSetterForProperty(Object object, String propertyName) throws NoSuchMethodException {
        Field field = getField(object, propertyName);

        String setterFieldName = "set" + StringUtils.capitalize(propertyName);

        return ReflectionUtils.findMethod(object.getClass(), setterFieldName, field.getType());
    }

    private static Field getField(Object object, String propertyName) {
        return ReflectionUtils.findField(object.getClass(), propertyName);
    }
}
