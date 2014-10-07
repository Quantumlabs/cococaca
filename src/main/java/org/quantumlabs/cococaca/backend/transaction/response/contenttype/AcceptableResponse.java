package org.quantumlabs.cococaca.backend.transaction.response.contenttype;

/**
 * An AcceptableReponse stands for a resource in corresponding content-type
 * which is requested.
 * */
public interface AcceptableResponse<T> {
	/**
	 * Get the content of corresponding content-type
	 * */
	T get();
}
