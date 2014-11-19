package org.quantumlabs.cococaca.backend.transaction;

import org.quantumlabs.cococaca.backend.BackEnd;
import org.quantumlabs.cococaca.backend.BackEnd.StartCallBack;
import org.quantumlabs.cococaca.backend.service.dispatching.ResourceRouter;
import org.quantumlabs.cococaca.backend.transaction.handler.AuthorizationHandler;
import org.quantumlabs.cococaca.backend.transaction.handler.FollowshipHandler;
import org.quantumlabs.cococaca.backend.transaction.handler.PostHandler;
import org.quantumlabs.cococaca.backend.transaction.handler.SubscriberHandler;

public class TransactionRegistry implements StartCallBack {

	private ResourceRouter router;

	public TransactionRegistry(ResourceRouter resourceRouter) {
		this.router = resourceRouter;
	}

	/**
	 * Any resource handler should be registered to router in the
	 * <code>callBack</code> method.
	 * */
	@Override
	public void callBack(BackEnd event) {
		router.register(new SubscriberHandler());
		router.register(new PostHandler());
		router.register(new AuthorizationHandler());
		router.register(new FollowshipHandler());
	}

	@Override
	public Void get() {
		// Nothing needed.
		return null;
	}
}
