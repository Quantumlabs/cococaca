package org.quantumlabs.cococaca.backend.service.dispatching;

public class MalformedRequestException extends Exception {
	private static final long serialVersionUID = -111847945879678350L;
	private final Object request;

	public MalformedRequestException(String message, Object request) {
		super(message);
		this.request = request;
	}

	public Object getRESTRequest() {
		return request;
	}
}
