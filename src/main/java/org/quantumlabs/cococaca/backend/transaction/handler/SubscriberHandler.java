package org.quantumlabs.cococaca.backend.transaction.handler;

import static org.quantumlabs.cococaca.backend.service.preference.Parameters.URL_SUBSCRIBER_PLURAL_LOCATOR;
import static org.quantumlabs.cococaca.backend.service.preference.Parameters.URL_SUBSCRIBER_SINGULAR_LOCATOR;

import java.util.Optional;

import org.quantumlabs.cococaca.backend.service.TXNManager;
import org.quantumlabs.cococaca.backend.service.dispatching.IResourceHandler;
import org.quantumlabs.cococaca.backend.service.dispatching.IResourceHandlerCallBack;
import org.quantumlabs.cococaca.backend.service.dispatching.RESTRequest;
import org.quantumlabs.cococaca.backend.service.persistence.model.ISubscriberKey;
import org.quantumlabs.cococaca.backend.service.persistence.model.Subscriber;
import org.quantumlabs.cococaca.backend.transaction.response.responseentity.SubscriberResponse;

public class SubscriberHandler implements IResourceHandler {

	@Override
	public void put(RESTRequest request, IResourceHandlerCallBack callBack) {

	}

	@Override
	public void get(RESTRequest request, IResourceHandlerCallBack callBack) {
		String resourceLocator = request.getResourceLocator();
		// TODO plural handling
		getSingular(request, callBack);
	}

	private void getSingular(RESTRequest request, IResourceHandlerCallBack callBack) {
		ISubscriberKey key = createSubscriberKey();
		Optional<Subscriber> fetchedSubscriber = getSubscriber(key);
		if (!(fetchedSubscriber.isPresent())) {
			callBack.onResourceHandlingFailed(request, key);
		} else {
			callBack.onResouceHandlingCompleted(request, new SubscriberResponse(fetchedSubscriber.get()));
		}
	}

	private Optional<Subscriber> getSubscriber(ISubscriberKey subsriberKey) {
		return Optional.of(TXNManager.getInstance().getPersistence().fetchSubscriber(subsriberKey));
	}

	private ISubscriberKey createSubscriberKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void post(RESTRequest request, IResourceHandlerCallBack callBack) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(RESTRequest request, IResourceHandlerCallBack callBack) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean accept(String resourceLocator) {
		return URL_SUBSCRIBER_PLURAL_LOCATOR.equals(resourceLocator)
				|| URL_SUBSCRIBER_SINGULAR_LOCATOR.equals(resourceLocator);
	}
}
