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
import org.quantumlabs.cococaca.backend.service.dispatching.MalformedRequestException;
import org.quantumlabs.cococaca.backend.service.dispatching.RESTRequest;

public class RequestEntryServlet extends HttpServlet {
	private static final long serialVersionUID = -5412265740476356268L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			RESTRequest restRequest = toRESTRequest(req);
			assertQualified(restRequest);
			TXNManager.getInstance().getResourceRouter().decorateAndRoute(restRequest)
					.get(restRequest, new HTTPServletResponseBasedCallBack(resp));
		} catch (MalformedRequestException e) {
			handleMalforedRequestFound(e, req, resp);
		}
	}

	private void handleMalforedRequestFound(MalformedRequestException e, HttpServletRequest req,
			HttpServletResponse resp) {
		// TODO 404 error handling?
		resp.setStatus(404);
	}

	private RESTRequest toRESTRequest(HttpServletRequest req) {
		return RESTRequest.wrap(req);
	}

	private void assertQualified(Object resourceRequest) throws MalformedRequestException {
		throw new MalformedRequestException("", resourceRequest);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			RESTRequest restRequest = toRESTRequest(req);
			assertQualified(restRequest);
			TXNManager.getInstance().getResourceRouter().decorateAndRoute(restRequest)
					.post(restRequest, new HTTPServletResponseBasedCallBack(resp));
		} catch (MalformedRequestException e) {
			handleMalforedRequestFound(e, req, resp);
		}
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			RESTRequest restRequest = toRESTRequest(req);
			assertQualified(restRequest);
			TXNManager.getInstance().getResourceRouter().decorateAndRoute(restRequest)
					.put(restRequest, new HTTPServletResponseBasedCallBack(resp));
		} catch (MalformedRequestException e) {
			handleMalforedRequestFound(e, req, resp);
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			RESTRequest restRequest = toRESTRequest(req);
			assertQualified(restRequest);
			TXNManager.getInstance().getResourceRouter().decorateAndRoute(restRequest)
					.delete(restRequest, new HTTPServletResponseBasedCallBack(resp));
		} catch (MalformedRequestException e) {
			handleMalforedRequestFound(e, req, resp);
		}
	}
}
