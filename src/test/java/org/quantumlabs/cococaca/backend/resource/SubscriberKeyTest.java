package org.quantumlabs.cococaca.backend.resource;

import org.junit.Test;
import org.quantumlabs.cococaca.backend.service.persistence.model.ISubscriberKey;
import org.quantumlabs.cococaca.backend.service.persistence.model.ISubscriberKeyImpl;

import static org.junit.Assert.assertEquals;

/**
 * Copy right (c) yudzhou since 2014/9/29.
 */
public class SubscriberKeyTest {

    private final String VALUE = "__key_value";

    @Test
    public void testEquals() {
        ISubscriberKey keyMock = new ISubscriberKeyImpl(VALUE);
        ISubscriberKey keyMock2 = new ISubscriberKeyImpl(VALUE);
        assertEquals(keyMock, keyMock2);
    }

    @Test
    public void testGet() {
        ISubscriberKey key = new ISubscriberKeyImpl(VALUE);
        assertEquals(VALUE, key.get());
    }
}
