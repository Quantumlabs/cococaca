package org.quantumlabs.cococaca.backend.transaction.handler;

import java.util.function.BiFunction;

import javax.servlet.http.HttpServletRequest;

import org.quantumlabs.cococaca.backend.Helper;
import org.quantumlabs.cococaca.backend.service.TXNManager;
import org.quantumlabs.cococaca.backend.service.dispatching.IResourceHandler;
import org.quantumlabs.cococaca.backend.service.dispatching.IResourceHandlerCallBack;
import org.quantumlabs.cococaca.backend.service.dispatching.RESTRequest;
import org.quantumlabs.cococaca.backend.service.persistence.model.ISubscriberKey;
import org.quantumlabs.cococaca.backend.service.persistence.model.ISubscriberKeyImpl;
import org.quantumlabs.cococaca.backend.service.preference.Parameters;
import org.quantumlabs.cococaca.backend.transaction.response.responseentity.GeneralResponse;

public class FollowshipHandler implements IResourceHandler {

	@Override
	public boolean accept(String resourceLocator) {
		return Parameters.URL_FOLLOWSHIP_LOCATOR.equals(resourceLocator);
	}

	@Override
	public void put(RESTRequest request, IResourceHandlerCallBack callBack) {
		Helper.assertUnsupportedOperation();
	}

	@Override
	public void get(RESTRequest request, IResourceHandlerCallBack callBack) {
		Helper.assertUnsupportedOperation();
	}

	@Override
	public void post(RESTRequest request, IResourceHandlerCallBack callBack) {
		changeFollowship(request, TXNManager.getInstance().getPersistence()::follow, callBack);
	}

	private void changeFollowship(RESTRequest request, BiFunction<ISubscriberKey, ISubscriberKey, Void> changeAction,
			IResourceHandlerCallBack callBack) {
		HttpServletRequest httpRequest = (HttpServletRequest) request.getAttachment();
		String follower = httpRequest.getParameter("follower");
		String followee = httpRequest.getParameter("followee");
		Helper.validateHTTPParameterNotNull("follow", follower);
		Helper.validateHTTPParameterNotNull("followee", followee);
		ISubscriberKey followeeKey = new ISubscriberKeyImpl(followee);
		ISubscriberKey followerKey = new ISubscriberKeyImpl(follower);
		changeAction.apply(followerKey, followeeKey);
		callBack.onResouceHandlingCompleted(request, new GeneralResponse(true));
	}

	@Override
	public void delete(RESTRequest request, IResourceHandlerCallBack callBack) {
		changeFollowship(request, TXNManager.getInstance().getPersistence()::unfollow, callBack);
	}
}
