package org.quantumlabs.cococaca.backend.service.persistence.model;

/**
 * Copy right (c) yudzhou since 2014/9/29.
 */
public class Subscriber {
	private final ISubscriberKey key;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getAvatarID() {
		return avatarID;
	}

	public void setAvatarID(String avatarID) {
		this.avatarID = avatarID;
	}

	private Gender gender;
	private String avatarID;

	public ISubscriberKey getKey() {
		return key;
	}

	public Subscriber(ISubscriberKey key) {
		this.key = key;
	}

	@Override
	public int hashCode() {
		// FIXME Should hash code change after its fields changing.
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Subscriber)) {
			return false;
		}
		return key.equals(((Subscriber) obj).key);
	}
}
