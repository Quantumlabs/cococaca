package org.quantumlabs.cococaca.web;

/**
 * ParameterMissingException stands for required parameters are missing in HTTP
 * request.
 * 
 * */
public class HTTPParameterMissingException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3621727428295322504L;

	public HTTPParameterMissingException(String message) {
		super(message);
	}
}
