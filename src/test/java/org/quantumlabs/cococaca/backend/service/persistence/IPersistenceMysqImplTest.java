package org.quantumlabs.cococaca.backend.service.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.quantumlabs.cococaca.backend.UnitTestUtil;
import org.quantumlabs.cococaca.backend.service.persistence.mock.UTPresistenceConfig;
import org.quantumlabs.cococaca.backend.service.persistence.model.Gender;
import org.quantumlabs.cococaca.backend.service.persistence.model.IContentKey;
import org.quantumlabs.cococaca.backend.service.persistence.model.IContentKeyImpl;
import org.quantumlabs.cococaca.backend.service.persistence.model.IPostKey;
import org.quantumlabs.cococaca.backend.service.persistence.model.IPostKeyImpl;
import org.quantumlabs.cococaca.backend.service.persistence.model.ISubscriberKey;
import org.quantumlabs.cococaca.backend.service.persistence.model.Post;
import org.quantumlabs.cococaca.backend.service.persistence.model.Subscriber;
import org.quantumlabs.cococaca.backend.service.preference.Config;

public class IPersistenceMysqImplTest {
	private IPersistence persistence;

	String avatarID = "_avatarID";
	String name = "_john";
	Gender gender = Gender.MALE;
	String avatarID2 = "__newAvatar";
	Gender gender2 = Gender.FEMALE;
	String name2 = "__robert";
	Map<String, Object> attributesMap = new HashMap<>();
	ISubscriberKey subscriberKey = generateSubscriberKey("_john");
	IContentKey contentKey = generateContentKey("_contentKey");
	String description;
	IPostKey postKey = generatePostKey("_postKey");
	long postTimeStamp = 1022412312;

	// @BeforeClass
	public static void beforeClass() {
		UnitTestUtil.setupDBEnv();
		UnitTestUtil.tearDownDBEnv();
	}

	private IContentKey generateContentKey(String string) {
		return new IContentKeyImpl(string);
	}

	private IPostKey generatePostKey(String string) {
		return new IPostKeyImpl(string);
	}

	// @AfterClass
	public static void afterClass() {
		UnitTestUtil.tearDownDBEnv();
	}

	@Test
	public void testStart() {
		preparePersistence();
		assertFalse(persistence.isStarted());
		persistence.start();
		assertTrue(persistence.isStarted());
	}

	@Test
	public void testStop() {
		preparePersistence();
		persistence.start();
		assertTrue(persistence.isStarted());
		persistence.stop();
		assertFalse(persistence.isStarted());
	}

	private void preparePersistence() {
		Config mockConfig = new UTPresistenceConfig();
		persistence = new IPersistenceMysqlImpl(mockConfig);
	}

	@Test
	public void testFetchSubscriber() {
		preparePersistence();
		Subscriber subscriber = generateSubscriber();
		persistence.storeSubscriber(subscriber, "_password");
		verifyTheSameFromDB(subscriber);
	}

	private void verifyTheSameFromDB(Subscriber subscriber) {
		Subscriber fetchedSubscriber = persistence
				.fetchSubscriber(subscriberKey);
		assertEquals(subscriber, fetchedSubscriber);
		assertEquals(attributesMap.get("avatar"),
				fetchedSubscriber.getAvatarID());
		assertEquals(attributesMap.get("gender"), fetchedSubscriber.getGender());
		assertEquals(attributesMap.get("name"), fetchedSubscriber.getName());
	}

	@Test
	public void testInsertSubscriber() {
		preparePersistence();
		Subscriber subscriber = generateSubscriber();
		persistence.storeSubscriber(subscriber, "__password");
		verifyTheSameFromDB(subscriber);
	}

	@Before
	public void before() {
		UnitTestUtil.clearAllTables();
	}

	private ISubscriberKey generateSubscriberKey(String string) {
		return SubscriberKeyFactory.INSTANCE.newKey(name);
	}

	private Subscriber generateSubscriber() {
		Subscriber subscriber = new Subscriber(subscriberKey);
		subscriber.setAvatarID(avatarID);
		subscriber.setGender(gender);
		subscriber.setName(name);
		attributesMap.put("avatar", avatarID);
		attributesMap.put("gender", gender);
		attributesMap.put("name", name);
		return subscriber;
	}

	@Test
	public void testUpdateSubscriber() {
		preparePersistence();
		Subscriber subscriber = generateSubscriber();
		persistence.storeSubscriber(subscriber, "__password");
		verifyTheSameFromDB(subscriber);
		updateSubscriberInMemory(subscriber);
		persistence.updateSubscriber(subscriber);
		verifyTheSameFromDB(subscriber);
	}

	private void updateSubscriberInMemory(Subscriber subscriber) {
		subscriber.setAvatarID(avatarID2);
		subscriber.setGender(gender2);
		subscriber.setName(name2);
		attributesMap.put("avatar", avatarID2);
		attributesMap.put("gender", gender2);
		attributesMap.put("name", name2);
	}

	@Test
	public void testFetchPost() {
		preparePersistence();
		Post post = generatedPost();
		persistence.storePost(post);
		verifyTheSameFromDB(post);
	}

	private void verifyTheSameFromDB(Post post) {
		Post fetchedPost = persistence.fetchPost(postKey);
		assertEquals(fetchedPost.getKey(), post.getKey());
		assertEquals(fetchedPost.getAuthorKey(), post.getAuthorKey());
		assertEquals(fetchedPost.getContentKey(), post.getContentKey());
		assertEquals(fetchedPost.getDescription(), post.getDescription());
		assertEquals(fetchedPost.getTimeStamp(), post.getTimeStamp());
	}

	private Post generatedPost() {
		Post post = new Post(postKey);
		post.setAuthorKey(subscriberKey);
		post.setContentKey(contentKey);
		post.setDescription(description);
		post.setTimeStamp(postTimeStamp);
		return post;
	}
}
