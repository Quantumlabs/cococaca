package org.quantumlabs.cococaca.backend.service.preference;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;

public class ResourceUtil {
	private static ClassLoader classLoader;

	public static File getSystemResource(String path) throws FileNotFoundException {
		try {
			URL url = classLoader.getResource(path);
			if (url != null) {
				return new File(url.toURI());
			} else {
				throw new FileNotFoundException(path);
			}
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setSystemClassLoader(ClassLoader classLoader) {
		ResourceUtil.classLoader = classLoader;
	}
}
