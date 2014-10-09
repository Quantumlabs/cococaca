package org.quantumlabs.cococaca.backend.service.persistence;

import static org.quantumlabs.cococaca.backend.service.preference.Parameters.CONFIG_PERSISTENCE_TYPE;

import org.quantumlabs.cococaca.backend.service.preference.Config;

public class PersistenceFactory {
	private static final String _SUPPORTED_PERSISTENCE_TYPE_MYSQL = "mysql";

	public IPersistence getPersistence(Config config) {
		String persistenceType = config.get(CONFIG_PERSISTENCE_TYPE);
		IPersistence candidatePersistence;
		switch (persistenceType) {
		case _SUPPORTED_PERSISTENCE_TYPE_MYSQL:
			candidatePersistence = new IPersistenceMysqlImpl();
			candidatePersistence.start();
			break;
		default:
			throw new NotSupportedPersistenceTypeException(persistenceType);
		}
		return candidatePersistence;
	}

	class NotSupportedPersistenceTypeException extends RuntimeException {

		private static final long serialVersionUID = -5765617375749787042L;

		NotSupportedPersistenceTypeException(String message) {
			super(message);
		}
	}

	public static PersistenceFactory getInstance() {
		return null;
	}
}
