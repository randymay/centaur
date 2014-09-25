package org.blaazinsoftware.centaur.data.util;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.blaazinsoftware.centaur.service.SimpleEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EntityTranslatorTest {

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

    @Test
    public void testToEntityWithEmptyKey() {
        SimpleEntity simpleClass = new SimpleEntity();

        final String name = "name";
        final String description = "description";
        simpleClass.setName(name);
        simpleClass.setShortDescription(description);

        Entity entity = new EntityTranslator().toEntity(simpleClass);

        assertEquals(0, entity.getKey().getId());
        assertEquals(simpleClass.getKind(), entity.getKind());
        assertEquals(simpleClass.getName(), entity.getProperty("name"));
        assertEquals(simpleClass.getShortDescription(), entity.getProperty("shortDescription"));
    }

    @Test
    public void testFromEntityWithEmptyKey() {
        final String name = "name";
        final String description = "description";

        Entity entity = new Entity(SimpleEntity.class.getSimpleName());

        entity.setProperty("name", name);
        entity.setProperty("shortDescription", description);

        SimpleEntity simpleClass = new EntityTranslator().fromEntity(entity, SimpleEntity.class);

        assertEquals(0, entity.getKey().getId());
        assertEquals(simpleClass.getKind(), entity.getKind());
        assertEquals(simpleClass.getName(), entity.getProperty("name"));
        assertEquals(simpleClass.getShortDescription(), entity.getProperty("shortDescription"));
    }
}