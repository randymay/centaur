package org.blaazinsoftware.centaur.annotation;

import java.lang.annotation.*;

/**
 * Indicates that a field is to be ignored by Centaur.
 * The contents of this field will not be stored or retrieved.
 *
 * @author Randy May
 *         Date: 14-12-01
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AppEngineIgnore {
}
