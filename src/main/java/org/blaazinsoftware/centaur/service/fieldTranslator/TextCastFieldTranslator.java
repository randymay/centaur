package org.blaazinsoftware.centaur.service.fieldTranslator;

import com.google.appengine.api.datastore.Text;

/**
 * @author Randy May
 *         Date: 15-04-06
 */
public class TextCastFieldTranslator implements FieldTranslator<Text> {
    public Text castValue(Object valueToTranslate, Object parentObject, String propertyName) {
        if (valueToTranslate instanceof String) {
            return new Text((String)valueToTranslate);
        }
        return new Text(valueToTranslate.toString());
    }
}
