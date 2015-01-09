package org.blaazinsoftware.centaur.data.util;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.blaazinsoftware.centaur.service.DefaultEntityTranslatorImpl;
import org.blaazinsoftware.centaur.service.EntityTranslator;
import org.blaazinsoftware.centaur.service.SimpleEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultEntityTranslatorTest {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    private EntityTranslator entityTranslator = new DefaultEntityTranslatorImpl();

    @Test
    public void testToEntityWithEmptyKey() throws Exception {
        SimpleEntity simpleClass = new SimpleEntity();

        final String name = "name";
        final String description = "description";
        simpleClass.setName(name);
        simpleClass.setShortDescription(description);

        Entity entity = entityTranslator.toEntity(simpleClass);

        assertNotNull(entity);
    }

    @Test
    public void testToEntityWithEmptyKeyKindName() throws Exception {
        SimpleEntity simpleClass = new SimpleEntity();

        simpleClass.setName(null);
        simpleClass.setKind(null);

        try {
            entityTranslator.toEntity(simpleClass);
            fail("Should have thrown an Exception");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    @Test
    public void testFromEntityWithEmptyKey() throws Exception {
        final String name = "name";
        final String description = "description";

        Entity entity = new Entity(SimpleEntity.class.getSimpleName());

        entity.setProperty("name", name);
        entity.setProperty("shortDescription", description);

        SimpleEntity simpleClass = entityTranslator.fromEntity(entity, SimpleEntity.class);

        assertEquals(0, entity.getKey().getId());
        assertEquals(simpleClass.getKind(), entity.getKind());
        assertEquals(simpleClass.getName(), entity.getProperty("name"));
        assertEquals(simpleClass.getShortDescription(), entity.getProperty("shortDescription"));
    }
}