package org.quantumlabs.cococaca.backend.transaction.handler;

import org.quantumlabs.cococaca.backend.service.dispatching.RESTRequest;
import org.quantumlabs.cococaca.backend.transaction.IResourceHandler;
import org.quantumlabs.cococaca.backend.transaction.IResourceHandlerCallBack;
import org.quantumlabs.cococaca.backend.transaction.Parameters;

public class PostHandler implements IResourceHandler {

	@Override
	public boolean accept(String resourceLocator) {
		return Parameters.URL_POST_PLURAL_LOCATOR.equals(resourceLocator)
				|| Parameters.URL_POST_SINGULAR_LOCATOR.equals(resourceLocator);
	}

	@Override
	public void put(RESTRequest request, IResourceHandlerCallBack callBack) {

	}

	@Override
	public void get(RESTRequest request, IResourceHandlerCallBack callBack) {

	}

	@Override
	public void post(RESTRequest request, IResourceHandlerCallBack callBack) {

	}

	@Override
	public void delete(RESTRequest request, IResourceHandlerCallBack callBack) {
	}
}
