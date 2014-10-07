package org.quantumlabs.cococaca.backend.transaction.response.responseentity;

import org.quantumlabs.cococaca.backend.service.persistence.model.Subscriber;
import org.quantumlabs.cococaca.backend.transaction.response.contenttype.AcceptableResponse;
import org.quantumlabs.cococaca.backend.transaction.response.contenttype.Json;

/**
 * vrnd.org.quantumlabs.cococaca.user.v2 +json
 * */
public class SubscriberResponse implements AcceptableResponse<Json> {
	private Subscriber subscriber;

	public SubscriberResponse(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	@Override
	public Json get() {
		return buildAcceptableJson(subscriber);
	}

	// content-type -> vrnd.org.quantumlabs.cococaca.user.v2+json @see
	// REST-API-BETA-V2
	// {avatar:<avatar-ID>, follower:[<follower-1-ID>,<follower-2-ID>...], ...}
	private Json buildAcceptableJson(Subscriber subscriber) {
		// TODO
		return null;
	}
}
