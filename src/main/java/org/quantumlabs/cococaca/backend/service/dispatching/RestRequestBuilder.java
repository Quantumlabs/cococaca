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
		Optional<ResourceFilter[]> filters = policy.extractResourceFilters(request);
		if (filters.isPresent()) {
			request.addFilter();
		}
		// TODO Decorate other fields of the request
	}

	public RESTRequest build(HttpServletRequest request) {
		RESTRequest restRequest = RESTRequest.wrap(request);
		decorate(restRequest);
		return restRequest;
	}

}
