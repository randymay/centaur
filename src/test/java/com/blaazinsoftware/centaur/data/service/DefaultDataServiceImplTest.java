package com.blaazinsoftware.centaur.data.service;

import com.blaazinsoftware.centaur.data.QueryResults;
import com.blaazinsoftware.centaur.data.QuerySearchOptions;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class DefaultDataServiceImplTest {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    private DataService service = DataServiceFactory.getInstance();

    private Closeable session;

    @Before
    public void setUp() {
        helper.setUp();

        session = ObjectifyService.begin();

        ObjectifyService.register(SimpleEntity.class);
        ObjectifyService.register(SimpleEntityWithStringId.class);
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
        ObjectifyService.register(HierarchicalEntity.class);
        ObjectifyService.register(EntityWithRef.class);
    }

    @After
    public void tearDown() {
        helper.tearDown();

        session.close();
    }

    @Test
    public void testSimpleCRUDUsingID() throws Exception {
        SimpleEntity simpleEntity = new SimpleEntity();

        String description = "description";

        simpleEntity.setShortDescription(description);

        long id = service.saveForId(simpleEntity);
        assertNotNull(id);

        simpleEntity = service.getEntity(id, SimpleEntity.class);
        assertNotNull(simpleEntity);
        assertEquals(description, simpleEntity.getShortDescription());

        description = "new description";
        simpleEntity.setShortDescription(description);
        service.saveForId(simpleEntity);
        simpleEntity = service.getEntity(id, SimpleEntity.class);
        assertNotNull(simpleEntity);
        assertEquals(description, simpleEntity.getShortDescription());

        service.deleteEntity(id, SimpleEntity.class);
        assertNull(service.getEntity(id, SimpleEntity.class));
    }

    @Test
    public void testSimpleCRUDUsingStringID() throws Exception {
        SimpleEntityWithStringId simpleEntity = new SimpleEntityWithStringId();

        String description = "description";
        simpleEntity.setId(SimpleEntityWithStringId.class.getCanonicalName());
        simpleEntity.setShortDescription(description);

        String keyString = service.saveForWebSafeKey(simpleEntity);
        assertNotNull(keyString);

        simpleEntity = service.getEntityByWebSafeKey(keyString);
        assertNotNull(simpleEntity);
        assertEquals(description, simpleEntity.getShortDescription());

        description = "new description";
        simpleEntity.setShortDescription(description);
        service.saveForId(simpleEntity);
        simpleEntity = service.getEntityByWebSafeKey(keyString);
        assertNotNull(simpleEntity);
        assertEquals(description, simpleEntity.getShortDescription());

        service.deleteEntity(keyString);
        assertNull(service.getEntity(keyString, SimpleEntityWithStringId.class));
    }

    @Test
    public void testSimpleCRUDUsingKey() throws Exception {
        SimpleEntity simpleEntity = new SimpleEntity();

        String description = "description";

        simpleEntity.setShortDescription(description);

        String key = service.saveForWebSafeKey(simpleEntity);
        assertNotNull(key);

        simpleEntity = service.getEntityByWebSafeKey(key);
        assertNotNull(simpleEntity);
        assertEquals(description, simpleEntity.getShortDescription());

        description = "new description";
        simpleEntity.setShortDescription(description);
        service.saveForId(simpleEntity);
        simpleEntity = service.getEntityByWebSafeKey(key);
        assertNotNull(simpleEntity);
        assertEquals(description, simpleEntity.getShortDescription());

        service.deleteEntity(key);
        assertNull(service.getEntity(key, SimpleEntity.class));
    }

    @Test
    public void testSimpleCRUDUsingWebSafeKey() throws Exception {
        SimpleEntity simpleEntity = new SimpleEntity();

        String description = "description";

        simpleEntity.setShortDescription(description);

        String key = service.saveForWebSafeKey(simpleEntity);
        Long id = simpleEntity.getId();
        assertNotNull(key);
        final String webSafeKey = service.getWebSafeString(simpleEntity);
        assertNotNull(webSafeKey);
        assertEquals(key, webSafeKey);

        simpleEntity = service.getEntityByWebSafeKey(key);
        assertNotNull(simpleEntity);
        assertEquals(description, simpleEntity.getShortDescription());
        assertEquals(id, simpleEntity.getId());
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

        String key = service.saveForWebSafeKey(simpleEntity);
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

        String parentDescription = "parentDescription";

        long parentKey = service.saveForId(parentEntity);
        assertNotNull(parentKey);

        parentEntity.setShortDescription(parentDescription);

        String childDescription = "description";

        final int childrenToCreate = 10;
        createChildEntities(parentEntity, childDescription, childrenToCreate);

        List<ChildEntity> childEntities = service.getAllChildren(parentEntity, ChildEntity.class);

        assertEquals(10, childEntities.size());
        for (int i = 0; i < childEntities.size(); i++) {
            ChildEntity listEntity = childEntities.get(i);

            assertEquals(childDescription + i, listEntity.getShortDescription());
        }
    }

    private void createChildEntities(ParentEntity parentEntity, String childDescription, int countOfChildren) {
        List<ChildEntity> children = new ArrayList<>();
        for (int i = 0; i < countOfChildren; i++) {
            ChildEntity childEntity = new ChildEntity();
            childEntity.setParentEntity(parentEntity);
            childEntity.setShortDescription(childDescription + i);
            children.add(childEntity);
        }

        Map<Key<ChildEntity>, ChildEntity> savedChildren = service.saveAll(children);
        for (Map.Entry<Key<ChildEntity>, ChildEntity> entry : savedChildren.entrySet()) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
        }
    }

    private void createSimpleEntities(String description, int countOfChildren) {

        List<SimpleEntity> children = new ArrayList<>();

        for (int i = 0; i < countOfChildren; i++) {
            String entityDescription = description;
            SimpleEntity simpleEntity = new SimpleEntity();
            if (i < 10) {
                entityDescription += i;
            }
            simpleEntity.setShortDescription(entityDescription);
            children.add(simpleEntity);
        }

        Map<Key<SimpleEntity>, SimpleEntity> savedChildren = service.saveAll(children);
        for (Map.Entry<Key<SimpleEntity>, SimpleEntity> entry : savedChildren.entrySet()) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
        }
    }

    @Test
    public void testHeirarchicalCRUDNoNames() throws Exception {
        ParentEntity parentEntity = new ParentEntity();

        String parentDescription = "parentDescription";

        String parentKey = service.saveForWebSafeKey(parentEntity);
        assertNotNull(parentKey);

        parentEntity.setShortDescription(parentDescription);

        String childDescription = "description";

        for (int i = 0; i < 10; i++) {
            ChildEntity childEntity = new ChildEntity();
            childEntity.setParentEntity(parentEntity);
            childEntity.setShortDescription(childDescription + i);
            String key = service.saveForWebSafeKey(childEntity);
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

        String key = service.saveForWebSafeKey(entity);

        entity = service.getEntityByWebSafeKey(key);
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
        userIds.add(7L);
        userIds.add(6L);
        userIds.add(5L);
        userIds.add(4L);
        userIds.add(3L);
        userIds.add(2L);
        userIds.add(1L);
        userIds.add(0L);
        object.setUserIds(userIds);

        String key = service.saveForWebSafeKey(object);

        object = service.getEntityByWebSafeKey(key);
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

        String key = service.saveForWebSafeKey(object);

        object = service.getEntityByWebSafeKey(key);
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

        String key = service.saveForWebSafeKey(object);
        assertNotNull(key);

        object = service.getEntityByWebSafeKey(key);
        assertNotNull(object);
        assertTrue(object.isBooleanValue1());
        assertFalse(object.getBooleanValue2());

        object.setBooleanValue1(false);
        object.setBooleanValue2(true);

        key = service.saveForWebSafeKey(object);

        object = service.getEntityByWebSafeKey(key);
        assertNotNull(object);
        assertFalse(object.isBooleanValue1());
        assertTrue(object.getBooleanValue2());
    }

    @Test
    public void testGetEntitiesByPropertyValue() throws Exception {
        final String childDescription = "description";

        final int childrenToCreate = 20;
        createSimpleEntities(childDescription, childrenToCreate);

        QueryResults<SimpleEntity> results =
                service.findEntities(
                        "shortDescription",
                        childDescription,
                        SimpleEntity.class);

        assertNotNull(results);
        assertEquals(10, results.getCountFound());
        assertEquals(10, results.getCountReturned());
    }

    @Test
    public void testGetEntitiesByPropertyValues() throws Exception {
        String childDescription = "description";

        for (int i = 0; i < 20; i++) {
            EntityWithStringAndIntegerField simpleEntity = new EntityWithStringAndIntegerField();
            simpleEntity.setIntValue(0);
            if (i <= 10) {
                simpleEntity.setStringValue(childDescription + i);
                simpleEntity.setIntValue(i);
            }
            simpleEntity.setStringValue(childDescription);
            service.saveForWebSafeKey(simpleEntity);
        }

        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put("intValue", 0);
        filter.put("stringValue", childDescription);

        QueryResults<EntityWithStringAndIntegerField> results =
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

        String key = service.saveForWebSafeKey(expected);
        assertNotNull(key);

        SimpleEntity actual = service.getEntityByWebSafeKey(key);
        assertNotNull(actual);
        assertEquals(description, actual.getLongDescriptionValue());
    }

    /*@Test
    public void testTransaction() throws Exception {
        Transaction transaction = service.beginTransaction();

        String description = "Description for testGetObject";

        SimpleEntity expected = new SimpleEntity();
        expected.setLongDescription(new Text(description));

        String key = service.saveForWebSafeKey(expected, transaction);

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

        service.saveForWebSafeKey(expected, transaction);
        service.commit(transaction);

        assertNotNull(key);

        SimpleEntity actual = service.getEntity(key, SimpleEntity.class);
        assertNotNull(actual);
        assertEquals(description, actual.getLongDescription().getValue());
    }*/

    @Test
    public void testUserCRUD() throws Exception {
        UserEntity userEntity = new UserEntity();

        String userKey = service.saveForWebSafeKey(userEntity);

        assertNotNull(userKey);
        assertNotNull(service.getEntityByWebSafeKey(userKey));
        assertNull(userEntity.getFirstName());

        userEntity.setFirstName("name");
        service.saveForWebSafeKey(userEntity);
        userEntity = service.getEntityByWebSafeKey(userKey);
        assertEquals("name", userEntity.getFirstName());

        service.deleteEntity(userEntity);

        assertNull(service.getEntity(userKey, UserEntity.class));

        userEntity = new UserEntity();

        userKey = service.saveForWebSafeKey(userEntity);

        assertNotNull(userKey);
        userEntity = service.getEntityByWebSafeKey(userKey);
        assertNotNull(userEntity);
    }

    @Test
    public void testGetWebSafeKey() throws Exception {
        String parentName = "parentName";
        String parentDescription = "parentDescription";

        ParentEntity parentEntity = new ParentEntity();
        parentEntity.setLongDescription(parentName);
        parentEntity.setShortDescription(parentDescription);

        String parentKey = service.saveForWebSafeKey(parentEntity);
        assertNotNull(parentKey);
        final ParentEntity loadedEntityByKey = service.getEntity(parentEntity.getId(), ParentEntity.class);
        assertNotNull(loadedEntityByKey);
        assertEquals(parentEntity, loadedEntityByKey);
        final ParentEntity loadedEntityByWebSafeKey = service.getEntityByWebSafeKey(parentEntity.getWebSafeKey());
        assertNotNull(loadedEntityByWebSafeKey);
        assertEquals(parentEntity, loadedEntityByWebSafeKey);
        assertEquals(parentEntity.getWebSafeKey(), loadedEntityByWebSafeKey.getWebSafeKey());

        String childDescription = "description";

        for (int i = 0; i < 10; i++) {
            ChildEntity childEntity = new ChildEntity();
            childEntity.setShortDescription(childDescription + i);
            childEntity.setParentEntity(parentEntity);
            String key = service.saveForWebSafeKey(childEntity);
            assertNotNull(key);
            assertNotNull(childEntity.getWebSafeKey());
            assertEquals(childEntity, service.getEntityByWebSafeKey(childEntity.getWebSafeKey()));
            assertEquals(childEntity.getWebSafeKey(), (service.getChild(childEntity.getId(), parentEntity, ChildEntity.class)).getWebSafeKey());
        }

        List<ChildEntity> childEntities = service.getAllChildren(parentEntity, ChildEntity.class);

        assertEquals(10, childEntities.size());
        for (int i = 0; i < childEntities.size(); i++) {
            ChildEntity listEntity = childEntities.get(i);

            assertEquals(childDescription + i, listEntity.getShortDescription());
        }
    }

    @Test
    public void testSimpleDeleteByString() throws Exception {
        SimpleEntity simpleEntity = new SimpleEntity();

        String description = "description";

        simpleEntity.setShortDescription(description);

        String keyString = service.saveForWebSafeKey(simpleEntity);
        assertNotNull(keyString);
        assertNotNull(simpleEntity.getId());

        service.deleteEntity(keyString);
        assertNull(service.getEntity(keyString, SimpleEntity.class));
    }

    @Test
    public void testGetAllEntities() throws Exception {
        String childDescription = "description";

        final int childrenToCreate = 20;
        createSimpleEntities(childDescription, childrenToCreate);

        QueryResults<SimpleEntity> results =
                service.findEntities(SimpleEntity.class);

        assertNotNull(results);
        assertEquals(20, results.getCountFound());
        assertEquals(20, results.getCountReturned());
    }

    @Test
    public void testGetEntitiesByFilter() throws Exception {
        String childName = "name";
        String childDescription = "description";

        for (int i = 0; i < 20; i++) {
            SimpleEntity simpleEntity = new SimpleEntity();
            simpleEntity.setShortDescription(childName + i);
            if (i <= 10) {
                childDescription += i;
            }
            simpleEntity.setLongDescription(childDescription);
            service.saveForWebSafeKey(simpleEntity);
        }

        QuerySearchOptions<SimpleEntity> searchOptions = new QuerySearchOptions<>(SimpleEntity.class);
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put("shortDescription", "name5");
        searchOptions.filter(filter);

        QueryResults<SimpleEntity> results = service.findEntities(searchOptions);

        assertNotNull(results);
        assertEquals(1, results.getCountFound());
        assertEquals(1, results.getCountReturned());
        assertEquals("name5", results.getResults().get(0).getShortDescription());
    }

    @Test
    public void testGetEntitiesByFilterSorted() throws Exception {
        String childDescription = "description";

        for (int i = 0; i < 20; i++) {
            EntityWithStringAndIntegerField simpleEntity = new EntityWithStringAndIntegerField();
            simpleEntity.setIntValue(0);
            if (i <= 10) {
                simpleEntity.setStringValue(childDescription + i);
                simpleEntity.setIntValue(i);
            }
            service.saveForWebSafeKey(simpleEntity);
        }

        QuerySearchOptions<EntityWithStringAndIntegerField> searchOptions
                = new QuerySearchOptions<>(EntityWithStringAndIntegerField.class);
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put("intValue >", 5);
        searchOptions.filter(filter);
        searchOptions.orderByField("intValue");
        searchOptions.descending(false);

        QueryResults<EntityWithStringAndIntegerField> results = service.findEntities(searchOptions);

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
    public void testGetEntitiesByFilterSortedPaged() throws Exception {
        Date date = new Date();

        int pageSize = 10;

        for (int i = 0; i < 100; i++) {
            EntityWithDateField simpleEntity = new EntityWithDateField();
            simpleEntity.setDate(DateUtils.addMinutes(date, (i + 1) * 5));
            service.saveForId(simpleEntity);
        }

        QuerySearchOptions<EntityWithDateField> searchOptions
                = new QuerySearchOptions<>(EntityWithDateField.class);
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put("date >", date);
        searchOptions.filter(filter);
        searchOptions.orderByField("date");
        searchOptions.limit(pageSize);

        Cursor cursor = null;

        for (int count = 0; count < 10; count++) {
            if (cursor != null) {
                searchOptions.setCursor(cursor);
            }

            QueryResults<EntityWithDateField> page = service.findEntities(searchOptions);

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
    public void testInheritedCRUDWithRefList() throws Exception {
        HierarchicalEntity parentEntity = new HierarchicalEntity();

        String parentDescription = "parentDescription";

        String parentKey = service.saveForWebSafeKey(parentEntity);
        assertNotNull(parentKey);

        parentEntity.setShortDescription(parentDescription);

        String childDescription = "description";

        for (int i = 0; i < 10; i++) {
            SimpleEntity childEntity = new SimpleEntity();
            childEntity.setShortDescription(childDescription + i);
            String key = service.saveForWebSafeKey(childEntity);
            assertNotNull(key);
            assertNotNull(childEntity.getId());
            parentEntity.getChildEntities().add(childEntity);
        }

        HierarchicalEntity loadedEntity = service.getEntityByWebSafeKey(parentKey);

        List<SimpleEntity> childEntities = loadedEntity.getChildEntities().deRef();
        assertEquals(10, childEntities.size());
        for (int i = 0; i < childEntities.size(); i++) {
            SimpleEntity listEntity = childEntities.get(i);

            assertEquals(childDescription + i, listEntity.getShortDescription());
        }
    }

    @Test
    public void testFindEntityByRef() throws Exception {
        UserEntity userEntity = new UserEntity();
        service.saveForId(userEntity);

        EntityWithRef withRef = new EntityWithRef();
        withRef.setUserEntity(userEntity);
        service.saveForId(withRef);

        EntityWithRef loadedEntity = service.findEntity("userEntity", userEntity, EntityWithRef.class);
        assertNotNull(loadedEntity);
    }
}