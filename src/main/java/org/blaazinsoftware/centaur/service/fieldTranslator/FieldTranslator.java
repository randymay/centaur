package org.blaazinsoftware.centaur.service.fieldTranslator;

/**
 * @author Randy May
 *         Date: 15-04-06
 */
public interface FieldTranslator<T> {
    public T castValue(Object valueToTranslate, Object parentObject, String propertyName);

}
