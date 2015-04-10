package org.blaazinsoftware.centaur.service;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import org.apache.commons.beanutils.PropertyUtils;
import org.blaazinsoftware.centaur.CentaurException;
import org.blaazinsoftware.centaur.exception.TranslatorNotFoundException;
import org.blaazinsoftware.centaur.service.fieldTranslator.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Default implementation of <code>DocumentTranslator</code>
 *
 * @author Randy May
 */
public class DefaultDocumentTranslatorImpl implements DocumentTranslator {
    private static final Logger log = LoggerFactory.getLogger(DefaultDocumentTranslatorImpl.class);

    private Map<Class, FieldTranslator> fieldTranslatorMap = new HashMap<>();

    public DefaultDocumentTranslatorImpl() {
        final IntegerFieldTranslator integerFieldTranslator = new IntegerFieldTranslator();
        final FloatFieldTranslator floatFieldTranslator = new FloatFieldTranslator();
        final NoCastFieldTranslator noCastFieldTranslator = new NoCastFieldTranslator();
        final TextCastFieldTranslator textCastFieldTranslator = new TextCastFieldTranslator();
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
        fieldTranslatorMap.put(Text.class, textCastFieldTranslator);
    }

    @SuppressWarnings("unchecked")
    public <T> Document toDocument(final T object) throws CentaurException {
        if (null == object) {

            return null;
        }
        if (log.isTraceEnabled()) {
            log.trace("Attempting to convert " + object + " to a Document");
        }

        Document.Builder documentBuilder = Document.newBuilder();

        Key key = CentaurServiceUtils.getKey(object);

        if (null != key) {
            documentBuilder = documentBuilder.setId(CentaurServiceUtils.keyToString(key));
        }

        if (object instanceof Map) {
            //documentBuilder.addField(Field.newBuilder().setName()) setProperty("name", CentaurServiceUtils.getNameValue(object));
            Map<String, Object> mapValues = (Map) object;
            for (Map.Entry<String, Object> entry : mapValues.entrySet()) {
                addField(documentBuilder, object, entry.getKey(), entry.getValue());
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
                            addField(documentBuilder, object, propertyName, value);
                        }
                    } catch (Exception e) {
                        log.warn(e.getMessage(), e);
                    }

                }

            }
        }

        return documentBuilder.build();
    }

    private <T> void addField(Document.Builder documentBuilder, T originalObject, String fieldName, Object value) {
        Field.Builder builder = Field.newBuilder();
        builder.setName(fieldName);

        // Cast the value to the correct type for this field
        if (value != null) {
            if (value.getClass().equals(Date.class)) {
                builder.setDate((Date) value);
            } else if (value instanceof Number) {
                builder.setNumber((Double) value);
            } else {
                if (value instanceof Text) {
                    builder.setText(((Text)value).getValue());
                } else {
                    builder.setText(value.toString());
                }
            }
        }

        documentBuilder.addField(builder);
    }

    @SuppressWarnings("unchecked")
    public <T> T fromDocument(final Document document, Class<T> klass) throws CentaurException {
        if (null == document) {
            return null;
        }
        if (log.isTraceEnabled()) {
            log.trace("Attempting to convert an Document to " + klass);
        }
        T object;
        try {
            object = (T) klass.newInstance();
        } catch (Exception e) {
            log.error("Error creating an instance of " + klass.getName(), e);
            return null;
        }

        if (document.getId() != null) {
            CentaurServiceUtils.setKey(object, KeyFactory.stringToKey(document.getId()));
        }

        for (Field field : document.getFields()) {
            /*if (object instanceof MapEntity) {
                ((MapDocument) object).put(entry.getKey(), entry.getValue());
                continue;
            }*/
            String propertyName = field.getName();
            if (!"key".equals(propertyName) && !"kind".equals(propertyName) && !"appEngineKey".equals(propertyName)) {
                try {
                    if (log.isTraceEnabled()) {
                        log.trace("Processing property '" + propertyName + "'");
                    }
                    PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(object, propertyName);
                    Method setterMethod = PropertyUtils.getWriteMethod(descriptor);
                    Method getterMethod = PropertyUtils.getReadMethod(descriptor);

                    if (null == setterMethod) {
                        log.warn("No setter method found for property '" + propertyName + "'");
                    } else {
                        Object value = getValueFromField(field, descriptor.getPropertyType());
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

    private Object getValueFromField(Field field, Class<?> expectedReturnType) {
        if (expectedReturnType.equals(Date.class)) {
            return field.getDate();
        } else if (expectedReturnType.isInstance(Number.class)) {
            return field.getNumber();
        } else {
            return field.getText();
        }
    }

    public Map<Class, FieldTranslator> getFieldTranslatorMap() {
        return fieldTranslatorMap;
    }
}
