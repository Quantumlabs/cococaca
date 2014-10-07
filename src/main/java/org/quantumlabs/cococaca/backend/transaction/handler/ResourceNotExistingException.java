package org.quantumlabs.cococaca.backend.transaction.handler;

/**
 * ResourceNotExistingException stands for valid request without existing
 * resource. It should be considered as a recoverable error case.<br>
 * Since handler call back could handle failure case, so no exception needed in
 * resource handling level.
 * */
@Deprecated
public class ResourceNotExistingException extends Exception {
	private static final long serialVersionUID = -5905948052011879395L;

	public ResourceNotExistingException(String message) {
		super(message);
	}
}
