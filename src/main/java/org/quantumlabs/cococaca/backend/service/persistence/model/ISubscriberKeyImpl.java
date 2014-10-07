package org.quantumlabs.cococaca.backend.service.persistence.model;

/**
 * Copy right (c) yudzhou since 2014/9/29.
 */
public class ISubscriberKeyImpl implements ISubscriberKey {

    private final String value;

    public ISubscriberKeyImpl(String value) {
        this.value = value;
    }

    @Override
    public String get() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ISubscriberKeyImpl)) {
            return false;
        }
        return value.equals(((ISubscriberKeyImpl) obj).value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
