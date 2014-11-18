package org.quantumlabs.cococaca.backend.transaction.authorization;

import java.util.Optional;

import org.quantumlabs.cococaca.backend.service.persistence.model.ISubscriberKey;

public interface IAuthorizationManager {
	/**
	 * Authorize the credential, return a client token string.
	 * */
	Optional<ISubscriberKey> authorize(Credential credential);
}
