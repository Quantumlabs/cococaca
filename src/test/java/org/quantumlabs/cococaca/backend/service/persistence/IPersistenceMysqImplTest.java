package org.quantumlabs.cococaca.backend.service.persistence;

import org.junit.Test;
import static org.junit.Assert.*;


public class IPersistenceMysqImplTest {

	@Test
	public void testStart() {
		IPersistence persistence = new IPersistenceMysqlImpl();
		persistence.start();
		assertTrue(persistence.isStarted());
	}

	public void testStop() {
	}

	public void testCheckoutSingle() {
	}

	public void testCheckoutMany() {
	}

	public void testCheckinSingle() {
	}

	public void testCheckinMany() {
	}

	public void testCheckoutContestOnResourceLimitation() {
	}
}
