package org.blaazinsoftware.centaur.service.fieldTranslator;

/**
 * @author Randy May
 *         Date: 15-04-06
 */
public class FloatFieldTranslator implements FieldTranslator<Float> {
    public Float castValue(Object valueToTranslate, Object parentObject, String propertyName) {
        return ((Double) valueToTranslate).floatValue();
    }
}
