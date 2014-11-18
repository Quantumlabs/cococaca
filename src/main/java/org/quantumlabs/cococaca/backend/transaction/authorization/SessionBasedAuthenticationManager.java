package org.quantumlabs.cococaca.backend.transaction.authorization;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.quantumlabs.cococaca.backend.service.TXNManager;
import org.quantumlabs.cococaca.backend.service.persistence.model.ISubscriberKey;
import org.quantumlabs.cococaca.backend.service.preference.Parameters;

public class SessionBasedAuthenticationManager implements IAuthorizationManager {
	private final HttpSession session;

	public SessionBasedAuthenticationManager(HttpSession session) {
		this.session = session;
	}

	public Optional<ISubscriberKey> authorize(Credential credential) {
		ISubscriberKey key = TXNManager.getInstance().getPersistence().authorize(credential);
		if (key == null) {
			return Optional.empty();
		} else {
			session.setAttribute(Parameters.HTTP_SESSION_ATTRIBUTE_SUBSCRIBER_KEY, key);
			return Optional.of(key);
		}
	}
}
