package org.quantumlabs.cococaca.backend.transaction.handler;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.quantumlabs.cococaca.backend.Helper;
import org.quantumlabs.cococaca.backend.service.dispatching.IResourceHandler;
import org.quantumlabs.cococaca.backend.service.dispatching.IResourceHandlerCallBack;
import org.quantumlabs.cococaca.backend.service.dispatching.RESTRequest;
import org.quantumlabs.cococaca.backend.service.persistence.model.ISubscriberKey;
import org.quantumlabs.cococaca.backend.service.preference.Parameters;
import org.quantumlabs.cococaca.backend.transaction.authorization.Credential;
import org.quantumlabs.cococaca.backend.transaction.authorization.IAuthorizationManager;
import org.quantumlabs.cococaca.backend.transaction.authorization.SessionBasedAuthenticationManager;
import org.quantumlabs.cococaca.backend.transaction.response.contenttype.JsonResponse;

public class AuthorizationHandler implements IResourceHandler {

	@Override
	public boolean accept(String resourceLocator) {
		return Parameters.URL_AUTHORIZATION.equals(resourceLocator);
	}

	@Override
	public void put(RESTRequest request, IResourceHandlerCallBack callBack) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void get(RESTRequest request, IResourceHandlerCallBack callBack) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void post(RESTRequest request, IResourceHandlerCallBack callBack) {
		HttpServletRequest httpRequest = (HttpServletRequest) request.getAttachment();
		String userEmail = httpRequest.getParameter("email");
		String userPassword = httpRequest.getParameter("password");
		Helper.validateHTTPParameterNotNull("user email", userEmail);
		Helper.validateHTTPParameterNotNull("user password", userPassword);
		IAuthorizationManager authorization = new SessionBasedAuthenticationManager(httpRequest.getSession());
		Credential credential = new Credential(userEmail, userPassword);
		Optional<ISubscriberKey> subscriberKey = authorization.authorize(credential);
		if (subscriberKey.isPresent()) {
			callBack.onResouceHandlingCompleted(request, new UserAuthorizationResponse(subscriberKey.get()));
		} else {
			callBack.onResourceHandlingFailed(request, null);
		}
	}

	@Override
	public void delete(RESTRequest request, IResourceHandlerCallBack callBack) {
		throw new UnsupportedOperationException();
	}

	static class UserAuthorizationResponse implements JsonResponse {
		ISubscriberKey userID;

		UserAuthorizationResponse(ISubscriberKey userID) {
			this.userID = userID;
		}

		@Override
		public String get() {
			return String.format("{userID:'%S'}", userID.get());
		}
	}
}
