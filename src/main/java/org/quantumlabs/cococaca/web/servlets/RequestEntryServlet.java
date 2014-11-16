/**
 * Copy right (c) yudzhou since 2014/9/29.
 */
package org.quantumlabs.cococaca.web.servlets;

import java.io.IOException;
import java.util.function.BiFunction;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.quantumlabs.cococaca.backend.Helper;
import org.quantumlabs.cococaca.backend.service.TXNManager;
import org.quantumlabs.cococaca.backend.service.dispatching.DefaultResourceRoutingPolicy;
import org.quantumlabs.cococaca.backend.service.dispatching.IResourceHandler;
import org.quantumlabs.cococaca.backend.service.dispatching.MalformedRequestException;
import org.quantumlabs.cococaca.backend.service.dispatching.RESTRequest;
import org.quantumlabs.cococaca.backend.service.dispatching.RestRequestBuilder;
import org.quantumlabs.cococaca.web.HTTPParameterMissingException;

public class RequestEntryServlet extends HttpServlet {
	private static final long serialVersionUID = -5412265740476356268L;
	private RestRequestBuilder requestBuilder;

	public RequestEntryServlet() {
		requestBuilder = new RestRequestBuilder();
		requestBuilder.setRountingPolicy(new DefaultResourceRoutingPolicy());
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doHandlerOperation(
				(restRequest, httpResponse) -> {
					retrieveRequestHandler(restRequest).get(restRequest,
							new HTTPServletResponseBasedCallBack(httpResponse));
					return null;
				}, req, resp);
	}

	private void handleMalforedRequestFound(MalformedRequestException e,
			HttpServletRequest req, HttpServletResponse resp) {
		resp.setStatus(403);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doHandlerOperation(
				(restRequest, httpResponse) -> {
					retrieveRequestHandler(restRequest).post(restRequest,
							new HTTPServletResponseBasedCallBack(httpResponse));
					return null;
				}, req, resp);

	}

	public void doHandlerOperation(
			BiFunction<RESTRequest, HttpServletResponse, Void> hanlder,
			HttpServletRequest req, HttpServletResponse resp) {
		try {
			hanlder.apply(requestBuilder.build(req), resp);
		} catch (MalformedRequestException e) {
			handleMalforedRequestFound(e, req, resp);
		} catch (HTTPParameterMissingException e) {
			Helper.logError("Missing required parameter", e);
			resp.setStatus(402);
		}
	}

	private IResourceHandler retrieveRequestHandler(RESTRequest restRequest)
			throws MalformedRequestException {
		IResourceHandler handler = TXNManager.getInstance().getResourceRouter()
				.retrieveResourceHandler(restRequest);
		if (handler == null) {
			throw new MalformedRequestException(restRequest.getURL(),
					restRequest);
		} else {
			return handler;
		}
	}

	@Override
	public void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doHandlerOperation(
				(restRequest, httpResponse) -> {
					retrieveRequestHandler(restRequest).put(restRequest,
							new HTTPServletResponseBasedCallBack(httpResponse));
					return null;
				}, req, resp);
	}

	@Override
	public void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		RESTRequest restRequest = requestBuilder.build(req);
		try {
			retrieveRequestHandler(restRequest).delete(restRequest,
					new HTTPServletResponseBasedCallBack(resp));
		} catch (MalformedRequestException e) {
			handleMalforedRequestFound(e, req, resp);
		}
	}
}
