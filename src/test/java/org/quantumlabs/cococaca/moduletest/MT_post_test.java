package org.quantumlabs.cococaca.moduletest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import static org.mockito.Mockito.*;
import org.quantumlabs.cococaca.web.servlets.RequestEntryServlet;

public class MT_post_test {

	String contextPath = "_context";

	@Test
	public void sendAPost_thenBeVisibleInOwnPosts() throws ServletException, IOException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getContextPath()).thenReturn(contextPath);
		when(request.getRequestURI()).thenReturn("http://" + contextPath + "/Posts");
		when(request.getParameter("author")).thenReturn("_john");
		when(request.getParameter("description")).thenReturn("_fackDescription");
		HttpServletResponse response = MTUtil.mockResponse();
		RequestEntryServlet entry = prepareEntry();
		entry.doPost(request, response);
		MTUtil.verifyResponse();
	}

	private RequestEntryServlet prepareEntry() {
		RequestEntryServlet entryServlet = new RequestEntryServlet();
		return entryServlet;
	}
}
