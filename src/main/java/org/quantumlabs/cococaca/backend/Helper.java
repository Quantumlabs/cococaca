package org.quantumlabs.cococaca.backend;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.quantumlabs.cococaca.backend.service.preference.Parameters;
import org.quantumlabs.cococaca.web.HTTPParameterMissingException;

public class Helper {

	private static DefaultResourceRequestDecorator decorator = new DefaultResourceRequestDecorator();

	public static void assertTrue(boolean expr) {
		assertTrue("", expr);
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

	public static void assertNotNull(String message, Object o) {
		if (!(Objects.nonNull(o))) {
			throw new AssertionError(message);
		}
	}

	public static void assertNotEmtryString(String string) {
		assertTrue(!isEmptyString(string));
	}

	public static boolean isEmptyString(String string) {
		return string == null || "".equals(string);
	}

	public static void logError(Exception e) {
		logError(null, e);
	}

	public static void logError(String message, Exception e) {
		if (message != null) {
			log(message);
		}
		log(appendRootCause(e));
	}

	private static String appendRootCause(Throwable e) {
		StringBuilder builder = new StringBuilder();
		if (e.getCause() != null) {
			return appendRootCause(e.getCause());
		} else {
			builder.append("Caused by --->");
			builder.append(e);
			builder.append("\n");
			String causeStackTrace = buildStackTrace(e);
			builder.append(causeStackTrace);
			return builder.toString();
		}
	}

	private static String buildStackTrace(Throwable cause) {
		StringBuilder builder = new StringBuilder();
		for (StackTraceElement stackElement : cause.getStackTrace()) {
			builder.append(stackElement.toString());
			builder.append("\n");
		}
		return builder.toString();
	}

	private static void log(String string) {
		System.out.println(string);
	}

	public static void assertTrue(String message, boolean expr) {
		if (!expr) {
			throw new AssertionError(message);
		}
	}

	// Only use for validating HTTP parameters, <strong></strong>
	public static void validateHTTPParameterNotNull(String message, Object o) throws HTTPParameterMissingException {
		if (o == null) {
			throw new HTTPParameterMissingException(message == null ? "" : message);
		}
	}

	public static void assertError() {
		throw new AssertionError();
	}

	public static void validateHTTPParameterNotNull(String userEmail) {
		validateHTTPParameterNotNull(null, userEmail);
	}

	public static void assertUnsupportedOperation() {
		throw new UnsupportedOperationException();
	}

	public static byte[] allocateBuffer() {
		return new byte[1024];
	}

	public static <P, R> void map(Function<P, R> applier, P... params) {
		for (P p : params) {
			applier.apply(p);
		}
	}
}
