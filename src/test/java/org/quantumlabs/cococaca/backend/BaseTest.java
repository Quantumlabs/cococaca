package org.quantumlabs.cococaca.backend;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.IOException;
import java.util.Properties;

import static org.quantumlabs.cococaca.backend.service.preference.Parameters.DIR_SYSTEM_PREFERENCES;

/**
 * Setting up testing environment staffs. e.g. DB based testing staffs.
 */
public class BaseTest {
    @BeforeClass
    public static void beforeClass() throws IOException {
        setupDBENV();
    }

    private static void setupDBENV() throws IOException {
        Properties testConfig = new Properties();
        testConfig.load(ClassLoader.getSystemResource(DIR_SYSTEM_PREFERENCES).openStream());

    }

    @AfterClass
    public static void afterClass() {

    }
}
