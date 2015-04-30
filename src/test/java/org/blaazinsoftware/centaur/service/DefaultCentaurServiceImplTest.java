package org.blaazinsoftware.centaur.service;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.apache.commons.lang3.time.DateUtils;
import org.blaazinsoftware.centaur.CentaurException;
import org.blaazinsoftware.centaur.data.dto.MapEntity;
import org.blaazinsoftware.centaur.search.SortCriteria;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class DefaultCentaurServiceImplTest {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    private DefaultCentaurServiceImpl service = (DefaultCentaurServiceImpl) CentaurServiceFactory.newInstance();

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

        String key = service.save(simpleEntity);
        assertNotNull(key);

        simpleEntity = service.getObject(key, SimpleEntity.class);
        assertNotNull(simpleEntity);
        assertEquals(name, simpleEntity.getName());
        assertEquals(description, simpleEntity.getShortDescription());

        name = "new name";
        simpleEntity.setName(name);
        service.save(simpleEntity);
        simpleEntity = service.getObject(key, SimpleEntity.class);
        assertNotNull(simpleEntity);
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
    public void testSimpleCRUDWithSearch() throws Exception {
        SimpleEntity simpleEntity = new SimpleEntity();

        String name = "name";
        String description = "description";
        String longDescription = "long description";

        simpleEntity.setName(name);
        simpleEntity.setShortDescription(description);
        simpleEntity.setLongDescription(new Text(longDescription));

        String key = service.saveAndIndex(simpleEntity);
        assertNotNull(key);

        com.google.appengine.api.search.Query.Builder queryBuilder = com.google.appengine.api.search.Query.newBuilder();
        QueryOptions.Builder queryOptionsBuilder = QueryOptions.newBuilder();
        QueryOptions queryOptions = queryOptionsBuilder.build();

        queryBuilder.setOptions(queryOptions);

        com.google.appengine.api.search.Query query = queryBuilder.build("name");
        SearchResults<SimpleEntity> searchResults = service.search(SimpleEntity.class, query);

        assertNotNull(searchResults);
        assertEquals(1, searchResults.getNumberReturned());

        SimpleEntity translated = searchResults.getResults().iterator().next();
        assertNotNull(translated);
        final Key appEngineKey = translated.getAppEngineKey();
        assertNotNull(appEngineKey);
        assertEquals(name, translated.getName());
        assertEquals(description, translated.getShortDescription());
        assertEquals(longDescription, translated.getLongDescriptionValue());

        name = "new name";
        simpleEntity.setName(name);
        service.saveAndIndex(simpleEntity);

        searchResults = service.search(SimpleEntity.class, query);
        translated = searchResults.getResults().iterator().next();
        assertNotNull(translated);
        assertEquals(appEngineKey, translated.getAppEngineKey());
        assertEquals(name, translated.getName());
        assertEquals(description, translated.getShortDescription());
        assertEquals(longDescription, translated.getLongDescriptionValue());

        service.deleteObject(simpleEntity);
        searchResults = service.search(SimpleEntity.class, query);
        assertFalse(searchResults.getResults().iterator().hasNext());
    }

    @Test
    public void testHeirarchicalCRUD() throws Exception {
        ParentEntity parentEntity = new ParentEntity();

        String parentName = "parentName";
        String parentDescription = "parentDescription";

        String parentKey = service.save(parentEntity);
        assertNotNull(parentKey);

        parentEntity.setName(parentName);
        parentEntity.setShortDescription(parentDescription);

        String childName = "name";
        String childDescription = "description";

        for (int i = 0; i < 10; i++) {
            SimpleEntity simpleEntity = new SimpleEntity();
            simpleEntity.setName(childName + i);
            simpleEntity.setShortDescription(childDescription + i);
            String key = service.saveChild(parentEntity, simpleEntity);
            assertNotNull(key);
            assertNotNull(simpleEntity.getAppEngineKey());
        }

        List<SimpleEntity> simpleEntities = service.getAllChildren(parentEntity, SimpleEntity.class);

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

        String parentKey = service.save(parentEntity);
        assertNotNull(parentKey);

        parentEntity.setShortDescription(parentDescription);

        String childDescription = "description";

        for (int i = 0; i < 10; i++) {
            SimpleEntity simpleEntity = new SimpleEntity();
            simpleEntity.setShortDescription(childDescription + i);
            String key = service.saveChild(parentEntity, simpleEntity);
            assertNotNull(key);
            assertNotNull(simpleEntity.getAppEngineKey());
        }

        List<SimpleEntity> simpleEntities = service.getAllChildren(SimpleEntity.class.getSimpleName(), parentEntity, SimpleEntity.class);

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

        entityWithIntegerField = service.findObject(EntityWithIntegerField.class.getSimpleName(), "userId", userId, EntityWithIntegerField.class);
        assertNotNull(entityWithIntegerField);
        assertNotNull(entityWithIntegerField.getUserId());
        assertEquals(userId, entityWithIntegerField.getUserId());
    }

    @Test
    public void testToAndFromEntityWithAllPropertyTypes() throws Exception {
        final int intValue = 1;
        final Integer integerWrapperValue = 2;
        final float floatValue = 3f;
        final Float floatWrapperValue = 4f;
        final double doubleValue = 5d;
        final Double doubleWrapperValue = 6d;

        EntityWithAllFields entity = new EntityWithAllFields();
        entity.setIntField(intValue);
        entity.setIntegerWrapperField(integerWrapperValue);
        entity.setFloatField(floatValue);
        entity.setFloatWrapperField(floatWrapperValue);
        entity.setDoubleField(doubleValue);
        entity.setDoubleWrapperField(doubleWrapperValue);

        String key = service.save(entity);

        entity = service.getObject(key, EntityWithAllFields.class);
        assertNotNull(entity);
        assertEquals(intValue, entity.getIntField());
        assertEquals(integerWrapperValue, entity.getIntegerWrapperField());
        assertTrue(floatValue == entity.getFloatField());
        assertEquals(floatWrapperValue, entity.getFloatWrapperField());
        assertTrue(doubleValue == entity.getDoubleField());
        assertEquals(doubleWrapperValue, entity.getDoubleWrapperField());
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

        String key = service.save(object);

        object = service.getObject(key, EntityWithListOfLongField.class);
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

        String key = service.save(object);

        object = service.getObject(key, EntityWithListOfIntegerField.class);
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

        String key = service.save(object);

        object = service.getObject(key, EntityWithBooleanFields.class);
        assertNotNull(object);
        assertTrue(object.isBooleanValue1());
        assertFalse(object.getBooleanValue2());

        object.setBooleanValue1(false);
        object.setBooleanValue2(true);

        key = service.save(object);

        object = service.getObject(key, EntityWithBooleanFields.class);
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
                service.findObjects(
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
                service.findObjects(
                        EntityWithStringAndIntegerField.class.getSimpleName(),
                        keyValues,
                        EntityWithStringAndIntegerField.class);

        assertNotNull(objects);
        assertEquals(10, objects.size());
    }

    @Test
    public void testToAndFromMapEntity() throws Exception {
        MapEntity mapEntity = new MapEntity();

        mapEntity.put("key1", "value1");
        mapEntity.put("key2", 2l);

        String key = service.save(mapEntity);
        assertNotNull(key);

        mapEntity = service.getObject(key, MapEntity.class);
        assertNotNull(mapEntity);
        assertTrue(mapEntity.containsKey("key1"));
        assertEquals("value1", mapEntity.get("key1"));
        assertTrue(mapEntity.containsKey("key2"));
        assertEquals(2l, mapEntity.get("key2"));
    }

    @Test
    public void testMultipleMapEntities() throws Exception {
        MapEntity firstMapEntity = new MapEntity();
        firstMapEntity.setName("FirstEntity");

        firstMapEntity.put("key1", "value1");
        firstMapEntity.put("key2", 2l);

        String key1 = service.save(firstMapEntity);
        assertNotNull(key1);

        MapEntity secondMapEntity = new MapEntity();
        secondMapEntity.setName("SecondEntity");

        secondMapEntity.put("key3", "value3");
        secondMapEntity.put("key4", 4l);

        String key2 = service.save(secondMapEntity);
        assertNotNull(key2);

        firstMapEntity = service.getObject(key1, MapEntity.class);
        secondMapEntity = service.getObject(key2, MapEntity.class);
        assertNotNull(firstMapEntity);
        assertTrue(firstMapEntity.containsKey("key1"));
        assertEquals("value1", firstMapEntity.get("key1"));
        assertTrue(firstMapEntity.containsKey("key2"));
        assertEquals(2l, firstMapEntity.get("key2"));

        assertNotNull(secondMapEntity);
        assertTrue(secondMapEntity.containsKey("key3"));
        assertEquals("value3", secondMapEntity.get("key3"));
        assertTrue(secondMapEntity.containsKey("key4"));
        assertEquals(4l, secondMapEntity.get("key4"));
    }

    @Test
    public void testGetObject() throws Exception {
        String description = "Description for testGetObject";

        SimpleEntity expected = new SimpleEntity();
        expected.setLongDescription(new Text(description));

        String key = service.save(expected);
        assertNotNull(key);

        SimpleEntity actual = service.getObject(key, SimpleEntity.class);
        assertNotNull(actual);
        assertEquals(description, actual.getLongDescriptionValue());
    }

    @Test
    public void testTransaction() throws Exception {
        Transaction transaction = service.beginTransaction();

        String description = "Description for testGetObject";

        SimpleEntity expected = new SimpleEntity();
        expected.setLongDescription(new Text(description));

        String key = service.save(expected, transaction);

        assertNotNull(key);

        try {
            service.getObject(key, SimpleEntity.class);
            fail("This should throw an exception");
        } catch (CentaurException e) {
            // This is expected behaviour
            assertTrue(e.getCause() instanceof EntityNotFoundException);
        }

        service.rollback(transaction);
        transaction = service.beginTransaction();

        try {
            service.getObject(key, SimpleEntity.class);
            fail("This should throw an exception");
        } catch (CentaurException e) {
            // This is expected behaviour
            assertTrue(e.getCause() instanceof EntityNotFoundException);
        }

        service.save(expected, transaction);
        service.commit(transaction);

        assertNotNull(key);

        SimpleEntity actual = service.getObject(key, SimpleEntity.class);
        assertNotNull(actual);
        assertEquals(description, actual.getLongDescription().getValue());
    }


    @Test
    public void testUserCRUD() throws Exception {
        UserEntity userEntity = new UserEntity();

        String userKey = service.save(userEntity);

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
            assertTrue(e.getCause() instanceof EntityNotFoundException);
        }

        userEntity = new UserEntity();

        userKey = service.save(userEntity);

        assertNotNull(userKey);
        userEntity = service.getObject(userKey, UserEntity.class);
        assertNotNull(userEntity);
    }

    @Test
    public void testSaveChildrenWithNoNameWithTransaction() throws Exception {
        Transaction transaction = service.beginTransaction();
        try {
            ParentEntity parentEntity = new ParentEntity();

            String parentName = "parentName";
            String parentDescription = "parentDescription";

            String parentKey = service.save(parentEntity, transaction);
            assertNotNull(parentKey);

            parentEntity.setName(parentName);
            parentEntity.setShortDescription(parentDescription);

            String childName = "name";
            String childDescription = "description";

            for (int i = 0; i < 10; i++) {
                SimpleEntity simpleEntity = new SimpleEntity();
                simpleEntity.setShortDescription(childDescription + i);
                String key = service.saveChild(parentEntity, simpleEntity, transaction);
                assertNotNull(key);
                assertNotNull(simpleEntity.getAppEngineKey());
            }

            List<SimpleEntity> simpleEntities = service.getAllChildren(SimpleEntity.class.getSimpleName(), parentEntity, SimpleEntity.class);

            assertEquals(10, simpleEntities.size());
            for (int i = 0; i < simpleEntities.size(); i++) {
                SimpleEntity listEntity = simpleEntities.get(i);

                assertEquals(childName + i, listEntity.getName());
                assertEquals(childDescription + i, listEntity.getShortDescription());
            }
            service.commit(transaction);
        } catch (Exception e) {
            service.rollback(transaction);
        }
    }

    @Test
    public void testSimpleDeleteByString() throws Exception {
        SimpleEntity simpleEntity = new SimpleEntity();

        String name = "name";
        String description = "description";

        simpleEntity.setName(name);
        simpleEntity.setShortDescription(description);

        String keyString = service.save(simpleEntity);
        assertNotNull(keyString);
        assertNotNull(simpleEntity.getAppEngineKey());

        service.deleteObject(keyString);
        try {
            assertNull(service.getObject(keyString, SimpleEntity.class));
            fail("This method should have thrown an EntityNotFoundException");
        } catch (CentaurException e) {
            assertTrue(e.getMessage().startsWith("com.google.appengine.api.datastore.EntityNotFoundException: No entity was found matching the key: "));
        }
    }

    @Test
    public void testGetObjectsByKind() throws Exception {
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
                        SimpleEntity.class);

        assertNotNull(objects);
        assertEquals(20, objects.size());
    }

    @Test
    public void testGetAllObjects() throws Exception {
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
                service.getObjects(SimpleEntity.class);

        assertNotNull(objects);
        assertEquals(20, objects.size());
    }

    @Test
    public void testGetObjectsByFilter() throws Exception {
        String childName = "name";
        String childDescription = "description";

        for (int i = 0; i < 20; i++) {
            SimpleEntity simpleEntity = new SimpleEntity();
            simpleEntity.setShortDescription(childName + i);
            if (i <= 10) {
                childDescription += i;
            }
            simpleEntity.setLongDescription(new Text(childDescription));
            service.save(simpleEntity);
        }

        Query.Filter filter = new Query.FilterPredicate("shortDescription", Query.FilterOperator.EQUAL, "name5");

        List<SimpleEntity> objects =
                service.findObjectsByFilter(SimpleEntity.class, filter);

        assertNotNull(objects);
        assertEquals(1, objects.size());
        assertEquals("name5", objects.get(0).getShortDescription());
    }

    @Test
    public void testGetObjectsByFilterSorted() throws Exception {
        String childDescription = "description";

        for (int i = 0; i < 20; i++) {
            EntityWithStringAndIntegerField simpleEntity = new EntityWithStringAndIntegerField();
            simpleEntity.setName("" + i);
            simpleEntity.setIntValue(0);
            if (i <= 10) {
                simpleEntity.setStringValue(childDescription + i);
                simpleEntity.setIntValue(i);
            }
            service.save(simpleEntity);
        }

        Query.Filter filter = new Query.FilterPredicate("intValue", Query.FilterOperator.GREATER_THAN, 5);
        List<SortCriteria> sortCriteria = new ArrayList<>();
        sortCriteria.add(new SortCriteria("intValue", false));
        FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();

        List<EntityWithStringAndIntegerField> objects =
                service.findObjectsByFilterSorted(EntityWithStringAndIntegerField.class, filter, sortCriteria, fetchOptions);

        assertNotNull(objects);
        assertEquals(5, objects.size());
        assertEquals("description10", objects.get(0).getStringValue());
        assertEquals("description9", objects.get(1).getStringValue());
        assertEquals("description8", objects.get(2).getStringValue());
        assertEquals("description7", objects.get(3).getStringValue());
        assertEquals("description6", objects.get(4).getStringValue());
    }

    @Test
    public void testGetObjectsByFilterSortedPaged() throws Exception {
        Date date = new Date();

        int pageSize = 10;

        for (int i = 0; i < 100; i++) {
            EntityWithDateField simpleEntity = new EntityWithDateField();
            simpleEntity.setName("" + i);
            simpleEntity.setDate(DateUtils.addMinutes(date, (i + 1) * 5));
            service.save(simpleEntity);
        }

        Query.Filter filter = new Query.FilterPredicate("date", Query.FilterOperator.GREATER_THAN, date);
        List<SortCriteria> sortCriteria = new ArrayList<>();
        sortCriteria.add(new SortCriteria("date", true));
        FetchOptions fetchOptions = FetchOptions.Builder.withLimit(pageSize);

        for (int count = 0; count < 10; count++) {
            ResultList<EntityWithDateField> page =
                    service.findObjectsByFilterSorted(EntityWithDateField.class, filter, sortCriteria, fetchOptions);

            assertNotNull(page);

            if (count < 10) {
                assertEquals(10, page.size());

                for (int i = 0; i < page.size(); i++) {
                    assertEquals(DateUtils.addMinutes(date, (i + 1) * 5 + (count * 50)), page.get(i).getDate());
                }

                fetchOptions = FetchOptions.Builder.withLimit(pageSize).startCursor(page.getCursor());
            } else {
                assertEquals(0, page.size());
            }
        }
    }

    @Test
    public void testGetByKeys() throws Exception {
        List<String> keys = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            EntityWithDateField simpleEntity = new EntityWithDateField();
            simpleEntity.setDate(new Date());
            keys.add(service.save(simpleEntity));
        }

        Map<String, EntityWithDateField> values = service.getObjectByKeyStrings(keys, EntityWithDateField.class);

        assertNotNull(values);
        assertEquals(10, values.size());
        for (Map.Entry<String, EntityWithDateField> entry : values.entrySet()) {
            String keyString = entry.getKey();
            EntityWithDateField value = entry.getValue();

            assertEquals(keyString, KeyFactory.keyToString(value.getAppEngineKey()));
        }
    }

    @Test
    public void testCaching() throws Exception {
        final int intValue = 1;
        final Integer integerWrapperValue = 2;
        final float floatValue = 3f;
        final Float floatWrapperValue = 4f;
        final double doubleValue = 5d;
        final Double doubleWrapperValue = 6d;

        EntityWithAllFields entity = new EntityWithAllFields();
        entity.setIntField(intValue);
        entity.setIntegerWrapperField(integerWrapperValue);
        entity.setFloatField(floatValue);
        entity.setFloatWrapperField(floatWrapperValue);
        entity.setDoubleField(doubleValue);
        entity.setDoubleWrapperField(doubleWrapperValue);

        CentaurService service = getService();
        String key = service.save(entity);

        assertNotNull(key);
        assertNull(service.getObjectFromCacheByKey(key));
        service.cacheObjectByKey(key, entity);
        final Object objectFromCache = service.getObjectFromCacheByKey(key);
        assertNotNull(objectFromCache);

        service.cacheObjectByKey(key, null);
        assertNull(service.getObjectFromCacheByKey(key));
    }

    private CentaurService getService() {
        return CentaurServiceFactory.newInstance();
    }
}