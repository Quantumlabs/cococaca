package org.quantumlabs.cococaca.backend.service.persistence;

import org.quantumlabs.cococaca.backend.service.persistence.model.ISubscriberKey;
import org.quantumlabs.cococaca.backend.service.persistence.model.Subscriber;


/**
 * Copy right (c) yudzhou since 2014/9/29.
 */
public interface IPersistence {
    Subscriber fetchSubscriber(ISubscriberKey subscriberKey);
}
