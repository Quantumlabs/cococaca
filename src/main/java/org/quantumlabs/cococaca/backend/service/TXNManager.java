package org.quantumlabs.cococaca.backend.service;

import org.quantumlabs.cococaca.backend.service.dispatching.ResourceRouter;
import org.quantumlabs.cococaca.backend.service.persistence.IPersistence;
import org.quantumlabs.cococaca.backend.service.persistence.PersistenceFactory;
import org.quantumlabs.cococaca.backend.service.preference.Config;
import org.quantumlabs.cococaca.backend.service.preference.PreferenceConfig;

public class TXNManager {
	private IPersistence persistence;
	private ResourceRouter resourceRouter;

	/**
	 * Fetch system services related preferences from preference module,
	 * initialize each system services based on corresponding preferences.<br>
	 * In case, any system service related preference changes, un-initialization
	 * and re-initialization are both needed.
	 * */
	public void initialize() {
		Config config = new PreferenceConfig();
		persistence = new PersistenceFactory().getPersistence(config);
		resourceRouter = new ResourceRouter();
	}

	public static TXNManager getInstance() {
		return InstanceHolder.instance;
	}

	public IPersistence getPersistence() {
		return persistence;
	}

	public ResourceRouter getResourceRouter() {
		return resourceRouter;
	}

	private static class InstanceHolder {
		static TXNManager instance = new TXNManager();
	}
}
