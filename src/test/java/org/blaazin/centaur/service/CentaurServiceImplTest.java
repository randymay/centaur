package org.blaazin.centaur.service;

import org.blaazin.centaur.CentaurException;
import org.blaazin.centaur.data.dto.MapEntity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class CentaurServiceImplTest {

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
    public void testSimpleCRUD() throws Exception {
        SimpleEntity simpleEntity = new SimpleEntity();

        String name = "name";
        String description = "description";

        simpleEntity.setName(name);
        simpleEntity.setShortDescription(description);

        service.save(simpleEntity);

        Key key = simpleEntity.getAppEngineKey();
        assertNotNull(key);

        simpleEntity = service.getObject(key, SimpleEntity.class);
        assertNotNull(simpleEntity);
        assertEquals(key, simpleEntity.getAppEngineKey());
        assertEquals(name, simpleEntity.getName());
        assertEquals(description, simpleEntity.getShortDescription());

        name = "new name";
        simpleEntity.setName(name);
        service.save(simpleEntity);
        simpleEntity = service.getObject(key, SimpleEntity.class);
        assertNotNull(simpleEntity);
        assertEquals(key, simpleEntity.getAppEngineKey());
        assertEquals(name, simpleEntity.getName());
        assertEquals(description, simpleEntity.getShortDescription());

        service.deleteObject(simpleEntity);
        try {
            assertNull(service.getObject(key, SimpleEntity.class));
            fail("This method should have thrown an EntityNotFoundException");
        } catch (CentaurException e) {
            assertTrue(e.getMessage().startsWith("com.google.appengine.api.datastore.EntityNotFoundException: No entity was found matching the key: "));
        }
    }

    @Test
    public void testHeirarchicalCRUD() throws Exception {
        ParentEntity parentEntity = new ParentEntity();

        String parentName = "parentName";
        String parentDescription = "parentDescription";

        Key parentKey = service.save(parentEntity);
        assertNotNull(parentKey);

        parentEntity.setName(parentName);
        parentEntity.setShortDescription(parentDescription);

        String childName = "name";
        String childDescription = "description";

        for (int i = 0; i < 10; i++) {
            SimpleEntity simpleEntity = new SimpleEntity();
            simpleEntity.setName(childName + i);
            simpleEntity.setShortDescription(childDescription + i);
            Key key = service.saveChild(parentEntity, simpleEntity);
            assertNotNull(key);
            assertNotNull(simpleEntity.getAppEngineKey());
        }

        List<SimpleEntity> simpleEntities = service.getChildren(SimpleEntity.class.getSimpleName(), parentEntity, SimpleEntity.class);

        assertEquals(10, simpleEntities.size());
        for (int i = 0; i < simpleEntities.size(); i++) {
            SimpleEntity listEntity = simpleEntities.get(i);

            assertEquals(childName + i, listEntity.getName());
            assertEquals(childDescription + i, listEntity.getShortDescription());
        }
    }

    @Test
    public void testHeirarchicalCRUDNoNames() throws Exception {
        ParentEntity parentEntity = new ParentEntity();

        String parentDescription = "parentDescription";

        Key parentKey = service.save(parentEntity);
        assertNotNull(parentKey);

        parentEntity.setShortDescription(parentDescription);

        String childDescription = "description";

        for (int i = 0; i < 10; i++) {
            SimpleEntity simpleEntity = new SimpleEntity();
            simpleEntity.setShortDescription(childDescription + i);
            Key key = service.saveChild(parentEntity, simpleEntity);
            assertNotNull(key);
            assertNotNull(simpleEntity.getAppEngineKey());
        }

        List<SimpleEntity> simpleEntities = service.getChildren(SimpleEntity.class.getSimpleName(), parentEntity, SimpleEntity.class);

        assertEquals(10, simpleEntities.size());
        for (int i = 0; i < simpleEntities.size(); i++) {
            SimpleEntity listEntity = simpleEntities.get(i);

            assertEquals(childDescription + i, listEntity.getShortDescription());
        }
    }

    @Test
    public void testToAndFromEntityWithIntegerProperty() throws Exception {
        final int userId = 1;

        EntityWithIntegerField entityWithIntegerField = new EntityWithIntegerField();
        entityWithIntegerField.setUserId(userId);

        service.save(entityWithIntegerField);

        entityWithIntegerField = service.getObject(EntityWithIntegerField.class.getSimpleName(), "userId", userId, EntityWithIntegerField.class);
        assertNotNull(entityWithIntegerField);
        assertNotNull(entityWithIntegerField.getUserId());
        assertEquals(userId, entityWithIntegerField.getUserId());
    }

    @Test
    public void testToAndFromEntityWithListOfLongsProperty() throws Exception {
        EntityWithListOfLongField object = new EntityWithListOfLongField();
        List<Long> userIds = new ArrayList<>();
        userIds.add(7l);
        userIds.add(6l);
        userIds.add(5l);
        userIds.add(4l);
        userIds.add(3l);
        userIds.add(3l);
        userIds.add(1l);
        userIds.add(0l);
        object.setUserIds(userIds);

        service.save(object);

        object = service.getObject(object.getAppEngineKey(), EntityWithListOfLongField.class);
        assertNotNull(object);
        assertNotNull(object.getUserIds());
        assertEquals(8, object.getUserIds().size());

        for (Integer index = 7; index >= 0; index--) {
            assertEquals(userIds.get(index), object.getUserIds().get(index));
        }
    }

    @Test
    public void testToAndFromEntityWithListOfIntegersProperty() throws Exception {
        EntityWithListOfIntegerField object = new EntityWithListOfIntegerField();
        List<Integer> userIds = new ArrayList<>();
        userIds.add(7);
        userIds.add(6);
        userIds.add(5);
        userIds.add(4);
        userIds.add(3);
        userIds.add(3);
        userIds.add(1);
        userIds.add(0);
        object.setUserIds(userIds);

        service.save(object);

        object = service.getObject(object.getAppEngineKey(), EntityWithListOfIntegerField.class);
        assertNotNull(object);
        assertNotNull(object.getUserIds());
        assertEquals(8, object.getUserIds().size());

        for (Integer index = 7; index >= 0; index--) {
            assertEquals(userIds.get(index), object.getUserIds().get(index));
        }
    }

    @Test
    public void testToAndFromEntityWithBooleanProperties() throws Exception {
        EntityWithBooleanFields object = new EntityWithBooleanFields();
        object.setBooleanValue1(true);
        object.setBooleanValue2(false);

        service.save(object);

        object = service.getObject(object.getAppEngineKey(), EntityWithBooleanFields.class);
        assertNotNull(object);
        assertTrue(object.isBooleanValue1());
        assertFalse(object.getBooleanValue2());

        object.setBooleanValue1(false);
        object.setBooleanValue2(true);

        service.save(object);

        object = service.getObject(object.getAppEngineKey(), EntityWithBooleanFields.class);
        assertNotNull(object);
        assertFalse(object.isBooleanValue1());
        assertTrue(object.getBooleanValue2());
    }

    @Test
    public void testGetObjectsByPropertyValue() throws Exception {
        String childName = "name";
        String childDescription = "description";

        for (int i = 0; i < 20; i++) {
            SimpleEntity simpleEntity = new SimpleEntity();
            simpleEntity.setName(childName + i);
            if (i <= 10) {
                childDescription += i;
            }
            simpleEntity.setShortDescription(childDescription);
            service.save(simpleEntity);
        }

        List<SimpleEntity> objects =
                service.getObjects(
                        SimpleEntity.class.getSimpleName(),
                        "shortDescription",
                        childDescription,
                        SimpleEntity.class);

        assertNotNull(objects);
        assertEquals(10, objects.size());
    }

    @Test
    public void testGetObjectsByPropertyValues() throws Exception {
        String childDescription = "description";

        for (int i = 0; i < 20; i++) {
            EntityWithStringAndIntegerField simpleEntity = new EntityWithStringAndIntegerField();
            simpleEntity.setName("" + i);
            simpleEntity.setIntValue(0);
            if (i <= 10) {
                simpleEntity.setStringValue(childDescription + i);
                simpleEntity.setIntValue(i);
            }
            simpleEntity.setStringValue(childDescription);
            service.save(simpleEntity);
        }

        Map<String, Object> keyValues = new HashMap<>();
        keyValues.put("intValue", 0);
        keyValues.put("stringValue", childDescription);

        List<EntityWithStringAndIntegerField> objects =
                service.getObjects(
                        EntityWithStringAndIntegerField.class.getSimpleName(),
                        keyValues,
                        EntityWithStringAndIntegerField.class);

        assertNotNull(objects);
        assertEquals(10, objects.size());
    }

    @Test
    public void testToAndFromMapEntity() throws Exception {
        MapEntity simpleEntity = new MapEntity();

        simpleEntity.put("key1", "value1");
        simpleEntity.put("key2", 2l);

        service.save(simpleEntity);

        Key key = simpleEntity.getAppEngineKey();
        assertNotNull(key);

        simpleEntity = service.getObject(key, MapEntity.class);
        assertNotNull(simpleEntity);
        assertEquals(key, simpleEntity.getAppEngineKey());
        assertTrue(simpleEntity.containsKey("key1"));
        assertEquals("value1", simpleEntity.get("key1"));
        assertTrue(simpleEntity.containsKey("key2"));
        assertEquals(2l, simpleEntity.get("key2"));
    }

    @Test
    public void testMultipleMapEntities() throws Exception {
        MapEntity firstMapEntity = new MapEntity();
        firstMapEntity.setName("FirstEntity");

        firstMapEntity.put("key1", "value1");
        firstMapEntity.put("key2", 2l);

        service.save(firstMapEntity);

        Key key1 = firstMapEntity.getAppEngineKey();
        assertNotNull(key1);

        MapEntity secondMapEntity = new MapEntity();
        secondMapEntity.setName("SecondEntity");

        secondMapEntity.put("key3", "value3");
        secondMapEntity.put("key4", 4l);

        service.save(secondMapEntity);

        Key key2 = secondMapEntity.getAppEngineKey();
        assertNotNull(key2);

        firstMapEntity = service.getObject(key1, MapEntity.class);
        secondMapEntity = service.getObject(key2, MapEntity.class);
        assertNotNull(firstMapEntity);
        assertEquals(key1, firstMapEntity.getAppEngineKey());
        assertTrue(firstMapEntity.containsKey("key1"));
        assertEquals("value1", firstMapEntity.get("key1"));
        assertTrue(firstMapEntity.containsKey("key2"));
        assertEquals(2l, firstMapEntity.get("key2"));

        assertNotNull(secondMapEntity);
        assertEquals(key2, secondMapEntity.getAppEngineKey());
        assertTrue(secondMapEntity.containsKey("key3"));
        assertEquals("value3", secondMapEntity.get("key3"));
        assertTrue(secondMapEntity.containsKey("key4"));
        assertEquals(4l, secondMapEntity.get("key4"));
    }

    @Test
    public void testGetObject() throws Exception {
        String kind = SimpleEntity.class.getSimpleName();
        String description = "Description for testGetObject";

        SimpleEntity expected = new SimpleEntity();
        expected.setLongDescription(description);

        Key key = service.save(expected);
        assertNotNull(key);
        assertNotNull(key.getId());
        long id = key.getId();

        SimpleEntity actual = service.getObject(kind, id, SimpleEntity.class);
        assertNotNull(actual);
        assertEquals(id, actual.getAppEngineKey().getId());
        assertEquals(description, actual.getLongDescription());
    }

    @Test
    public void testGetObjectByName() throws Exception {
        String name = UUID.randomUUID().toString();
        String description = "Description for testGetObject";

        SimpleEntity expected = new SimpleEntity();
        expected.setName(name);
        expected.setLongDescription(description);

        Key key = service.save(expected);
        assertNotNull(key);
        assertNotNull(key.getId());
        long id = key.getId();

        SimpleEntity actual = service.getObject(name, SimpleEntity.class);
        assertNotNull(actual);
        assertEquals(id, actual.getAppEngineKey().getId());
        assertEquals(name, actual.getName());
        assertEquals(name, actual.getAppEngineKey().getName());
        assertEquals(description, actual.getLongDescription());
    }

    @Test
    public void testTransaction() throws Exception {
        Transaction transaction = service.beginTransaction();

        String kind = SimpleEntity.class.getSimpleName();
        String description = "Description for testGetObject";

        SimpleEntity expected = new SimpleEntity();
        expected.setLongDescription(description);

        Key key = service.save(expected, transaction);

        assertNotNull(key);
        assertNotNull(key.getId());
        long id = key.getId();

        try {
            service.getObject(kind, id, SimpleEntity.class);
            fail("This should throw an exception");
        } catch (CentaurException e) {
            // This is expected behaviour
            assertTrue(e.getCause() instanceof EntityNotFoundException);
        }

        service.rollback(transaction);
        transaction = service.beginTransaction();

        try {
            service.getObject(kind, id, SimpleEntity.class);
            fail("This should throw an exception");
        } catch (CentaurException e) {
            // This is expected behaviour
            assertTrue(e.getCause() instanceof EntityNotFoundException);
        }

        service.save(expected, transaction);
        service.commit(transaction);

        assertNotNull(key);
        assertNotNull(key.getId());
        id = key.getId();

        SimpleEntity actual = service.getObject(kind, id, SimpleEntity.class);
        assertNotNull(actual);
        assertEquals(id, actual.getAppEngineKey().getId());
        assertEquals(description, actual.getLongDescription());
    }


    @Test
    public void testUserCRUD() throws Exception {
        UserEntity userEntity = new UserEntity();

        service.save(userEntity);

        Key userKey = userEntity.getAppEngineKey();

        assertNotNull(userKey);
        assertNotNull(service.getObject(userKey, SimpleEntity.class));
        assertNull(userEntity.getFirstName());

        userEntity.setFirstName("name");
        service.save(userEntity);
        userEntity = service.getObject(userKey, UserEntity.class);
        assertEquals("name", userEntity.getFirstName());

        service.deleteObject(userEntity);

        try {
            service.getObject(userKey, UserEntity.class);
            fail("This should have thrown an Exception");
        } catch (CentaurException e) {
            // This is expected behaviour
        }

        userEntity = new UserEntity();

        service.save(userEntity);

        //accountService.create(account);
        //assertNotNull(account);
        //Key accountId = account.getId();
        //assertNotNull(accountId);

        userKey = userEntity.getAppEngineKey();

        assertNotNull(userKey);
        userEntity = service.getObject(userKey, UserEntity.class);
        assertNotNull(userEntity);
    }
}