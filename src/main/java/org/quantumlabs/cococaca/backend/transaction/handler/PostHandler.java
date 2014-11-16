package org.quantumlabs.cococaca.backend.transaction.handler;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.quantumlabs.cococaca.backend.Helper;
import org.quantumlabs.cococaca.backend.service.TXNManager;
import org.quantumlabs.cococaca.backend.service.dispatching.IResourceHandler;
import org.quantumlabs.cococaca.backend.service.dispatching.IResourceHandlerCallBack;
import org.quantumlabs.cococaca.backend.service.dispatching.RESTRequest;
import org.quantumlabs.cococaca.backend.service.persistence.model.IContentKeyImpl;
import org.quantumlabs.cococaca.backend.service.persistence.model.IPostKey;
import org.quantumlabs.cococaca.backend.service.persistence.model.IPostKeyImpl;
import org.quantumlabs.cococaca.backend.service.persistence.model.ISubscriberKey;
import org.quantumlabs.cococaca.backend.service.persistence.model.ISubscriberKeyImpl;
import org.quantumlabs.cococaca.backend.service.persistence.model.Post;
import org.quantumlabs.cococaca.backend.service.preference.Parameters;
import org.quantumlabs.cococaca.backend.transaction.response.responseentity.PostCreatitionResponse;
import org.quantumlabs.cococaca.backend.transaction.response.responseentity.PostResponse;
import org.quantumlabs.cococaca.web.HTTPParameterMissingException;

public class PostHandler implements IResourceHandler {
	public PostHandler() {
		factory = new DiskFileItemFactory();
		factory.setSizeThreshold(Parameters.SVLT_PIC_UPLDR_BUFR_SIZE_THRESHOLD);
		factory.setRepository(Parameters.SVLT_PIC_UPLDR_REPO);
		setAllowedMaxUploadSize(Parameters.SVLT_PIC_UPLDR_SIZE_MAX);
	}

	@Override
	public boolean accept(String resourceLocator) {
		return Parameters.URL_POST_PLURAL_LOCATOR.equals(resourceLocator)
				|| Parameters.URL_POST_SINGULAR_LOCATOR.equals(resourceLocator);
	}

	@Override
	public void put(RESTRequest request, IResourceHandlerCallBack callBack) {

	}

	@Override
	public void get(RESTRequest request, IResourceHandlerCallBack callBack) {
		switch (request.getQuantifier()) {
		case SINGULAR:
			IPostKey postKey = null;
			Post post = TXNManager.getInstance().getPersistence()
					.fetchPost(postKey);
			callBack.onResouceHandlingCompleted(request, new PostResponse(post));
			break;
		case PLURAL:
			// Cache client token/key?
			String clientToken = "3";
			ISubscriberKey subscriberKey = new ISubscriberKeyImpl(clientToken);
			Post[] posts = TXNManager.getInstance().getPersistence()
					.fetchPostsForSubscriber(subscriberKey);
			callBack.onResouceHandlingCompleted(request,
					new PostResponse(posts));
			break;
		default:
			Helper.assertError();
		}

	}

	private DiskFileItemFactory factory;
	private long svltPicUpldSizeMax;

	public ServletFileUpload getFileUpload() {
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(svltPicUpldSizeMax);
		return upload;
	}

	// Max size should be configurable.
	public void setAllowedMaxUploadSize(long size) {
		svltPicUpldSizeMax = size;
	}

	@Override
	public void post(RESTRequest request, IResourceHandlerCallBack callBack) {
		HttpServletRequest httpRequest = (HttpServletRequest) request
				.getAttachment();
		try {
			List<FileItem> files = getFileUpload().parseRequest(httpRequest);
			// The post and the file content share the same key
			String sharedKey = storeFile(files);
			Post post = new Post(new IPostKeyImpl(sharedKey));
			post.setContentKey(new IContentKeyImpl(sharedKey));
			post.setDescription(extractDescription(files));
			post.setAuthorKey(new ISubscriberKeyImpl(extractAuthorKey(files)));
			TXNManager.getInstance().getPersistence().storePost(post);
			callBack.onResouceHandlingCompleted(request,
					new PostCreatitionResponse(post));
		} catch (FileUploadException | IOException e) {
			Helper.logError(e);
			callBack.onResourceHandlingFailed(request, null);
		}
	}

	// Currently, client send client ID directly, don't store ID in session
	// based on REST constrain.
	private String extractAuthorKey(List<FileItem> files)
			throws HTTPParameterMissingException {
		String authorKey = null;
		for (FileItem item : files) {
			if (item.isFormField()) {
				String fieldName = item.getFieldName();
				if (Parameters.SVLT_POST_AHTOR_ID.equals(fieldName)) {
					authorKey = item.getString();
				}
			}
		}
		Helper.validateHTTPParameterNotNull(
				"Post author key should not be null.", authorKey);
		return authorKey;
	}

	private String storeFile(List<FileItem> files) throws IOException {
		String fileContentKey = null;
		for (FileItem item : files) {
			if (!item.isFormField()) {
				fileContentKey = storeFileAndRetrieveKey(item);
			}
		}
		Helper.assertNotNull("Post file contentKey should not be null.",
				fileContentKey);
		return fileContentKey;
	}

	private String extractDescription(List<FileItem> files) {
		String description = null;
		for (FileItem item : files) {
			if (item.isFormField()) {
				String fieldName = item.getFieldName();
				if (Parameters.SVLT_STREAM_PARAM_DESCRIPTION.equals(fieldName)) {
					description = item.getString();
				}
			}
		}
		Helper.assertNotNull("Post description should not be null.",
				description);
		return description;
	}

	private String storeFileAndRetrieveKey(FileItem item) throws IOException {
		String fieldName = item.getFieldName();
		String fileName = item.getName();
		String contentType = item.getContentType();
		boolean isInMemory = item.isInMemory();
		long sizeInBytes = item.getSize();
		Helper.assertTrue(Parameters.FRONT_END_HTML_NEW_POST_FIELD
				.equals(fieldName));
		// Check file content type, img/jpeg etv.
		// Helper.assertTrue("multipart/form-data".equals(contentType));
		String key = TXNManager.getInstance().getPersistence()
				.write(item.getInputStream());
		item.getInputStream().close();
		return key;
	}

	private void processFormField(FileItem item) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(RESTRequest request, IResourceHandlerCallBack callBack) {
	}
}
