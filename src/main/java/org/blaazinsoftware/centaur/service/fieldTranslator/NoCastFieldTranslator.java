package org.blaazinsoftware.centaur.service.fieldTranslator;

/**
 * @author Randy May
 *         Date: 15-04-06
 */
public class NoCastFieldTranslator implements FieldTranslator<Object> {
    public Object castValue(Object valueToTranslate, Object parentObject, String propertyName) {
        return valueToTranslate;
    }
}
