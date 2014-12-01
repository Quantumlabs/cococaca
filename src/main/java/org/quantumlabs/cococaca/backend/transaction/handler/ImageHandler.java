package org.quantumlabs.cococaca.backend.transaction.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.Validate;
import org.quantumlabs.cococaca.backend.Helper;
import org.quantumlabs.cococaca.backend.service.TXNManager;
import org.quantumlabs.cococaca.backend.service.dispatching.IResourceHandler;
import org.quantumlabs.cococaca.backend.service.dispatching.IResourceHandlerCallBack;
import org.quantumlabs.cococaca.backend.service.dispatching.RESTRequest;
import org.quantumlabs.cococaca.backend.service.dispatching.RESTRequest.Quantifier;
import org.quantumlabs.cococaca.backend.service.preference.Parameters;

public class ImageHandler implements IResourceHandler {

	@Override
	public boolean accept(String resourceLocator) {
		return Parameters.URL_IMG_LOCATOR.equals(resourceLocator);
	}

	@Override
	public void put(RESTRequest request, IResourceHandlerCallBack callBack) {
		throw new UnsupportedOperationException("Unsupport PUT");
	}

	@Override
	public void get(RESTRequest request, final IResourceHandlerCallBack callBack) {
		if (Quantifier.SINGULAR.equals(request.getQuantifier())) {
			HttpServletResponse httpResponse = (HttpServletResponse) callBack.getAttachment();
			Optional<String> resourceID = request.getResourceIdentifier();
			Validate.isTrue(resourceID.isPresent());
			TXNManager.getInstance().getPersistence().read(resourceID.get(), (in) -> {
				try (OutputStream out = httpResponse.getOutputStream()) {
					byte[] buffer = Helper.allocateBuffer();
					while (in.read(buffer) > -1) {
						out.write(buffer);
					}
					out.flush();
					httpResponse.setStatus(200);
				} catch (IOException e) {
					Helper.logError("Write IMG into stream failed.", e);
					httpResponse.setStatus(501);
					throw new RuntimeException(e);
				}
				return null;
			});
		} else {
			throw new UnsupportedOperationException("Unsupport plural img retriving.");
		}
	}

	@Override
	public void post(RESTRequest request, IResourceHandlerCallBack callBack) {
		throw new UnsupportedOperationException("Unsupport PUT");

	}

	@Override
	public void delete(RESTRequest request, IResourceHandlerCallBack callBack) {
		throw new UnsupportedOperationException("Unsupport PUT");
	}
}
