package org.blaazinsoftware.centaur.service;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.blaazinsoftware.centaur.CentaurException;
import org.blaazinsoftware.centaur.annotation.AppEngineKind;
import org.blaazinsoftware.centaur.annotation.AppEngineName;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class CentaurServiceUtils {
    protected static <T> Key getKey(T object) throws CentaurException {
        for (PropertyDescriptor descriptor : PropertyUtils.getPropertyDescriptors(object)) {
            if (Key.class.equals(descriptor.getPropertyType())) {
                Method readMethod = PropertyUtils.getReadMethod(descriptor);

                if (readMethod == null) {
                    throw new CentaurException("No read property found for the type: " + Key.class.getSimpleName());
                } else {
                    try {
                        return (Key) readMethod.invoke(object);
                    } catch (Exception e) {
                        throw new CentaurException(e);
                    }
                }
            }
        }

        return null;
    }

    protected static <T> void setKey(T object, Key key) throws CentaurException {
        Method writeMethod = PropertyUtils.getWriteMethod(CentaurServiceUtils.getKeyPropertyDescriptor(object));

        if (writeMethod == null) {
            throw new CentaurException("No read property found for the type: " + Key.class.getSimpleName());
        } else {
            try {
                writeMethod.invoke(object, key);
            } catch (Exception e) {
                throw new CentaurException(e);
            }
        }
    }

    private static <T> PropertyDescriptor getKeyPropertyDescriptor(T object) throws CentaurException {
        return CentaurServiceUtils.getPropertyDescriptor(object, Key.class);
    }

    private static <T> PropertyDescriptor getPropertyDescriptor(T object, Class<?> klass) throws CentaurException {
        for (PropertyDescriptor descriptor : PropertyUtils.getPropertyDescriptors(object)) {
            if (klass.equals(descriptor.getPropertyType())) {
                return descriptor;
            }
        }

        return null;
    }

    protected static List<Field> getFieldsByAnnotation(Class<?> klass, Class<? extends Annotation> annotation)
            throws CentaurException {

        List<Field> fields = new ArrayList<>();
        for (Field field : getAllFieldsInClassAndSuperClass(klass)) {
            // Using isAnnotationPresent method from Field class.
            if (field.isAnnotationPresent(annotation)) {
                fields.add(field);
            }
        }

        return fields;
    }

    protected static List<Field> getAllFieldsInClassAndSuperClass(Class<?> klass) {
        List<Field> fields = new ArrayList<>();
        final Field[] declaredFields = klass.getDeclaredFields();
        fields.addAll(Arrays.asList(declaredFields));

        final Class<?> superclass = klass.getSuperclass();
        if (null != superclass) {
            fields.addAll(CentaurServiceUtils.getAllFieldsInClassAndSuperClass(superclass));
        }

        return fields;
    }

    protected static <T> void initKey(T object) throws CentaurException {
        if (null == CentaurServiceUtils.getKey(object)) {
            Key key = createKey(object);
            CentaurServiceUtils.setKey(object, key);
        }
    }

    protected static <T> Key createKey(T object) throws CentaurException {
        String kind = CentaurServiceUtils.getKindValue(object);
        String name = CentaurServiceUtils.getNameValue(object);
        if (object != null && !StringUtils.isEmpty(kind) && !StringUtils.isEmpty(name)) {
            return KeyFactory.createKey(kind, name);
        }

        return null;
    }

    protected static <T, X> Key createKey(X parent, T object) throws CentaurException {
        String kind = CentaurServiceUtils.getKindValue(object);
        String name = CentaurServiceUtils.getNameValue(object);
        if (object != null && !StringUtils.isEmpty(kind) && !StringUtils.isEmpty(name)) {
            Entity parentEntity = new DefaultEntityTranslatorImpl().toEntity(parent);
            return KeyFactory.createKey(parentEntity.getKey(), kind, name);
        }

        return null;
    }

    protected static <T> String getKindValue(T object) throws CentaurException {
        try {
            Field kindField = CentaurServiceUtils.getSingleFieldByAnnotation(object, AppEngineKind.class);

            return CentaurServiceUtils.getStringValue(object, kindField);
        } catch (Exception e) {
            throw new CentaurException(e);
        }
    }

    protected static <T> String getNameValue(T object) throws CentaurException {
        try {
            Field kindField = CentaurServiceUtils.getSingleFieldByAnnotation(object, AppEngineName.class);

            return CentaurServiceUtils.getStringValue(object, kindField);
        } catch (Exception e) {
            throw new CentaurException(e);
        }
    }

    protected static String keyToString(Key key) {
        if (key == null) {
            return null;
        }
        return KeyFactory.keyToString(key);
    }

    private static <T> Field getSingleFieldByAnnotation(T object, Class<? extends Annotation> annotation) throws CentaurException {
        return CentaurServiceUtils.getSingleFieldByAnnotation(object.getClass(), annotation);
    }

    private static Field getSingleFieldByAnnotation(Class<?> klass, Class<? extends Annotation> annotation) throws CentaurException {
        List<Field> fields = CentaurServiceUtils.getFieldsByAnnotation(klass, annotation);
        if (fields.size() == 0) {
            throw new IllegalArgumentException(annotation.getSimpleName() + " is missing");
        } else if (fields.size() > 1) {
            throw new IllegalArgumentException("Only one field of type " + annotation.getSimpleName() + " is allowed");
        }

        return fields.get(0);
    }

    private static <T> String getStringValue(T object, Field field) throws CentaurException {
        try {
            if (!String.class.equals(field.getType())) {
                throw new IllegalArgumentException("Field must be String");
            }

            PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(object, field.getName());

            Method method = descriptor.getReadMethod();

            if (null == method) {
                throw new IllegalArgumentException("Field marked as " + field.getName() +" missing, or no Read Method found");
            }

            Object value = method.invoke(object);

            if (null == value) {
                return null;
            } else {
                return value.toString();
            }
        } catch (Exception e) {
            throw new CentaurException(e);
        }
    }
}
