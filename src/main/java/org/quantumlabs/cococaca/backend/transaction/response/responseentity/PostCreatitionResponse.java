package org.quantumlabs.cococaca.backend.transaction.response.responseentity;

import org.quantumlabs.cococaca.backend.Helper;
import org.quantumlabs.cococaca.backend.service.persistence.model.Post;
import org.quantumlabs.cococaca.backend.transaction.response.contenttype.JsonResponse;

/**
 * Content-type: vrnd.org.quantumlabs.cococaca.post.create.v2 +json
 */
public class PostCreatitionResponse implements JsonResponse {
	private Post post;
	private final String TEMPLATE = "{status:'0', postID:'%s'}";

	public PostCreatitionResponse(Post post) {
		Helper.assertNotNull(post);
		this.post = post;
	}

	@Override
	public String get() {
		return String.format(TEMPLATE, post.getKey().get());
	}
}
