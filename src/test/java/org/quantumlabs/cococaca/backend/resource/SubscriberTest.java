/**
 * Copy right (c) yudzhou since 2014/9/29.
 */
package org.quantumlabs.cococaca.backend.resource;

import org.junit.Test;
import org.quantumlabs.cococaca.backend.service.persistence.model.ISubscriberKey;
import org.quantumlabs.cococaca.backend.service.persistence.model.ISubscriberKeyImpl;
import org.quantumlabs.cococaca.backend.service.persistence.model.Subscriber;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SubscriberTest {

    private final static String KEY_VALUE = "__key_value";

    @Test
    public void testEquals() {
        ISubscriberKey key = new ISubscriberKeyImpl(KEY_VALUE);
        ISubscriberKey key2 = new ISubscriberKeyImpl(KEY_VALUE);

        assertEquals(prepareSubscriber(key), prepareSubscriber(key2));
    }

    private Subscriber prepareSubscriber(ISubscriberKey key) {
        return new Subscriber(key);
    }


    @Test
    public void getSubscriberKey() {
    }
}
