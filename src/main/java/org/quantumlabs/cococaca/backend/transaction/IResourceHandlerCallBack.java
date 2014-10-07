package org.quantumlabs.cococaca.backend.transaction;

import org.quantumlabs.cococaca.backend.service.dispatching.RESTRequest;
import org.quantumlabs.cococaca.backend.transaction.response.contenttype.AcceptableResponse;

/**
 * Copy right (c) yudzhou since 2014/9/29.
 */
public interface IResourceHandlerCallBack {

	void onResouceHandlingCompleted(RESTRequest request, AcceptableResponse<?> response);

	void onResourceHandlingFailed(RESTRequest request, Object attachment);
}
