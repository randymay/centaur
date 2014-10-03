package org.blaazinsoftware.centaur.service;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.blaazinsoftware.centaur.CentaurException;
import org.blaazinsoftware.centaur.data.dto.MapEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Randy May <a href="www.blaazinsoftware.com">Blaazin Software Consulting, Inc.</a>
 */
public interface EntityTranslator {

    public <T> Entity toEntity(final T object) throws CentaurException;

    public <T> Entity toEntity(final T object, Key parentKey) throws CentaurException;

    public <T> T fromEntity(final Entity entity, Class<?> klass) throws CentaurException;
}
