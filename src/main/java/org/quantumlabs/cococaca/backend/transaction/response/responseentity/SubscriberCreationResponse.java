package org.quantumlabs.cococaca.backend.transaction.response.responseentity;

import org.quantumlabs.cococaca.backend.Helper;
import org.quantumlabs.cococaca.backend.transaction.response.contenttype.JsonResponse;

/**
 * JSON content type : vrnd.org.quantumlabs.cococaca.user.id.v2 +json
 * */
public class SubscriberCreationResponse implements JsonResponse {

	private String ID;

	public SubscriberCreationResponse(String userID) {
		Helper.assertNotEmtryString(userID);
		this.ID = userID;
	}

	@Override
	public String get() {
		return String.format("{userID:'%s'}", ID);
	}
}
