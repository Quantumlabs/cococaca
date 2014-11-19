package org.quantumlabs.cococaca.backend.transaction.response.responseentity;

import java.util.function.Function;

import org.quantumlabs.cococaca.backend.Helper;
import org.quantumlabs.cococaca.backend.service.persistence.model.Danmuku;
import org.quantumlabs.cococaca.backend.service.persistence.model.Post;
import org.quantumlabs.cococaca.backend.transaction.response.contenttype.JsonResponse;

/**
 * Content-type: vrnd.org.quantumlabs.cococaca.post.reteive.v2 +json
 */
public class PostResponse implements JsonResponse {

	private static final String _JSON_TEMP = "{postID:'%s', author:'%s', content:'%s',description:'%s', danmuku:[%s] }";
	private static final String _DANMUKU_TEMP = "{author:'%s', content:'%s', timestamp:'%s'}";
	private Post[] posts;

	public PostResponse(Post... posts) {
		Helper.assertNotNull(posts);
		this.posts = posts;
	}

	@Override
	public String get() {
		return this.buildJsonArray(this::buildSinglePost, posts);
	}

	private String buildSinglePost(Post post) {
		return String.format(_JSON_TEMP, post.getKey().get(), post.getAuthorKey().get(), post.getContentKey().get(),
				post.getDescription(), buildDanmukus(post));
	}

	private <T> String buildJsonArray(Function<T, String> op, T[] suppliers) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for (int idx = 0; idx < suppliers.length; idx++) {
			T t = suppliers[idx];
			builder.append(op.apply(t));
			if (!isLast(suppliers.length, idx)) {
				builder.append(",");
			}
		}
		builder.append("]");
		return builder.toString();
	}

	private String buildDanmuku(Danmuku danmuku) {
		return String.format(_DANMUKU_TEMP, danmuku.getAuthorID(), danmuku.getContent(), danmuku.getTimeStamp());
	}

	private String buildDanmukus(Post post) {
		return buildJsonArray(this::buildDanmuku, post.getDanmukus());
	}

	private boolean isLast(int arrayLength, int idx) {
		return idx == arrayLength - 1;
	}
}
