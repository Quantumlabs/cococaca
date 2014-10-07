package org.quantumlabs.cococaca.web.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import org.quantumlabs.cococaca.backend.service.dispatching.RESTRequest;
import org.quantumlabs.cococaca.backend.transaction.IResourceHandlerCallBack;
import org.quantumlabs.cococaca.backend.transaction.response.contenttype.AcceptableResponse;
import org.quantumlabs.cococaca.backend.transaction.response.contenttype.Binary;
import org.quantumlabs.cococaca.backend.transaction.response.contenttype.Json;

public class HTTPServletResponseBasedCallBack implements IResourceHandlerCallBack {
	private final HttpServletResponse resp;

	public HTTPServletResponseBasedCallBack(HttpServletResponse resp) {
		this.resp = resp;
	}

	@Override
	public void onResourceHandlingFailed(RESTRequest request, Object attachment) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResouceHandlingCompleted(RESTRequest request, AcceptableResponse<?> response) {
		writeToResponse(response.get());
	}

	private void writeToResponse(Object contentData) {
		AutoCloseable closableOutputChannel = null;
		try {
			if (contentData instanceof Json) {
				closableOutputChannel = resp.getWriter();
				((Writer) closableOutputChannel).write(Json.class.cast(contentData).get());
			} else if (contentData instanceof Binary) {
				closableOutputChannel = resp.getOutputStream();
				((OutputStream) closableOutputChannel).write(Binary.class.cast(contentData).get());
			} else {
				throw new RuntimeException(String.format("Unsupported data content-type so for %s", contentData));
			}
		} catch (IOException e) {
			handleEx(e);
		} finally {
			try {
				closableOutputChannel.close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void handleEx(IOException e) {
		// TODO Auto-generated method stub

	}
}
