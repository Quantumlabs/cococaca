package org.quantumlabs.cococaca.backend.transaction.handler;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.quantumlabs.cococaca.backend.Helper;
import org.quantumlabs.cococaca.backend.service.TXNManager;
import org.quantumlabs.cococaca.backend.service.dispatching.IResourceHandler;
import org.quantumlabs.cococaca.backend.service.dispatching.IResourceHandlerCallBack;
import org.quantumlabs.cococaca.backend.service.dispatching.RESTRequest;
import org.quantumlabs.cococaca.backend.service.dispatching.RESTRequest.ResourceFilter;
import org.quantumlabs.cococaca.backend.service.persistence.model.Danmuku;
import org.quantumlabs.cococaca.backend.service.persistence.model.IPostKey;
import org.quantumlabs.cococaca.backend.service.persistence.model.IPostKeyImpl;
import org.quantumlabs.cococaca.backend.service.preference.Parameters;
import org.quantumlabs.cococaca.backend.transaction.response.contenttype.JsonResponse;
import org.quantumlabs.cococaca.backend.transaction.response.responseentity.GeneralResponse;

public class DanmukuHandler implements IResourceHandler {

	@Override
	public boolean accept(String resourceLocator) {
		return Parameters.URL_DANMUKU_LOCATOR.equals(resourceLocator);
	}

	@Override
	public void put(RESTRequest request, IResourceHandlerCallBack callBack) {

	}

	@Override
	public void get(RESTRequest request, IResourceHandlerCallBack callBack) {
		switch (request.getQuantifier()) {
		case SINGULAR:
			break;
		case PLURAL:
			String postKeyValue = retrievePostKey(request);
			Helper.validateHTTPParameterNotNull("Post key for retrieving DANMUKU", postKeyValue);
			if (StringUtils.isEmpty(postKeyValue)) {
				callBack.onResourceHandlingFailed(request, null);
			} else {
				IPostKey postKey = new IPostKeyImpl(postKeyValue);
				final Danmuku[] danmukus = TXNManager.getInstance().getPersistence().fetchDanmukuByPostID(postKey);
				callBack.onResouceHandlingCompleted(request, new JsonResponse() {
					@Override
					public String get() {
						StringBuilder sb = new StringBuilder();
						sb.append("[");
						for (int danmukuIdx = 0; danmukuIdx < danmukus.length; danmukuIdx++) {
							sb.append(buildDanmuJson(danmukus[danmukuIdx]));
							if (danmukuIdx < danmukus.length - 1) {
								sb.append(",");
							}
						}
						sb.append("]");
						return sb.toString();
					}

				});
			}
			break;
		}
	}

	private String retrievePostKey(RESTRequest request) {
		ResourceFilter[] filters = request.getFilters();
		String postKeyValue = null;
		for (ResourceFilter filter : filters) {
			if ("postID".equals(filter.getCondition())) {
				postKeyValue = filter.getValue();
			}
		}
		return postKeyValue;
	}

	protected String buildDanmuJson(Danmuku danmuku) {
		return String.format("{ID:'%s', authorID:'%s', content:'%s', timestamp:'%s', postID:'%s'}", danmuku.getID(),
				danmuku.getAuthorID(), danmuku.getContent(), danmuku.getTimeStamp(), danmuku.getPostID());
	}

	@Override
	public void post(RESTRequest request, IResourceHandlerCallBack callBack) {
		HttpServletRequest httpRequest = (HttpServletRequest) request.getAttachment();
		String authorID = httpRequest.getParameter("authorID");
		String content = httpRequest.getParameter("content");
		String postID = httpRequest.getParameter("postID");
		Helper.validateHTTPParameterNotNull("authenID", authorID);
		Helper.validateHTTPParameterNotNull("content", content);
		Helper.validateHTTPParameterNotNull("postID", postID);
		Danmuku danmuku = new Danmuku();
		danmuku.setAuthorID(authorID);
		danmuku.setContent(content);
		danmuku.setPostID(postID);
		TXNManager.getInstance().getPersistence().insertDanmukuForPost(new IPostKeyImpl(postID), danmuku);
		callBack.onResouceHandlingCompleted(request, new GeneralResponse(true));
	}

	@Override
	public void delete(RESTRequest request, IResourceHandlerCallBack callBack) {
		
	}
}
