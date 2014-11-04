/**
 * Copy right (c) yudzhou since 2014/9/29.
 */
package org.quantumlabs.cococaca.backend.service.dispatching;

import java.util.ArrayList;
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

	public ResourceRouter() {
		handlerRegistry = new ArrayList<>();
	}

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
	 * Route request to the corresponding handler.
	 * 
	 * @param request
	 *            The restful request.
	 * 
	 * @return IResourceHandler The handler that binds to the resource which is
	 *         requested.
	 * @throws MalformedRequestException
	 * */
	public IResourceHandler retrieveResourceHandler(RESTRequest request) throws MalformedRequestException {
		return syncGetHandler(request.getResourceLocator());
	}

	private IResourceHandler syncGetHandler(String resourceLocator) throws MalformedRequestException {
		try {
			readLock.lock();
			for (IResourceHandler handler : handlerRegistry) {
				if (handler.accept(resourceLocator)) {
					return handler;
				}
			}
			throw new MalformedRequestException(String.format("No resource handler for %s ", resourceLocator), null);
		} finally {
			readLock.unlock();
		}
	}
}
