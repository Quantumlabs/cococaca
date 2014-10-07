package org.quantumlabs.cococaca.backend.service.dispatching;

import org.quantumlabs.cococaca.backend.service.dispatching.RESTRequest.ResourceFilter;

/**
 * IResourceRountingPolicy stands for the manner of extracting information out
 * of the bare request, so that it could be used by corresponding
 * IResourceHandler.
 * */
public interface IResourceRoutingPolicy {
	/**
	 * Extracting the resource locator, the corresponding request could be
	 * processed by the <code>IResourceHandler</code> if is accepted by it.
	 * 
	 * @see {@link ResourceRouter#decorateAndRoute(RESTRequest)}
	 *      {@link org.quantumlabs.cococaca.backend.transaction.IResourceHandler#accept(String)}
	 * 
	 * @param request
	 *            The bare request string. It could be a URL string.
	 * 
	 * @return The resource identifier.
	 * */
	String extractResourceLocator(RESTRequest request);

	/**
	 * Extracting the resource filters, it would be used for filtering parts
	 * resources out of a set.
	 * 
	 * @see {@link ResourceRouter#decorateAndRoute(RESTRequest)}
	 * 
	 * @param bareRequest
	 *            The raw request string. It could be a URL string.
	 * 
	 * @return The resource filters.
	 * */
	ResourceFilter[] extractResourceFilters(RESTRequest bareRequest);
}
