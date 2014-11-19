package org.quantumlabs.cococaca.backend.service.preference;

import java.io.File;

public class Parameters {
	public static final String CONTENT_TYPE_VRND_QLBS_COCA_USR_V2_JSN_FMT = "vrnd.org.quantumlabs.cococaca.user.v2+json";

	public static final String URL_FILTER_DELIMITER = "\\?";
	public static final String URL_FILTER_CONCATENATOR = "&";
	public static final String HTTP_HEADER_ACCEPT = "Accept";

	// @start Resources locators
	public static final String URL_REST_RESOURCE_PREFIX = "/Resource";
	public static final String URL_STATIC_HTML_PREFIX = "/static";
	public static final String URL_SUBSCRIBER_SINGULAR_LOCATOR = "User";
	public static final String URL_SUBSCRIBER_PLURAL_LOCATOR = "Users";
	public static final String URL_POST_SINGULAR_LOCATOR = "Post";
	public static final String URL_POST_PLURAL_LOCATOR = "Posts";
	public static final String URL_FOLLOWSHIP_LOCATOR = "Followship";
	public static final String URL_AUTHORIZATION = "Authorization";

	// @end Resources locators

	// @start configuration attributes
	public static final String CONFIG_PERSISTENCE_TYPE = "org.quantumlabs.cococaca.persistence.type";
	public static final String CONFIG_PERSISTENCE_DB_URL = "org.quantumlabs.cococaca.persistence.db.url";
	public static final String CONFIG_PERSISTENCE_DB_USERNAME = "org.quantumlabs.cococaca.persistence.db.username";
	public static final String CONFIG_PERSISTENCE_DB_PASSWORD = "org.quantumlabs.cococaca.persistence.db.password";
	public static final String CONFIG_PERSISTENCE_DB_POOL_SIZE = "org.quantumlabs.cococaca.persistence.db.pool.size";
	public static final String CONFIG_PERSISTENCE_DB_DRIVER = "org.quantumlabs.cococaca.persistence.db.driver";
	// @end configuration attributes

	public static final String DIR_SYSTEM_PREFERENCES = "resources/cococaca-preference.properties";

	public static int SVLT_PIC_UPLDR_BUFR_SIZE_THRESHOLD = 512; // The file will

	public static final File SVLT_PIC_UPLDR_REPO = null;
	public static final String SVLT_STREAM_PARAM_DESCRIPTION = "description";

	public static final String SVLT_POST_AHTOR_ID = "author";

	public static final long _1K = 1024;
	public static final long _1M = 1024 * _1K;

	public static final long SVLT_PIC_UPLDR_SIZE_MAX = 30 * _1M;

	public static final String FRONT_END_HTML_NEW_POST_FIELD = "img_content";

	public static final String HTTP_SESSION_ATTRIBUTE_SUBSCRIBER_KEY = "_subscriber_key_string";
}
