package org.quantumlabs.cococaca.backend.transaction;

import java.util.Objects;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class Helper {

	private static DefaultResourceRequestDecorator decorator = new DefaultResourceRequestDecorator();

	public static void assertTrue(boolean expr) {
		if (!expr) {
			throw new AssertionError("Not true");
		}
	}

	public static String decorateResourceRequest(String rawRequest) {
		return decorator.decorate(rawRequest);
	}

	public static String undecorateResourceRequest(String decoratedRequest) {
		return decorator.undecorate(decoratedRequest);
	}

	private static class DefaultResourceRequestDecorator {
		private String decorate(String rawRequest) {
			StringBuilder sb = new StringBuilder(Parameters.URL_REST_RESOURCE_PREFIX.length() + rawRequest.length());
			return sb.append(Parameters.URL_REST_RESOURCE_PREFIX).append(rawRequest).toString();
		}

		private String undecorate(String decoratedRequest) {
			return decoratedRequest.substring(Parameters.URL_REST_RESOURCE_PREFIX.length());
		}
	}

	/**
	 * Stripping application-context of the URL of particular request,
	 * <strong>the relative URL starts with slash(/)</strong>.<br>
	 * Let's take an example, e.g.
	 * <code>/application-context/resource-1/1 </code> would be converted to
	 * <code>/resource-1/1</code>
	 * */
	public static String getRelativeURL(HttpServletRequest request) {
		String contextPath = ((HttpServletRequest) request).getContextPath();
		return ((HttpServletRequest) request).getRequestURI().substring(contextPath.length());
	}

	public static void assertNotNull(Object o) {
		if (!(Objects.nonNull(o))) {
			throw new AssertionError("Null is not allowed.");
		}
	}
}
