package org.quantumlabs.cococaca.backend;

import java.util.List;

import org.quantumlabs.cococaca.backend.service.TXNManager;
import org.quantumlabs.cococaca.backend.service.dispatching.CallBack;
import org.quantumlabs.cococaca.backend.transaction.TransactionRegistry;

public class BackEnd {
	private List<CallBack<BackEnd, Void>> startCallBacks;
	private List<CallBack<BackEnd, Void>> stopCallBacks;

	public void start() {
		doStart();
		notifyCallBacks(startCallBacks, this);
	}

	private void doStart() {
		// TODO Starting staffs, e.g. initializing system services. etc.
		initializeSystem();
		registerDefaultHooks();
	}

	private void initializeSystem() {
		TXNManager.getInstance().initialize();
	}

	private void notifyCallBacks(List<CallBack<BackEnd, Void>> callBacks, BackEnd event) {
		for (CallBack<BackEnd, Void> callBack : callBacks) {
			callBack.callBack(event);
		}
	}

	private void registerDefaultHooks() {
		registerStartHook(new TransactionRegistry(TXNManager.getInstance().getResourceRouter()));
	}

	public void stop() {
		notifyCallBacks(stopCallBacks, this);
		// TODO Stopping staffs
	}

	public void registerStartHook(StartCallBack callBack) {
		Helper.assertNotNull(callBack);
		startCallBacks.add(callBack);
	}

	public void registerStopHook(StopCallBack callBack) {
		Helper.assertNotNull(callBack);
		stopCallBacks.add(callBack);
	}

	public static interface StartCallBack extends CallBack<BackEnd, Void> {

	}

	public static interface StopCallBack extends CallBack<BackEnd, Void> {

	}

	public static void main(String[] args) {
		BackEnd backEnd = new BackEnd();
		backEnd.start();
	}
}
