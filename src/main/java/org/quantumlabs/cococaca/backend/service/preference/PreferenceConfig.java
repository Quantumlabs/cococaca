package org.quantumlabs.cococaca.backend.service.preference;


/***
 * Configuration from <code>resources/cococaca-preference.properties</code>
 * file. <strong>The instance is immutable</strong>, new instance needs to be
 * created after any change happening in cococaca-preference.properties file.
 * */
public class PreferenceConfig implements Config {
	public PreferenceConfig() {
		// Load properties from that file.
	}

	@Override
	public String get(String key) {
		return null;
	}
}
