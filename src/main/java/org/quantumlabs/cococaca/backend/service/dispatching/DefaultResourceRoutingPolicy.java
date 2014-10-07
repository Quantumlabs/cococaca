package org.quantumlabs.cococaca.backend.service.dispatching;

import org.quantumlabs.cococaca.backend.service.dispatching.RESTRequest.ResourceFilter;

public class DefaultResourceRoutingPolicy implements IResourceRoutingPolicy {

	@Override
	public String extractResourceLocator(RESTRequest request) {
		String url = request.getURL();
		return null;
	}

	@Override
	public ResourceFilter[] extractResourceFilters(RESTRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

}
