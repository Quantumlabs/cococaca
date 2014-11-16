package org.quantumlabs.cococaca.backend.service.persistence.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.quantumlabs.cococaca.backend.Helper;

public class Post {
	private IPostKey key;
	private ISubscriberKey authorKey;
	private IContentKey contentKey;
	private String description;
	private List<Danmuku> danmukus;
	// Epoch time
	private long timeStamp;

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public Danmuku[] getDanmukus() {
		// Return an array image or a copy which is better than exploring raw
		// reference of the danmukus.
		return danmukus.toArray(new Danmuku[danmukus.size()]);
	}

	public void addDanmukus(Danmuku... danmukus) {
		Collections.addAll(this.danmukus, danmukus);
	}

	public Post(IPostKey postKey) {
		Helper.assertNotNull(postKey);
		key = postKey;
		danmukus = new ArrayList<>();
	}

	public IPostKey getKey() {
		return key;
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
