/**
 * Copy right (c) yudzhou since 2014/9/29.
 */
package org.quantumlabs.cococaca.backend.service.dispatching;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.quantumlabs.cococaca.backend.service.dispatching.RESTRequest.ResourceFilter;

public class ResourceRouter {
	private List<IResourceHandler> handlerRegistry;
	private ReentrantReadWriteLock readAndWriteLock = new ReentrantReadWriteLock();
	private ReadLock readLock = readAndWriteLock.readLock();
	private WriteLock writeLock = readAndWriteLock.writeLock();
	private IResourceRoutingPolicy policy;

	/**
	 * Register specific resource handler on the fly. After registering, the
	 * registered resource should be handled be corresponding handler.<br>
	 * Compare to static configuring by declaring URL in web.xml, the
	 * hot-register mechanism decouples from dependencies of servlet-tyle
	 * staffs, and it is more flexible.
	 * 
	 * @param resource
	 *            The resource to be registered.
	 * */
	public void register(IResourceHandler handler) {
		try {
			writeLock.lock();
			if (handlerRegistry.contains(handler)) {
				throw new IllegalStateException(String.format("% is already registeried.", handler));
			}
			handlerRegistry.add(handler);
		} finally {
			writeLock.unlock();
		}
	}

	public void setRountingPolicy(IResourceRoutingPolicy policy) {
		this.policy = policy;
	}

	/**
	 * Unregister specific resource handler on the fly. In case routing
	 * unregistered resource, that would cause error.
	 * 
	 * @param resource
	 *            The resource to be unregistered.
	 * */
	public void unregister(String resourceIdentifier) {
		try {
			writeLock.lock();
			handlerRegistry.remove(resourceIdentifier);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Decorate the request Route request to the corresponding handler.
	 * 
	 * @param request
	 *            The restful request.
	 * 
	 * @return IResourceHandler The handler that binds to the resource which is
	 *         requested.
	 * */
	public IResourceHandler decorateAndRoute(RESTRequest request) {
		RESTRequest restRequest = (RESTRequest) request;
		decorate(restRequest);
		return syncGetHandler(restRequest.getResourceLocator());
	}

	private void decorate(RESTRequest request) {
		request.setResourceLocator(policy.extractResourceLocator(request));
		Optional<ResourceFilter[]> filters = policy.extractResourceFilters(request);
		if (filters.isPresent()) {
			request.addFilter();
		}
		// TODO Decorate other fields of the request
	}

	private IResourceHandler syncGetHandler(String resourceLocator) {
		try {
			readLock.lock();
			for (IResourceHandler handler : handlerRegistry) {
				if (handler.accept(resourceLocator)) {
					return handler;
				}
			}
			throw new IllegalArgumentException(String.format("Illegal resource locator %s", resourceLocator));
		} finally {
			readLock.unlock();
		}
	}
}
