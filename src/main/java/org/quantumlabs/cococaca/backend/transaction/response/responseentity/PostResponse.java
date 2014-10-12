package org.quantumlabs.cococaca.backend.transaction.response.responseentity;

import org.quantumlabs.cococaca.backend.service.persistence.model.Post;
import org.quantumlabs.cococaca.backend.transaction.response.contenttype.JsonResponse;

/**
 * Content-type: vrnd.org.quantumlabs.cococaca.post.v2 +json
 */
public class PostResponse implements JsonResponse {

    public PostResponse(Post post) {

    }

    @Override
    public String get() {
        return null;
    }
}
