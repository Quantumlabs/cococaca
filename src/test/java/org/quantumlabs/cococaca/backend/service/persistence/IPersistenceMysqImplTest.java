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
import org.quantumlabs.cococaca.backend.service.persistence.model.ISubscriberKey;
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
	ISubscriberKey key = generateSubscriberKey("_john");

	// @BeforeClass
	public static void beforeClass() {
		UnitTestUtil.setupDBEnv();
		UnitTestUtil.tearDownDBEnv();
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
		Subscriber fetchedSubscriber = persistence.fetchSubscriber(key);
		assertEquals(subscriber, fetchedSubscriber);
		assertEquals(attributesMap.get("avatar"), fetchedSubscriber.getAvatarID());
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
		Subscriber subscriber = new Subscriber(key);
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
}
