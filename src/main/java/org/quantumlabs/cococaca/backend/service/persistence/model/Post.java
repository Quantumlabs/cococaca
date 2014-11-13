package org.quantumlabs.cococaca.backend.service.persistence.model;

public class Post {
	private IPostKey key;
	private ISubscriberKey authorKey;
	private IContentKey contentKey;
	private String description;

	public Post(IPostKey postKey) {
		key = postKey;
	}

	public IPostKey getKey() {
		return key;
	}

	public void setKey(IPostKey key) {
		this.key = key;
	}

	public ISubscriberKey getAuthorKey() {
		return authorKey;
	}

	public void setAuthorKey(ISubscriberKey authorKey) {
		this.authorKey = authorKey;
	}

	public IContentKey getContentKey() {
		return contentKey;
	}

	public void setContentKey(IContentKey contentKey) {
		this.contentKey = contentKey;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
