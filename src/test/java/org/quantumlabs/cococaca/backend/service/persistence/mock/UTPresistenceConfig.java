package org.quantumlabs.cococaca.backend.service.persistence.mock;

import java.io.InputStream;
import java.util.Properties;

import org.quantumlabs.cococaca.backend.service.preference.Config;

public class UTPresistenceConfig implements Config {
	private final Properties properties = new Properties();

	public UTPresistenceConfig() {
		try (InputStream utConfigFileInputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(
				"cococaca-preference.properties")) {
			properties.load(utConfigFileInputStream);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String get(String key) {
		return properties.getProperty(key);
	}
}
