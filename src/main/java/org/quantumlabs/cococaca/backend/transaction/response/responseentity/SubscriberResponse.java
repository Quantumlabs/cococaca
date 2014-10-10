package org.quantumlabs.cococaca.backend.transaction.response.responseentity;

import org.quantumlabs.cococaca.backend.service.persistence.model.Subscriber;
import org.quantumlabs.cococaca.backend.transaction.response.contenttype.JsonResponse;

/**
 * vrnd.org.quantumlabs.cococaca.user.v2 +json
 * */
public class SubscriberResponse implements JsonResponse {
	private Subscriber subscriber;

	public SubscriberResponse(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	@Override
	public String get() {
		return buildAcceptableJson(subscriber);
	}

	// content-type -> vrnd.org.quantumlabs.cococaca.user.v2+json @see
	// REST-API-BETA-V2
	// {avatar:<avatar-ID>, follower:[<follower-1-ID>,<follower-2-ID>...], ...}
	private String buildAcceptableJson(Subscriber subscriber) {
		// TODO
		return null;
	}
}
