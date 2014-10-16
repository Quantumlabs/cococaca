package org.quantumlabs.cococaca.backend.service.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.quantumlabs.cococaca.backend.UnitTestUtil;
import org.quantumlabs.cococaca.backend.service.persistence.mock.UTPersistenceConfig;
import org.quantumlabs.cococaca.backend.service.persistence.model.Gender;
import org.quantumlabs.cococaca.backend.service.persistence.model.ISubscriberKey;
import org.quantumlabs.cococaca.backend.service.persistence.model.Subscriber;
import org.quantumlabs.cococaca.backend.service.preference.Config;

public class IPersistenceMysqImplTest {
	private IPersistence persistence;

	@BeforeClass
	public static void beforeClass() {
	}

	@AfterClass
	public static void afterClass() {
		// UnitTestUtil.tearDownDBForUnitTest();
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
		Config mockConfig = new UTPersistenceConfig();
		persistence = new IPersistenceMysqlImpl(mockConfig);
	}

	@Test
	public void testFetchSubscriber() {
		preparePersistence();
		ISubscriberKey key = SubscriberKeyFactory.INSTANCE.newKey("_john");
		Subscriber subscriber = new Subscriber(key);
		subscriber.setAvatarID("_avatarID");
		subscriber.setGender(Gender.MALE);
		subscriber.setName("_john");
		persistence.storeSubscriber(subscriber, "_password");
		Subscriber fetchedSubscriber = persistence.fetchSubscriber(key);
		assertEquals(subscriber, fetchedSubscriber);
		assertEquals("_avatarID", fetchedSubscriber.getAvatarID());
		assertEquals(Gender.MALE, fetchedSubscriber.getGender());
		assertEquals("_john", fetchedSubscriber.getName());
		UnitTestUtil.clearAllTables();
	}
}
