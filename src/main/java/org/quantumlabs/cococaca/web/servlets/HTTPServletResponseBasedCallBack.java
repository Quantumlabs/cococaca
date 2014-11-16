package org.quantumlabs.cococaca.web.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import org.quantumlabs.cococaca.backend.service.dispatching.IResourceHandlerCallBack;
import org.quantumlabs.cococaca.backend.service.dispatching.RESTRequest;
import org.quantumlabs.cococaca.backend.transaction.response.contenttype.AcceptableResponse;
import org.quantumlabs.cococaca.backend.transaction.response.contenttype.BinaryReponse;
import org.quantumlabs.cococaca.backend.transaction.response.contenttype.JsonResponse;

public class HTTPServletResponseBasedCallBack implements
		IResourceHandlerCallBack {
	private final HttpServletResponse resp;

	public HTTPServletResponseBasedCallBack(HttpServletResponse resp) {
		this.resp = resp;
	}

	@Override
	public void onResourceHandlingFailed(RESTRequest request, Object attachment) {
		try (Writer writer = resp.getWriter()) {
			// General resource handling failure code.
			writer.write("{status:'255'}");
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onResouceHandlingCompleted(RESTRequest request,
			AcceptableResponse response) {
		writeToResponse(response);
	}

	private void writeToResponse(AcceptableResponse acceptableResponse) {
		AutoCloseable closableOutputChannel = null;
		try {
			if (acceptableResponse instanceof JsonResponse) {
				closableOutputChannel = resp.getWriter();
				((Writer) closableOutputChannel).write(JsonResponse.class.cast(
						acceptableResponse).get());
			} else if (acceptableResponse instanceof BinaryReponse) {
				closableOutputChannel = resp.getOutputStream();
				((OutputStream) closableOutputChannel)
						.write(BinaryReponse.class.cast(acceptableResponse)
								.get());
			} else {
				throw new RuntimeException(String.format(
						"Unsupported data content-type so for %s",
						acceptableResponse));
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
