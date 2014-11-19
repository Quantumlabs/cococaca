/**
 * Copy right (c) yudzhou since 2014/9/29.
 */
package org.quantumlabs.cococaca.backend.service.dispatching;

import static org.quantumlabs.cococaca.backend.service.preference.Parameters.HTTP_HEADER_ACCEPT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.quantumlabs.cococaca.backend.Helper;
import org.quantumlabs.cococaca.backend.service.preference.Parameters;

public class RESTRequest {
	private final HttpServletRequest request;
	private String locator;
	private Quantifier quantifier;
	private List<ResourceFilter> filters;
	private Object attachment;
	private Optional<String> id;
	private static final Pattern URL_WITHOUT_RESOURCE_PREFIX = Pattern.compile(String.format("%s(.*)",
			Parameters.URL_REST_RESOURCE_PREFIX));

	private RESTRequest(HttpServletRequest request) {
		this.request = request;
		id = Optional.empty();
		setAttachment(request);
		filters = new ArrayList<>();
	}

	/**
	 * Return a URL start with Resource locator. e.g. <code>/Subscriber/1</code>
	 * , <code>/Post/1</code> etc. reference to REST-api designs.
	 * */
	public String getURL() {
		String urlWithPrefix = Helper.getRelativeURL(request);
		Matcher matcher = URL_WITHOUT_RESOURCE_PREFIX.matcher(urlWithPrefix);
		Helper.assertTrue(
				String.format("Relative url %s request doesn't match \"/Resource/....\" pattern", urlWithPrefix),
				matcher.matches());
		return matcher.group(1);
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

	public Object getAttachment() {
		return attachment;
	}

	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}

	public Optional<String> getResourceIdentifier() {
		return id;
	}

	public void setResourceIdentifier(String id) {
		if (id != null) {
			this.id = Optional.of(id);
		}
	}
}
