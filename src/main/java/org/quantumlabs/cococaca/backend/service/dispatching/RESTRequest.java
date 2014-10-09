/**
 * Copy right (c) yudzhou since 2014/9/29.
 */
package org.quantumlabs.cococaca.backend.service.dispatching;

import static org.quantumlabs.cococaca.backend.service.preference.Parameters.HTTP_HEADER_ACCEPT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.quantumlabs.cococaca.backend.Helper;

public class RESTRequest {
	private final HttpServletRequest request;
	private String locator;
	private Quantifier quantifier;
	private List<ResourceFilter> filters;

	private RESTRequest(HttpServletRequest request) {
		this.request = request;
		filters = new ArrayList<>();
	}

	public String getURL() {
		return Helper.getRelativeURL(request);
	}

	public String getAccept() {
		return request.getHeader(HTTP_HEADER_ACCEPT);
	}

	public static RESTRequest wrap(HttpServletRequest request) {
		return new RESTRequest(request);
	}

	public String getResourceLocator() {
		return locator;
	}

	public void setResourceLocator(String resourceLocator) {
		this.locator = resourceLocator;
	}

	public Quantifier getQuantifier() {
		return quantifier;
	}

	public void setQuantifier(Quantifier quantifier) {
		this.quantifier = quantifier;
	}

	public void addFilter(ResourceFilter... filter) {
		filters.addAll(Arrays.asList(filter));
	}

	public ResourceFilter[] getFilters() {
		return filters.toArray(new ResourceFilter[0]);
	}

	public static class ResourceFilter {
		private final String condition;
		private final String value;

		public ResourceFilter(String condition, String value) {
			this.condition = condition;
			this.value = value;
		}

		public String getCondition() {
			return condition;
		}

		public String getValue() {
			return value;
		}
	}

	public enum Quantifier {
		SINGULAR, PLURAL;
	}
}
