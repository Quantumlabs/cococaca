package org.quantumlabs.cococaca.backend.service.persistence;

import org.junit.Assert;
import org.junit.Test;
import org.quantumlabs.cococaca.backend.service.persistence.PersistenceFactory.NotSupportedPersistenceTypeException;
import org.quantumlabs.cococaca.backend.service.persistence.mock.UTPresistenceConfig;
import org.quantumlabs.cococaca.backend.service.preference.Config;
import org.quantumlabs.cococaca.backend.service.preference.Parameters;

import static org.mockito.Mockito.*;

public class PersistenceFactoryTest {

	@Test
	public void testGetPersistenceByPolicy() {
		PersistenceFactory fatory = new PersistenceFactory();
		Config config = new UTPresistenceConfig();
		IPersistence persistence = fatory.getPersistence(config);
		Assert.assertNotNull(persistence);
		Assert.assertTrue(persistence.isStarted());
	}

	@Test(expected = NotSupportedPersistenceTypeException.class)
	public void testGetPersistenceErrorDueToNoCorrespodingPersistenceType() {
		PersistenceFactory fatory = new PersistenceFactory();
		Config config = mock(Config.class);
		when(config.get(Parameters.CONFIG_PERSISTENCE_TYPE)).thenReturn("_unknown");
		fatory.getPersistence(config);
	}
}
