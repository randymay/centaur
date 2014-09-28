package org.blaazinsoftware.centaur.service;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;


public class CentaurServiceUtilsTest {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    private CentaurService service = CentaurServiceFactory.newInstance();

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testGetAndSetKey() throws Exception {
        SimpleEntity entity = new SimpleEntity();

        // Null Key
        Key expected = CentaurServiceUtils.createKey(entity);
        assertNull(expected);

        // Simple Key
        entity.setName("name");
        entity.setKind("kind");
        expected = CentaurServiceUtils.createKey(entity);
        assertNotNull(expected);

        CentaurServiceUtils.setKey(entity, expected);

        Key actual = CentaurServiceUtils.getKey(entity);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

}