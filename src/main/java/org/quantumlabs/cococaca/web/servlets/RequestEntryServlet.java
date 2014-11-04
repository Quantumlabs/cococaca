/**
 * Copy right (c) yudzhou since 2014/9/29.
 */
package org.quantumlabs.cococaca.web.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.quantumlabs.cococaca.backend.service.TXNManager;
import org.quantumlabs.cococaca.backend.service.dispatching.DefaultResourceRoutingPolicy;
import org.quantumlabs.cococaca.backend.service.dispatching.IResourceHandler;
import org.quantumlabs.cococaca.backend.service.dispatching.MalformedRequestException;
import org.quantumlabs.cococaca.backend.service.dispatching.RESTRequest;
import org.quantumlabs.cococaca.backend.service.dispatching.RestRequestBuilder;

public class RequestEntryServlet extends HttpServlet {
	private static final long serialVersionUID = -5412265740476356268L;
	private RestRequestBuilder requestBuilder;

	public RequestEntryServlet() {
		requestBuilder = new RestRequestBuilder();
		requestBuilder.setRountingPolicy(new DefaultResourceRoutingPolicy());
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		RESTRequest restRequest = requestBuilder.build(req);
		try {
			retrieveRequestHandler(restRequest).get(restRequest, new HTTPServletResponseBasedCallBack(resp));
		} catch (MalformedRequestException e) {
			handleMalforedRequestFound(e, req, resp);
		}
	}

	private void handleMalforedRequestFound(MalformedRequestException e, HttpServletRequest req,
			HttpServletResponse resp) {
		resp.setStatus(403);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		RESTRequest restRequest = requestBuilder.build(req);
		try {
			retrieveRequestHandler(restRequest).post(restRequest, new HTTPServletResponseBasedCallBack(resp));
		} catch (MalformedRequestException e) {
			handleMalforedRequestFound(e, req, resp);
		}
	}

	private IResourceHandler retrieveRequestHandler(RESTRequest restRequest) throws MalformedRequestException {
		IResourceHandler handler = TXNManager.getInstance().getResourceRouter().retrieveResourceHandler(restRequest);
		if (handler == null) {
			throw new MalformedRequestException(restRequest.getURL(), restRequest);
		} else {
			return handler;
		}
	}

	@Override
	public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		RESTRequest restRequest = requestBuilder.build(req);
		try {
			retrieveRequestHandler(restRequest).put(restRequest, new HTTPServletResponseBasedCallBack(resp));
		} catch (MalformedRequestException e) {
			handleMalforedRequestFound(e, req, resp);
		}
	}

	@Override
	public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		RESTRequest restRequest = requestBuilder.build(req);
		try {
			retrieveRequestHandler(restRequest).delete(restRequest, new HTTPServletResponseBasedCallBack(resp));
		} catch (MalformedRequestException e) {
			handleMalforedRequestFound(e, req, resp);
		}
	}
}
