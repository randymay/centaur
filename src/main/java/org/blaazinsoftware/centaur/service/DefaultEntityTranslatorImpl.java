package org.blaazinsoftware.centaur.service;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.blaazinsoftware.centaur.CentaurException;
import org.blaazinsoftware.centaur.data.dto.MapEntity;
import org.blaazinsoftware.centaur.exception.TranslatorNotFoundException;
import org.blaazinsoftware.centaur.service.fieldTranslator.FieldTranslator;
import org.blaazinsoftware.centaur.service.fieldTranslator.FloatFieldTranslator;
import org.blaazinsoftware.centaur.service.fieldTranslator.IntegerFieldTranslator;
import org.blaazinsoftware.centaur.service.fieldTranslator.NoCastFieldTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Default implementation of <code>EntityTranslator</code>
 *
 * @author Randy May
 */
public class DefaultEntityTranslatorImpl implements EntityTranslator {
    private static final Logger log = LoggerFactory.getLogger(DefaultEntityTranslatorImpl.class);

    private Map<Class, FieldTranslator> fieldTranslatorMap = new HashMap<>();

    public <T> Entity toEntity(final T object) throws CentaurException {
        return toEntity(object, null);
    }

    public DefaultEntityTranslatorImpl () {
        final IntegerFieldTranslator integerFieldTranslator = new IntegerFieldTranslator();
        final FloatFieldTranslator floatFieldTranslator = new FloatFieldTranslator();
        final NoCastFieldTranslator noCastFieldTranslator = new NoCastFieldTranslator();
        fieldTranslatorMap.put(Integer.class, integerFieldTranslator);
        fieldTranslatorMap.put(int.class, integerFieldTranslator);
        fieldTranslatorMap.put(Float.class, floatFieldTranslator);
        fieldTranslatorMap.put(float.class, floatFieldTranslator);
        fieldTranslatorMap.put(String.class, noCastFieldTranslator);
        fieldTranslatorMap.put(Double.class, noCastFieldTranslator);
        fieldTranslatorMap.put(double.class, noCastFieldTranslator);
        fieldTranslatorMap.put(Long.class, noCastFieldTranslator);
        fieldTranslatorMap.put(long.class, noCastFieldTranslator);
        fieldTranslatorMap.put(Date.class, noCastFieldTranslator);
        fieldTranslatorMap.put(Boolean.class, noCastFieldTranslator);
        fieldTranslatorMap.put(boolean.class, noCastFieldTranslator);
        fieldTranslatorMap.put(Text.class, noCastFieldTranslator);
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
                            if (List.class.equals(descriptor.getPropertyType())) {
                                // Field is a List class.  Let's see if we have to convert the values

                                if (getterMethod.getGenericReturnType() instanceof ParameterizedType) {
                                    ParameterizedType pType = (ParameterizedType) getterMethod.getGenericReturnType();
                                    final Type listClass = pType.getActualTypeArguments()[0];
                                    FieldTranslator fieldTranslator = fieldTranslatorMap.get(listClass);
                                    if (fieldTranslator == null) {
                                        throw new TranslatorNotFoundException("No FieldTranslator found for class: " + descriptor.getPropertyType());
                                    }
                                    List<Object> list = new ArrayList<>();
                                    for (Object listValue : (List) value) {
                                        list.add(fieldTranslator.castValue(listValue, object, propertyName));
                                    }
                                    castValue = list;
                                }
                            } else {
                                FieldTranslator fieldTranslator = fieldTranslatorMap.get(descriptor.getPropertyType());
                                if (fieldTranslator == null) {
                                    throw new TranslatorNotFoundException("No FieldTranslator found for class: " + descriptor.getPropertyType());
                                }

                                log.debug("Value to Translate: " + value);
                                castValue = fieldTranslator.castValue(value, object, propertyName);
                                log.debug("Translated Value: " + castValue);
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

    public Map<Class, FieldTranslator> getFieldTranslatorMap() {
        return fieldTranslatorMap;
    }
}
