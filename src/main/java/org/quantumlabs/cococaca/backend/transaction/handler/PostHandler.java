package org.quantumlabs.cococaca.backend.transaction.handler;

import org.quantumlabs.cococaca.backend.service.TXNManager;
import org.quantumlabs.cococaca.backend.service.dispatching.IResourceHandler;
import org.quantumlabs.cococaca.backend.service.dispatching.IResourceHandlerCallBack;
import org.quantumlabs.cococaca.backend.service.dispatching.RESTRequest;
import org.quantumlabs.cococaca.backend.service.persistence.model.IPostKey;
import org.quantumlabs.cococaca.backend.service.persistence.model.Post;
import org.quantumlabs.cococaca.backend.service.preference.Parameters;
import org.quantumlabs.cococaca.backend.transaction.response.responseentity.PostResponse;

public class PostHandler implements IResourceHandler {

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
        //Create from KEY factory?
        IPostKey postKey = null;
        Post post = TXNManager.getInstance().getPersistence().fetchPost(postKey);
        callBack.onResouceHandlingCompleted(request, new PostResponse(post));
    }

    @Override
    public void post(RESTRequest request, IResourceHandlerCallBack callBack) {

    }

    @Override
    public void delete(RESTRequest request, IResourceHandlerCallBack callBack) {
    }
}
