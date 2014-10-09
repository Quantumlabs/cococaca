package org.quantumlabs.cococaca.backend.service.persistence;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Set;

import org.quantumlabs.cococaca.backend.service.persistence.model.ISubscriberKey;
import org.quantumlabs.cococaca.backend.service.persistence.model.Subscriber;

public class IPersistenceMysqlImpl implements IPersistence {
	final static String _MYSQL_DB_DRIVER = "com.mysql.jdbc.Driver";
	final static String _MYSQL_DB_URL = "";
	final static int _MYSQL_DB_CONNECTION_POOL_SIZE = 1;
	private boolean started;
	private ConnectionPool pool;

	@Override
	public Subscriber fetchSubscriber(ISubscriberKey subscriberKey) {
		return null;
	}

	@Override
	public synchronized void start() {
		doStart();
		started = true;
	}

	// Any error happens in initialization should be considered as a
	// runtime-none-recoverable fatal.
	private void doStart() {
		registryDriver();
		prepareConnectionPool();
	}

	private void prepareConnectionPool() {
		try {
			Driver mysqlDriver = (Driver) Class.forName(_MYSQL_DB_DRIVER).newInstance();
			pool = new ConnectionPool(mysqlDriver, _MYSQL_DB_CONNECTION_POOL_SIZE);
			pool.initialize();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private void registryDriver() {
		// try {
		// ;
		// } catch (ClassNotFoundException e) {
		// throw new RuntimeException(e);
		// }
	}

	@Override
	public synchronized void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized boolean isStarted() {
		return started;
	}

	class ConnectionPool {
		private int size;
		private Driver driver;
		private Set<Connection> connections;

		ConnectionPool(Driver driver, int size) {
			this.driver = driver;
			this.size = size;
		}

		public void initialize() {
			try {
				for (int i = 0; i < size; i++) {
					connections.add(driver.connect("", null));
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		public int size() {
			return size;
		}

		public Connection checkout() {
			return null;
		}

		public void checkin(Connection connection) {

		}
	}
}
