package org.quantumlabs.cococaca.backend;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class UnitTestUtil {
	private static final Runtime commandRunner = Runtime.getRuntime();
	private static final String _INITIALIZATION_COMMAND = String.format("mysql --user=root --password=\"\" < %s",
			"db\\initialization.sql");
	private static final String _UNINITIALIZATION_COMMAND = String.format("mysql --user=root --password=\"\" < %s",
			"db\\uninitialization.sql");

	public static void setupDBForUnitTest() {
		setupSchema();
	}

	private static void setupSchema() {
		executeCommand(_INITIALIZATION_COMMAND);
	}

	public static void teardownDBForUnitTest() {
		teardownSchema();
	}

	private static void teardownSchema() {
		executeCommand(_UNINITIALIZATION_COMMAND);
	}

	private static void executeCommand(String command) {
		try {
			Process process = commandRunner.exec(command);
			boolean success = process.waitFor(5, TimeUnit.SECONDS);
			if (!success) {
				throw new RuntimeException(String.format("Time out for executing command %s", command));
			}
			int returnCode = process.exitValue();
			if (returnCode != 0) {
				throw new RuntimeException(String.format("Return code %s by executing command %S", returnCode, command));
			}
		} catch (InterruptedException | IOException e) {
			throw new RuntimeException(String.format("Failed to execute command %s", command), e);
		}
	}

	public static void clearAllTables() {
		
	}
}
