package com.blaazinsoftware.centaur.data.service;

import com.google.cloud.datastore.testing.LocalDatastoreHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;

/**
 * Sets up and tears down the Local Datastore emulator
 */
@RequiredArgsConstructor
@Slf4j
public class LocalDatastoreExtension implements BeforeAllCallback, BeforeEachCallback {

    @Override
    public void beforeAll(final ExtensionContext context) throws Exception {
        if (getHelper(context) == null) {
            log.info("Creating new LocalDatastoreHelper");

            final LocalDatastoreHelper helper = LocalDatastoreHelper.newBuilder()
                    .setConsistency(1.0)	// we always have strong consistency now
                    .setStoreOnDisk(false)
                    .build();

            context.getRoot().getStore(Namespace.GLOBAL).put(LocalDatastoreHelper.class, helper);
            helper.start();
        }

    }

    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        final LocalDatastoreHelper helper = getHelper(context);
        helper.reset();
    }

    /** Get the helper created in beforeAll; it should be global so there will one per test run */
    public static LocalDatastoreHelper getHelper(final ExtensionContext context) {
        return context.getRoot().getStore(Namespace.GLOBAL).get(LocalDatastoreHelper.class, LocalDatastoreHelper.class);
    }
}
