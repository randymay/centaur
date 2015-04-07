package org.blaazinsoftware.centaur.service.fieldTranslator;

/**
 * @author Randy May
 *         Date: 15-04-06
 */
public class IntegerFieldTranslator implements FieldTranslator<Integer> {
    public Integer castValue(Object valueToTranslate, Object parentObject, String propertyName) {
        return ((Long) valueToTranslate).intValue();
    }
}
