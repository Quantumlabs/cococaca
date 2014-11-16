package org.quantumlabs.cococaca.backend.service.dispatching;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.quantumlabs.cococaca.backend.service.dispatching.RESTRequest.ResourceFilter;

public class RestRequestBuilder {
	private IResourceRoutingPolicy policy;

	public void setRountingPolicy(IResourceRoutingPolicy policy) {
		this.policy = policy;
	}

	/**
	 * Decorate the request
	 * */
	private void decorate(RESTRequest request) {
		request.setResourceLocator(policy.extractResourceLocator(request));
		request.setQuantifier(policy.extractQuantifier(request));
		request.setResourceIdentifier(policy.extractResourceIdentifier(request));
		ResourceFilter[] filters = policy.extractResourceFilters(request);
		if (filters != null) {
			request.addFilter(filters);
		}
	}

	public RESTRequest build(HttpServletRequest request) {
		RESTRequest restRequest = RESTRequest.wrap(request);
		decorate(restRequest);
		return restRequest;
	}

}
