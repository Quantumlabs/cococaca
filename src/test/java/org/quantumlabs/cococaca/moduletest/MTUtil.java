package org.quantumlabs.cococaca.moduletest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;

public class MTUtil {
	public static HttpServletRequest mockRequest() {
		HttpServletRequest request = mock(HttpServletRequest.class);
		return request;
	}

	public static HttpServletResponse mockResponse() {
		HttpServletResponse response = mock(HttpServletResponse.class);
		return response;
	}

	public static void verifyResponse() {
		// TODO Auto-generated method stub
		
	}
}
