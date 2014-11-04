package org.quantumlabs.cococaca.backend.service.preference;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/***
 * Configuration from <code>resources/cococaca-preference.properties</code>
 * file. <strong>The instance is immutable</strong>, new instance needs to be
 * created after any change happening in cococaca-preference.properties file.
 * */
public class PreferenceConfig implements Config {
	private final Properties properties = new Properties();

	public PreferenceConfig() {
		try (InputStream utConfigFileInputStream = new FileInputStream(
				ResourceUtil.getSystemResource("cococaca-preference.properties"))) {
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
