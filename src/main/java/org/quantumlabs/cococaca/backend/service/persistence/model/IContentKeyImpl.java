package org.quantumlabs.cococaca.backend.service.persistence.model;

public class IContentKeyImpl implements IContentKey {

	private final String value;

	public IContentKeyImpl(String value) {
		this.value = value;
	}

	@Override
	public String get() {
		return value;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof IContentKeyImpl && value.equals(((IContentKeyImpl) obj).value);
	}
}
