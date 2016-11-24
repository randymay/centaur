package com.blaazinsoftware.centaur.cache.service;

import com.blaazinsoftware.centaur.data.service.EntityWithAllFields;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Randy May
 *         Date: 2016-01-09
 */
public class DefaultCacheServiceImplTest {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    private CacheService service = CacheServiceFactory.getInstance();

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testCaching() throws Exception {
        final long id = 1235123451;
        final int intValue = 1;
        final Integer integerWrapperValue = 2;
        final float floatValue = 3f;
        final Float floatWrapperValue = 4f;
        final double doubleValue = 5d;
        final Double doubleWrapperValue = 6d;

        EntityWithAllFields entity = new EntityWithAllFields();
        entity.setId(id);
        entity.setIntField(intValue);
        entity.setIntegerWrapperField(integerWrapperValue);
        entity.setFloatField(floatValue);
        entity.setFloatWrapperField(floatWrapperValue);
        entity.setDoubleField(doubleValue);
        entity.setDoubleWrapperField(doubleWrapperValue);

        String key = KeyFactory.createKey(EntityWithAllFields.class.getCanonicalName(), id).toString();
        service.cacheEntity(key, entity);

        assertNotNull(key);
        final EntityWithAllFields objectFromCache = service.getEntityFromCache(key);
        assertNotNull(objectFromCache);
        assertEquals(entity, objectFromCache);

        service.unCacheEntity(key);
        assertNull(service.getEntityFromCache(key));
    }
}