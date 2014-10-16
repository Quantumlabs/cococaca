package org.quantumlabs.cococaca.backend.service.persistence;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.quantumlabs.cococaca.backend.service.persistence.model.ISubscriberKey;
import org.quantumlabs.cococaca.backend.service.persistence.model.ISubscriberKeyImpl;

public enum SubscriberKeyFactory {
	INSTANCE("md5");

	SubscriberKeyFactory(String algorithm) {
		try {
			this.keyGen = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Can't create SubscriberKeyFactory", e);
		}
	}

	private MessageDigest keyGen;

	/**
	 * Create a UUID based on input userName.
	 * */
	public ISubscriberKey newKey(String userName) {
		byte[] raw = new StringBuilder(64).append(userName).toString().getBytes(Charset.forName("utf-8"));
		byte[] digest = keyGen.digest(raw);
		final int _UNSIGN_INTEGER = 1;
		String keyString = new BigInteger(_UNSIGN_INTEGER, digest).toString(16);
		return new ISubscriberKeyImpl(keyString);
	}
}
