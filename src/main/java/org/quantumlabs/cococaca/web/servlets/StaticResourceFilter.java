package org.quantumlabs.cococaca.web.servlets;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.quantumlabs.cococaca.backend.transaction.Helper;
import org.quantumlabs.cococaca.backend.transaction.Parameters;

public class StaticResourceFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		String rawRequest = Helper.getRelativeURL((HttpServletRequest) request);
		if (isRequestForStaticHTML(rawRequest)) {
			chain.doFilter(request, response);
		} else {
			String decoratedRequest = Helper.decorateResourceRequest(rawRequest);
			request.getRequestDispatcher(decoratedRequest).forward(request, response);
		}
	}

	private boolean isRequestForStaticHTML(String rawRequest) {
		return rawRequest.startsWith(Parameters.URL_STATIC_HTML_PREFIX);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void destroy() {
	}
}
