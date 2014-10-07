package org.quantumlabs.cococaca.backend.service.dispatching;

/**
 * Call-back model for client implementation.<br>
 * A BallBack will be notified by the registry on event <code>E</code>. And it
 * is able to get a result <code>R</code> after notification.
 * */
public interface CallBack<E, R> {
	/**
	 * Registry notifies the CallBack while the event occurs.
	 * 
	 * @param event
	 *            The notified event.
	 * */
	void callBack(E event);

	/**
	 * CallBack should be able to return a result according to the notified
	 * event.
	 * 
	 * @return r The result.
	 * */
	R get();
}
