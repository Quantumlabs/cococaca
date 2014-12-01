package org.quantumlabs.cococaca.backend.service.persistence;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.function.Function;

import org.quantumlabs.cococaca.backend.service.persistence.model.Danmuku;
import org.quantumlabs.cococaca.backend.service.persistence.model.IPostKey;
import org.quantumlabs.cococaca.backend.service.persistence.model.ISubscriberKey;
import org.quantumlabs.cococaca.backend.service.persistence.model.Post;
import org.quantumlabs.cococaca.backend.service.persistence.model.Subscriber;
import org.quantumlabs.cococaca.backend.transaction.authorization.Credential;

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

	Post[] fetchPostsForSubscriber(ISubscriberKey subscriberKey);

	// Authorize user
	ISubscriberKey authorize(Credential credential);

	boolean isSubscriberExisting(String userName);

	Void follow(ISubscriberKey followerKey, ISubscriberKey followeeKey);

	Void unfollow(ISubscriberKey followerKey, ISubscriberKey followeeKey);

	String write(InputStream inputStream);

	void read(String resourceID, Function<InputStream, Void> callBack);

	Danmuku[] fetchDanmukuByPostID(IPostKey postKey);

	Void insertDanmukuForPost(IPostKey postKey, Danmuku... danmukus);
}
