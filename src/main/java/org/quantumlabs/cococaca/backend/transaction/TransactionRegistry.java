package org.quantumlabs.cococaca.backend.transaction;

import org.quantumlabs.cococaca.backend.BackEnd;
import org.quantumlabs.cococaca.backend.BackEnd.StartCallBack;
import org.quantumlabs.cococaca.backend.service.dispatching.ResourceRouter;
import org.quantumlabs.cococaca.backend.transaction.handler.SubscriberHandler;

public class TransactionRegistry implements StartCallBack {

	private ResourceRouter router;

	public TransactionRegistry(ResourceRouter resourceRouter) {
		this.router = resourceRouter;
	}

	@Override
	public void callBack(BackEnd event) {
		router.register(new SubscriberHandler());
	}

	@Override
	public Void get() {
		return null;
	}
}
