package org.quantumlabs.cococaca.backend.transaction.response.responseentity;

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
		Helper.assertTrue(posts.length > 0);
		this.posts = posts;
	}

	@Override
	public String get() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for (Post post : posts) {
			builder.append(buildSinglePost(post));
		}
		builder.append("]");
		return builder.toString();
	}

	private String buildSinglePost(Post post) {
		return String.format(_JSON_TEMP, post.getKey().get(), post
				.getAuthorKey().get(), post.getContentKey().get(), post
				.getDescription(), buildDanmukus(post));
	}

	// TODO functional mapping iteration.
	// private <T> void buildJsonArray(bina<T> op, T... suppliers) {
	// StringBuilder builder = new StringBuilder();
	// for (int idx = 0; idx < suppliers.length; idx++) {
	// T t = suppliers[idx];
	// op.apply(t, u)
	// if (!isLast(danmukus.length, idx)) {
	// builder.append(",");
	// }
	// }
	// }

	private String buildDanmukus(Post post) {
		StringBuilder builder = new StringBuilder();
		Danmuku[] danmukus = post.getDanmukus();
		for (int idx = 0; idx < danmukus.length; idx++) {
			Danmuku danmuku = danmukus[idx];
			builder.append(String.format(_DANMUKU_TEMP, danmuku.getAuthorID(),
					danmuku.getContent(), danmuku.getTimeStamp()));
			if (!isLast(danmukus.length, idx)) {
				builder.append(",");
			}
		}
		return builder.toString();
	}

	private boolean isLast(int arrayLength, int idx) {
		return idx == arrayLength - 1;
	}
}
