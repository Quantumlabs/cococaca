package org.quantumlabs.cococaca.backend.service;

import static org.junit.Assert.*;

import org.junit.Test;

import static org.mockito.Mockito.*;

import org.quantumlabs.cococaca.backend.service.persistence.IPersistence;
import org.quantumlabs.cococaca.backend.service.persistence.IPersistenceImpl;
import org.quantumlabs.cococaca.backend.service.persistence.model.ISubscriberKey;
import org.quantumlabs.cococaca.backend.service.persistence.model.Subscriber;

/**
 * Copy right (c) yudzhou since 2014/9/29.
 */
public class IPersistenceTest {
    public static final String KEY = "__key";

    @Test
    public void testFetchSubscriber() {
        ISubscriberKey key = prepareKey(KEY);
        Subscriber subscriber = prepareSubscriber(key);
        prepareSubscriberInPersistence(key, subscriber);

        IPersistence persistence = prepareTarget();
        Subscriber fetchedSubscriber = persistence.fetchSubscriber(key);

        assertEquals(subscriber, fetchedSubscriber);
    }

    private void prepareSubscriberInPersistence(ISubscriberKey key, Subscriber subscriber) {

    }

    private Subscriber prepareSubscriber(ISubscriberKey key) {
        return null;
    }

    private ISubscriberKey prepareKey(String keyValue) {
        ISubscriberKey key = mock(ISubscriberKey.class);
        when(key.get()).thenReturn(keyValue);
        return key;
    }

    private IPersistenceImpl prepareTarget() {
        return new IPersistenceImpl();
    }

    public void testDeleteSubscriber() {
    }

    public void testFinalizeSubscriber() {
    }
}
