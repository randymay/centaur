package com.blaazinsoftware.centaur.service;

import com.blaazinsoftware.centaur.search.ListResults;
import com.blaazinsoftware.centaur.search.QuerySearchOptions;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class DefaultCentaurServiceImplTest {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    private DefaultCentaurServiceImpl service = new DefaultCentaurServiceImpl();

    private Closeable session;

    @Before
    public void setUp() {
        helper.setUp();

        session = ObjectifyService.begin();

        ObjectifyService.register(SimpleEntity.class);
        ObjectifyService.register(ParentEntity.class);
        ObjectifyService.register(ChildEntity.class);
        ObjectifyService.register(EntityWithDateField.class);
        ObjectifyService.register(EntityWithIntegerField.class);
        ObjectifyService.register(EntityWithStringAndIntegerField.class);
        ObjectifyService.register(EntityWithAllFields.class);
        ObjectifyService.register(EntityWithBooleanFields.class);
        ObjectifyService.register(EntityWithListOfIntegerField.class);
        ObjectifyService.register(EntityWithListOfLongField.class);
        ObjectifyService.register(UserEntity.class);
    }

    @After
    public void tearDown() {
        helper.tearDown();

        session.close();
    }

    @Test
    public void testSimpleCRUD() throws Exception {
        SimpleEntity simpleEntity = new SimpleEntity();

        String name = "name";
        String description = "description";

        simpleEntity.setName(name);
        simpleEntity.setShortDescription(description);

        long key = service.saveForId(simpleEntity);
        assertNotNull(key);

        simpleEntity = service.getEntity(key, SimpleEntity.class);
        assertNotNull(simpleEntity);
        assertEquals(name, simpleEntity.getName());
        assertEquals(description, simpleEntity.getShortDescription());

        name = "new name";
        simpleEntity.setName(name);
        service.saveForId(simpleEntity);
        simpleEntity = service.getEntity(key, SimpleEntity.class);
        assertNotNull(simpleEntity);
        assertEquals(name, simpleEntity.getName());
        assertEquals(description, simpleEntity.getShortDescription());

        service.deleteEntity(simpleEntity);
        assertNull(service.getEntity(key, SimpleEntity.class));
    }

    /*@Test
    public void testSimpleCRUDWithSearch() throws Exception {
        SimpleEntity simpleEntity = new SimpleEntity();

        String name = "name";
        Text description = new Text("description");
        String longDescription = "long description";

        simpleEntity.setName(name);
        simpleEntity.setShortDescription(description);
        simpleEntity.setLongDescription(new Text(longDescription));

        String key = service.saveForKey(simpleEntity);
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

        service.deleteEntity(simpleEntity);
        searchResults = service.search(SimpleEntity.class, query);
        assertFalse(searchResults.getResults().iterator().hasNext());
    }*/

    @Test
    public void testHierarchicalCRUD() throws Exception {
        ParentEntity parentEntity = new ParentEntity();

        String parentName = "parentName";
        String parentDescription = "parentDescription";

        long parentKey = service.saveForId(parentEntity);
        assertNotNull(parentKey);

        parentEntity.setName(parentName);
        parentEntity.setShortDescription(parentDescription);

        String childName = "name";
        String childDescription = "description";

        for (int i = 0; i < 10; i++) {
            ChildEntity childEntity = new ChildEntity();
            childEntity.setParentEntity(parentEntity);
            childEntity.setName(childName + i);
            childEntity.setShortDescription(childDescription + i);
            String key = service.saveForKey(childEntity);
            assertNotNull(key);
            assertNotNull(childEntity.getId());
        }

        List<ChildEntity> childEntities = service.getAllChildren(parentEntity, ChildEntity.class);

        assertEquals(10, childEntities.size());
        for (int i = 0; i < childEntities.size(); i++) {
            ChildEntity listEntity = childEntities.get(i);

            assertEquals(childName + i, listEntity.getName());
            assertEquals(childDescription + i, listEntity.getShortDescription());
        }
    }

    @Test
    public void testHeirarchicalCRUDNoNames() throws Exception {
        ParentEntity parentEntity = new ParentEntity();

        String parentDescription = "parentDescription";

        String parentKey = service.saveForKey(parentEntity);
        assertNotNull(parentKey);

        parentEntity.setShortDescription(parentDescription);

        String childDescription = "description";

        for (int i = 0; i < 10; i++) {
            ChildEntity childEntity = new ChildEntity();
            childEntity.setParentEntity(parentEntity);
            childEntity.setShortDescription(childDescription + i);
            String key = service.saveForKey(childEntity);
            assertNotNull(key);
            assertNotNull(childEntity.getId());
        }

        List<ChildEntity> childEntities = service.getAllChildren(parentEntity, ChildEntity.class);

        assertEquals(10, childEntities.size());
        for (int i = 0; i < childEntities.size(); i++) {
            ChildEntity listEntity = childEntities.get(i);

            assertEquals(childDescription + i, listEntity.getShortDescription());
        }
    }

    @Test
    public void testToAndFromEntityWithIntegerProperty() throws Exception {
        final int userId = 1;

        EntityWithIntegerField entityWithIntegerField = new EntityWithIntegerField();
        entityWithIntegerField.setUserId(userId);

        service.saveForId(entityWithIntegerField);

        entityWithIntegerField = service.findEntity("userId", userId, EntityWithIntegerField.class);
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

        String key = service.saveForKey(entity);

        entity = service.getEntity(key);
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

        String key = service.saveForKey(object);

        object = service.getEntity(key);
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

        String key = service.saveForKey(object);

        object = service.getEntity(key);
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

        String key = service.saveForKey(object);
        assertNotNull(key);

        object = service.getEntity(key);
        assertNotNull(object);
        assertTrue(object.isBooleanValue1());
        assertFalse(object.getBooleanValue2());

        object.setBooleanValue1(false);
        object.setBooleanValue2(true);

        key = service.saveForKey(object);

        object = service.getEntity(key);
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
            service.saveForKey(simpleEntity);
        }

        ListResults<SimpleEntity> results =
                service.findEntities(
                        "shortDescription",
                        childDescription,
                        SimpleEntity.class);

        assertNotNull(results);
        assertEquals(10, results.getCountFound());
        assertEquals(10, results.getCountReturned());
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
            service.saveForKey(simpleEntity);
        }

        Query.Filter intFilter = new Query.FilterPredicate("intValue", Query.FilterOperator.EQUAL, 0);
        Query.Filter stringFilter = new Query.FilterPredicate("stringValue", Query.FilterOperator.EQUAL, childDescription);
        Query.Filter filter = new Query.CompositeFilter(Query.CompositeFilterOperator.AND, Arrays.asList(intFilter, stringFilter));

        ListResults<EntityWithStringAndIntegerField> results =
                service.findEntities(
                        filter,
                        EntityWithStringAndIntegerField.class);

        assertNotNull(results);
        assertEquals(10, results.getCountFound());
        assertEquals(10, results.getCountReturned());
    }

    @Test
    public void testGetObject() throws Exception {
        String description = "Description for testGetObject";

        SimpleEntity expected = new SimpleEntity();
        expected.setLongDescription(description);

        String key = service.saveForKey(expected);
        assertNotNull(key);

        SimpleEntity actual = service.getEntity(key);
        assertNotNull(actual);
        assertEquals(description, actual.getLongDescriptionValue());
    }

    /*@Test
    public void testTransaction() throws Exception {
        Transaction transaction = service.beginTransaction();

        String description = "Description for testGetObject";

        SimpleEntity expected = new SimpleEntity();
        expected.setLongDescription(new Text(description));

        String key = service.saveForKey(expected, transaction);

        assertNotNull(key);

        try {
            service.getEntity(key, SimpleEntity.class);
            fail("This should throw an exception");
        } catch (CentaurException e) {
            // This is expected behaviour
            assertTrue(e.getCause() instanceof EntityNotFoundException);
        }

        service.rollback(transaction);
        transaction = service.beginTransaction();

        try {
            service.getEntity(key, SimpleEntity.class);
            fail("This should throw an exception");
        } catch (CentaurException e) {
            // This is expected behaviour
            assertTrue(e.getCause() instanceof EntityNotFoundException);
        }

        service.saveForKey(expected, transaction);
        service.commit(transaction);

        assertNotNull(key);

        SimpleEntity actual = service.getEntity(key, SimpleEntity.class);
        assertNotNull(actual);
        assertEquals(description, actual.getLongDescription().getValue());
    }*/

    @Test
    public void testUserCRUD() throws Exception {
        UserEntity userEntity = new UserEntity();

        String userKey = service.saveForKey(userEntity);

        assertNotNull(userKey);
        assertNotNull(service.getEntity(userKey));
        assertNull(userEntity.getFirstName());

        userEntity.setFirstName("name");
        service.saveForKey(userEntity);
        userEntity = service.getEntity(userKey);
        assertEquals("name", userEntity.getFirstName());

        service.deleteEntity(userEntity);

        assertNull(service.getEntity(userKey));

        userEntity = new UserEntity();

        userKey = service.saveForKey(userEntity);

        assertNotNull(userKey);
        userEntity = service.getEntity(userKey);
        assertNotNull(userEntity);
    }

    /*@Test
    public void testSaveChildrenWithNoNameWithTransaction() throws Exception {
        Transaction transaction = service.beginTransaction();
        try {
            String parentName = "parentName";
            Text parentDescription = new Text("parentDescription");

            ParentEntity parentEntity = new ParentEntity();
            parentEntity.setName(parentName);
            parentEntity.setShortDescription(parentDescription);

            String parentKey = service.saveForKey(parentEntity, transaction);
            service.commit(transaction);
            assertNotNull(parentKey);

            transaction = service.beginTransaction();

            String childDescription = "description";

            for (int i = 0; i < 10; i++) {
                SimpleEntity simpleEntity = new SimpleEntity();
                simpleEntity.setShortDescription(new Text(childDescription + i));
                String key = service.saveChild(parentEntity, simpleEntity, transaction);
                assertNotNull(key);
                assertNotNull(simpleEntity.getAppEngineKey());
            }
            service.commit(transaction);

            List<SimpleEntity> simpleEntities = service.getAllChildren(SimpleEntity.class.getSimpleName(), parentEntity, SimpleEntity.class);

            assertEquals(10, simpleEntities.size());
            for (int i = 0; i < simpleEntities.size(); i++) {
                SimpleEntity listEntity = simpleEntities.get(i);

                assertEquals(childDescription + i, listEntity.getShortDescription().getValue());
            }
        } catch (Exception e) {
            service.rollback(transaction);
            fail(e.getMessage());
        }
    }*/

    @Test
    public void testSimpleDeleteByString() throws Exception {
        SimpleEntity simpleEntity = new SimpleEntity();

        String name = "name";
        String description = "description";

        simpleEntity.setName(name);
        simpleEntity.setShortDescription(description);

        String keyString = service.saveForKey(simpleEntity);
        assertNotNull(keyString);
        assertNotNull(simpleEntity.getId());

        service.deleteEntity(keyString);
        assertNull(service.getEntity(keyString));
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
            service.saveForKey(simpleEntity);
        }

        ListResults<SimpleEntity> results =
                service.findEntities(SimpleEntity.class);

        assertNotNull(results);
        assertEquals(20, results.getCountFound());
        assertEquals(20, results.getCountReturned());
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
            service.saveForKey(simpleEntity);
        }

        ListResults<SimpleEntity> results =
                service.findEntities(SimpleEntity.class);

        assertNotNull(results);
        assertEquals(20, results.getCountFound());
        assertEquals(20, results.getCountReturned());
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
            simpleEntity.setLongDescription(childDescription);
            service.saveForKey(simpleEntity);
        }

        QuerySearchOptions<SimpleEntity> searchOptions = new QuerySearchOptions<>(SimpleEntity.class);
        Query.Filter filter = new Query.FilterPredicate("shortDescription", Query.FilterOperator.EQUAL, "name5");
        searchOptions.filter(filter);

        ListResults<SimpleEntity> results = service.findEntities(searchOptions);

        assertNotNull(results);
        assertEquals(1, results.getCountFound());
        assertEquals(1, results.getCountReturned());
        assertEquals("name5", results.getResults().get(0).getShortDescription());
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
            service.saveForKey(simpleEntity);
        }

        QuerySearchOptions<EntityWithStringAndIntegerField> searchOptions
                = new QuerySearchOptions<>(EntityWithStringAndIntegerField.class);
        Query.Filter filter = new Query.FilterPredicate("intValue", Query.FilterOperator.GREATER_THAN, 5);
        searchOptions.filter(filter);
        searchOptions.orderByField("intValue");
        searchOptions.descending(false);

        ListResults<EntityWithStringAndIntegerField> results = service.findEntities(searchOptions);

        assertNotNull(results);
        assertEquals(5, results.getCountFound());
        assertEquals(5, results.getCountReturned());
        List<EntityWithStringAndIntegerField> entities = results.getResults();
        assertEquals(5, entities.size());
        assertEquals("description10", entities.get(0).getStringValue());
        assertEquals("description9", entities.get(1).getStringValue());
        assertEquals("description8", entities.get(2).getStringValue());
        assertEquals("description7", entities.get(3).getStringValue());
        assertEquals("description6", entities.get(4).getStringValue());
    }

    @Test
    public void testGetObjectsByFilterSortedPaged() throws Exception {
        Date date = new Date();

        int pageSize = 10;

        for (int i = 0; i < 100; i++) {
            EntityWithDateField simpleEntity = new EntityWithDateField();
            simpleEntity.setName("" + i);
            simpleEntity.setDate(DateUtils.addMinutes(date, (i + 1) * 5));
            service.saveForId(simpleEntity);
        }

        QuerySearchOptions<EntityWithDateField> searchOptions
                = new QuerySearchOptions<>(EntityWithDateField.class);
        Query.Filter filter = new Query.FilterPredicate("date", Query.FilterOperator.GREATER_THAN, date);
        searchOptions.filter(filter);
        searchOptions.orderByField("date");
        searchOptions.limit(pageSize);

        Cursor cursor = null;

        for (int count = 0; count < 10; count++) {
            if (cursor != null) {
                searchOptions.setCursor(cursor);
            }

            ListResults<EntityWithDateField> page = service.findEntities(searchOptions);

            assertNotNull(page);

            if (count < 10) {
                assertEquals(100, page.getCountFound());
                assertEquals(10, page.getCountReturned());

                for (int i = 0; i < page.getCountReturned(); i++) {
                    assertEquals(DateUtils.addMinutes(date, (i + 1) * 5 + (count * 50)), page.getResults().get(i).getDate());
                }

                cursor = page.getCursor();
            } else {
                assertEquals(0, page.getCountReturned());
            }
        }
    }

    @Test
    public void testGetByKeys() throws Exception {
        List<Long> ids = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            EntityWithDateField simpleEntity = new EntityWithDateField();
            simpleEntity.setDate(new Date());
            ids.add(service.saveForId(simpleEntity));
        }

        Map<Long, EntityWithDateField> values = service.getEntitiesByIds(ids, EntityWithDateField.class);

        assertNotNull(values);
        assertEquals(10, values.size());
        for (Map.Entry<Long, EntityWithDateField> entry : values.entrySet()) {
            Long id = entry.getKey();
            EntityWithDateField value = entry.getValue();

            assertEquals(id, value.getId());
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

        CentaurService service = new DefaultCentaurServiceImpl();
        String key = service.saveForKey(entity);
        service.cacheEntity(entity);

        assertNotNull(key);
        final Object objectFromCache = service.getEntityFromCacheByKey(key);
        assertNotNull(objectFromCache);

        service.deleteEntity(key);
        assertNull(service.getEntityFromCacheByKey(key));
    }
}