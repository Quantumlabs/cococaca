package org.quantumlabs.cococaca.backend.service.persistence.model;

public class IPostKeyImpl implements IPostKey {

	private final String value;

	public IPostKeyImpl(String value) {
		this.value = value;
	}

	@Override
	public String get() {
		return value;
	}
}
