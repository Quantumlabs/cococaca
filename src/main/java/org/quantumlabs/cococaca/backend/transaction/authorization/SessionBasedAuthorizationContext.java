package org.quantumlabs.cococaca.backend.transaction.authorization;

import javax.servlet.http.HttpSession;

import org.quantumlabs.cococaca.backend.Helper;
import org.quantumlabs.cococaca.backend.service.preference.Parameters;

public class SessionBasedAuthorizationContext {
	private final HttpSession session;

	public SessionBasedAuthorizationContext(HttpSession session) {
		this.session = session;
	}

	public String getClientToken() {
		String token = (String) session.getAttribute(Parameters.HTTP_SESSION_ATTRIBUTE_SUBSCRIBER_KEY);
		if (Helper.isEmptyString(token)) {
			throw new IllegalStateException("Not authroized");
		}
		return token;
	}
}
