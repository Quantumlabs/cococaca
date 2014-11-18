package org.quantumlabs.cococaca.backend.transaction.response.responseentity;

import org.quantumlabs.cococaca.backend.transaction.response.contenttype.JsonResponse;

/**
 * JSON content type : vrnd.org.quantumlabs.cococaca.general.response.v2 +json
 */
public class GeneralResponse implements JsonResponse {
	private boolean success;

	public GeneralResponse(boolean success) {
		this.success = success;
	}

	@Override
	public String get() {
		return String.format("{status:'%s'}", success ? 0 : 1);
	}
}
