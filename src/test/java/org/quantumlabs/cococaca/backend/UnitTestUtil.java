package org.quantumlabs.cococaca.backend;

import static org.quantumlabs.cococaca.backend.service.preference.Parameters.CONFIG_PERSISTENCE_DB_DRIVER;
import static org.quantumlabs.cococaca.backend.service.preference.Parameters.CONFIG_PERSISTENCE_DB_PASSWORD;
import static org.quantumlabs.cococaca.backend.service.preference.Parameters.CONFIG_PERSISTENCE_DB_URL;
import static org.quantumlabs.cococaca.backend.service.preference.Parameters.CONFIG_PERSISTENCE_DB_USERNAME;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.quantumlabs.cococaca.backend.service.persistence.mock.UTPresistenceConfig;
import org.quantumlabs.cococaca.backend.service.preference.Config;

public class UnitTestUtil {
	private static final String DROP_UT_DATABASE_IF_EXISTS = "DROP DATABASE IF EXISTS cococaca";
	private static final Runtime commandRunner = Runtime.getRuntime();
	private static final String _INITIALIZATION_COMMAND = String.format("mysql --user=root --password=\"\" < %s",
			"db\\initialization.sql");
	private static final String _UNINITIALIZATION_COMMAND = String.format("mysql --user=root --password=\"\" < %s",
			"db\\uninitialization.sql");

	private static Config utConfig = new UTPresistenceConfig();

	public static void setupDBEnv() {
		setupSchema();
	}

	private static void setupSchema() {
		executeCommand(_INITIALIZATION_COMMAND);
	}

	public static void tearDownDBEnv() {
		dropDatabase();
	}

	private static void dropDatabase() {
		executeSQL(DROP_UT_DATABASE_IF_EXISTS);
	}

	@Deprecated
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

	public static void executeSQL(String rawSQL) {
		executeDBOperation(rawSQL, false);
	}

	public static ResultSet executeSQLQuery(String rawSQL) {
		return executeDBOperation(rawSQL, true);
	}

	private static ResultSet executeDBOperation(String rawSQL, boolean hasReturnValue) {
		Connection connection = null;

		try {
			connection = getUnitTestDBConnection();
			return doExecuteDBOperation(connection, rawSQL, hasReturnValue);
		} catch (Exception e) {
			throw new RuntimeException("Can't execute SQL", e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// Ignore
				}
			}
		}
	}

	private static ResultSet doExecuteDBOperation(Connection connection, String rawSQL, boolean hasReturnValue)
			throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement(rawSQL)) {
			if (hasReturnValue) {
				return statement.executeQuery(rawSQL);
			} else {
				statement.execute();
				return null;
			}
		}
	}

	private static Connection getUnitTestDBConnection() {
		try {
			Connection connection = DriverManager.getConnection(utConfig.get(CONFIG_PERSISTENCE_DB_URL),
					utConfig.get(CONFIG_PERSISTENCE_DB_USERNAME), utConfig.get(CONFIG_PERSISTENCE_DB_PASSWORD));
			connection.setAutoCommit(true);
			return connection;
		} catch (SQLException e) {
			throw new RuntimeException("Can't get UT DB connection", e);
		}
	}

	static {
		try {
			Class.forName(utConfig.get(CONFIG_PERSISTENCE_DB_DRIVER));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Can't initialize UT DB.", e);
		}
	}
}
