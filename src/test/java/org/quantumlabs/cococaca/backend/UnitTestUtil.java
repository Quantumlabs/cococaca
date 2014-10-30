package org.quantumlabs.cococaca.backend;

import static org.quantumlabs.cococaca.backend.service.preference.Parameters.CONFIG_PERSISTENCE_DB_DRIVER;
import static org.quantumlabs.cococaca.backend.service.preference.Parameters.CONFIG_PERSISTENCE_DB_PASSWORD;
import static org.quantumlabs.cococaca.backend.service.preference.Parameters.CONFIG_PERSISTENCE_DB_URL;
import static org.quantumlabs.cococaca.backend.service.preference.Parameters.CONFIG_PERSISTENCE_DB_USERNAME;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

import org.hsqldb.cmdline.SqlFile;
import org.quantumlabs.cococaca.backend.service.persistence.mock.UTPresistenceConfig;
import org.quantumlabs.cococaca.backend.service.preference.Config;

public class UnitTestUtil {
	private static final String workingDir = System.getProperty("user.dir");
	private static final String testResourcesDir = workingDir + "/src/resources";
	private static final String DB_SCHEMA_INIT_SQL_FILE_PATH = testResourcesDir + "/db/initialization.sql";
	private static final String DB_SCHEMA_UNINIT_SQL_FILE_PATH = testResourcesDir + "/db/uninitialization.sql";
	private static final String DROP_UT_DATABASE_IF_EXISTS = "DROP DATABASE IF EXISTS cococaca";

	private static Config utConfig = new UTPresistenceConfig();

	public static void setupDBEnv() {
		try {
			registerDriver();
			setupSchema();
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Can't initialize DB", e);
		}
	}

	private static void registerDriver() throws ClassNotFoundException {
		Class.forName(utConfig.get(CONFIG_PERSISTENCE_DB_DRIVER));
	}

	private static void setupSchema() {
		executeSQLFromFile(new File(DB_SCHEMA_INIT_SQL_FILE_PATH));
	}

	public static void tearDownDBEnv() {
		executeSQLFromFile(new File(DB_SCHEMA_UNINIT_SQL_FILE_PATH));
	}

	private static void dropDatabase() {
		executeSQL(DROP_UT_DATABASE_IF_EXISTS);
	}

	public static void executeSQLFromFile(File file) {
		executeDBOperation((connection) -> {
			try {
				SqlFile sqlFile = new SqlFile(file);
				sqlFile.setConnection(connection);
				sqlFile.execute();
				return null;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	public static void executeSQL(String rawSQL) {
		executeDBOperation((connection) -> {
			try (PreparedStatement statement = connection.prepareStatement(rawSQL)) {
				statement.execute(rawSQL);
				return null;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	public static ResultSet executeSQLQuery(String rawSQL) {
		return executeDBOperation((connection) -> {
			try (PreparedStatement statement = connection.prepareStatement(rawSQL)) {
				return statement.executeQuery(rawSQL);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	private static <R> R executeDBOperation(Function<Connection, R> f) {
		Connection connection = null;
		try {
			connection = getUnitTestDBConnection();
			return f.apply(connection);
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

	/**
	 * Only t_subscriber gets clear
	 * */
	public static void clearAllTables() {
		executeSQL("truncate t_subscriber;");
	}
}
