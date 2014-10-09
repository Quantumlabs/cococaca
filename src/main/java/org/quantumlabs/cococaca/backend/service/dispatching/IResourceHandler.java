package org.quantumlabs.cococaca.backend.service.dispatching;


/**
 * Copy right (c) yudzhou since 2014/9/29.
 */
public interface IResourceHandler {
	/**
	 * Specific resource only needs to implement <code>accept</code> method.
	 * 
	 * That means to clarify which kind of logic resource is represented by the
	 * resource object and is able to be handled by the binded handler.
	 * 
	 * @param resourceLocator
	 *            Part of the REST-full request, it identifies which kind of
	 *            resource is requested.
	 * @return <code>true</code>, if the fragment is represented by the resource
	 *         object. Otherwise, <code>false</code>.
	 */
	boolean accept(String resourceLocator);

	void put(RESTRequest request, IResourceHandlerCallBack callBack);

	void get(RESTRequest request, IResourceHandlerCallBack callBack);

	void post(RESTRequest request, IResourceHandlerCallBack callBack);

	void delete(RESTRequest request, IResourceHandlerCallBack callBack);
}
