package com.blaazinsoftware.centaur.data;

import com.blaazinsoftware.centaur.data.service.DataService;
import com.blaazinsoftware.centaur.data.service.DataServiceFactory;
import com.blaazinsoftware.centaur.data.service.SimpleEntity;
//import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
//import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test Class for RefList
 */
public class RefListTest {

//    private final LocalServiceTestHelper helper =
//            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    private DataService service = DataServiceFactory.getInstance();

    private Closeable session;

    @Before
    public void setUp() {
//        helper.setUp();

        session = ObjectifyService.begin();

        ObjectifyService.register(SimpleEntity.class);
    }

    @After
    public void tearDown() {
//        helper.tearDown();

        session.close();
    }

    @Test
    public void fromList() throws Exception {

        List<SimpleEntity> entities = new ArrayList<>();
        for (int index = 0; index < 100; index++) {
            SimpleEntity entity = new SimpleEntity();
            service.saveForId(entity);
            entities.add(entity);
        }

        RefList<SimpleEntity> entityRefList = RefList.fromList(entities);
        assertNotNull(entityRefList);
        assertEquals(100, entityRefList.size());

        List<SimpleEntity> deRefedEntities = entityRefList.deRef();
        for (int index = 0; index < 100; index++) {
            assertEquals(entities.get(index), deRefedEntities.get(index));
        }
    }
}