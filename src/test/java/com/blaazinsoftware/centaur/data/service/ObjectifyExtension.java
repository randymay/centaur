package com.blaazinsoftware.centaur.data.service;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.testing.LocalDatastoreHelper;
import com.google.common.base.Preconditions;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import net.spy.memcached.MemcachedClient;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;

/**
 * Sets up and tears down the GAE local unit test harness environment
 */
public class ObjectifyExtension implements BeforeEachCallback, AfterEachCallback {

    private static final Namespace NAMESPACE = Namespace.create(ObjectifyExtension.class);

    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        final LocalDatastoreHelper helper = LocalDatastoreExtension.getHelper(context);
        Preconditions.checkNotNull(helper, "This extension depends on " + LocalDatastoreExtension.class.getSimpleName());

        final Datastore datastore = helper.getOptions().getService();

        final MemcachedClient memcachedClient = LocalMemcacheExtension.getClient(context);
        Preconditions.checkNotNull(memcachedClient, "This extension depends on " + LocalMemcacheExtension.class.getSimpleName());

        ObjectifyService.init(new ObjectifyFactory(datastore, memcachedClient));

        final Closeable rootService = ObjectifyService.begin();

        context.getStore(NAMESPACE).put(Closeable.class, rootService);
    }

    @Override
    public void afterEach(final ExtensionContext context) throws Exception {
        final Closeable rootService = context.getStore(NAMESPACE).get(Closeable.class, Closeable.class);

        rootService.close();
    }
}
