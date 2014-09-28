package org.blaazinsoftware.centaur.annotation;

import java.lang.annotation.*;

/**
 * Interface to define the assigned field as the Key field to use
 * with Google App Engine's Data Store
 *
 * @author Randy May <a href="www.blaazinsoftware.com">Blaazin Software Consulting, Inc.</a>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AppEngineKey {
}
