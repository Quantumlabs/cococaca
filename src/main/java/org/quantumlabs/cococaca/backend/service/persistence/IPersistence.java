package org.quantumlabs.cococaca.backend.service.persistence;

import java.io.InputStream;

import org.quantumlabs.cococaca.backend.service.persistence.model.IPostKey;
import org.quantumlabs.cococaca.backend.service.persistence.model.ISubscriberKey;
import org.quantumlabs.cococaca.backend.service.persistence.model.Post;
import org.quantumlabs.cococaca.backend.service.persistence.model.Subscriber;

/**
 * Copy right (c) yudzhou since 2014/9/29.
 */
public interface IPersistence {
	Subscriber fetchSubscriber(ISubscriberKey subscriberKey);

	Post fetchPost(IPostKey postKey);

	void start();

	void stop();

	boolean isStarted();

	void storeSubscriber(Subscriber subscriber, String password);

	void updateSubscriber(Subscriber subscriber);

	void storePost(Post post);

	String write(InputStream inputStream);
}
