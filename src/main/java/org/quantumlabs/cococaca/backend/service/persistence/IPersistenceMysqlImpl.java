package org.quantumlabs.cococaca.backend.service.persistence;

import static org.quantumlabs.cococaca.backend.Helper.assertTrue;
import static org.quantumlabs.cococaca.backend.Helper.isNotEmptyString;
import static org.quantumlabs.cococaca.backend.service.persistence.model.Gender.FEMALE;
import static org.quantumlabs.cococaca.backend.service.persistence.model.Gender.MALE;
import static org.quantumlabs.cococaca.backend.service.preference.Parameters.CONFIG_PERSISTENCE_DB_DRIVER;
import static org.quantumlabs.cococaca.backend.service.preference.Parameters.CONFIG_PERSISTENCE_DB_PASSWORD;
import static org.quantumlabs.cococaca.backend.service.preference.Parameters.CONFIG_PERSISTENCE_DB_POOL_SIZE;
import static org.quantumlabs.cococaca.backend.service.preference.Parameters.CONFIG_PERSISTENCE_DB_URL;
import static org.quantumlabs.cococaca.backend.service.preference.Parameters.CONFIG_PERSISTENCE_DB_USERNAME;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.quantumlabs.cococaca.backend.Helper;
import org.quantumlabs.cococaca.backend.service.persistence.model.Gender;
import org.quantumlabs.cococaca.backend.service.persistence.model.IPostKey;
import org.quantumlabs.cococaca.backend.service.persistence.model.ISubscriberKey;
import org.quantumlabs.cococaca.backend.service.persistence.model.Post;
import org.quantumlabs.cococaca.backend.service.persistence.model.Subscriber;
import org.quantumlabs.cococaca.backend.service.preference.Config;

public class IPersistenceMysqlImpl implements IPersistence {
	private String _MYSQL_DB_URL;
	private String _MYSQL_DB_USERNAME;
	private String _MYSQL_DB_PASSWORD;
	private String _DB_DRIVER_CLASS;
	private int _MYSQL_DB_CONNECTION_POOL_SIZE;
	private boolean started;
	private ConnectionPool pool;

	public IPersistenceMysqlImpl(final Config config) {
		String poolSizeString = config.get(CONFIG_PERSISTENCE_DB_POOL_SIZE);
		_MYSQL_DB_URL = config.get(CONFIG_PERSISTENCE_DB_URL);
		_MYSQL_DB_USERNAME = config.get(CONFIG_PERSISTENCE_DB_USERNAME);
		_MYSQL_DB_PASSWORD = config.get(CONFIG_PERSISTENCE_DB_PASSWORD);
		_DB_DRIVER_CLASS = config.get(CONFIG_PERSISTENCE_DB_DRIVER);
		isNotEmptyString(_MYSQL_DB_URL);
		isNotEmptyString(_MYSQL_DB_USERNAME);
		isNotEmptyString(_MYSQL_DB_PASSWORD);
		isNotEmptyString(_DB_DRIVER_CLASS);
		isNotEmptyString(poolSizeString);
		_MYSQL_DB_CONNECTION_POOL_SIZE = Integer.valueOf(poolSizeString);
		prepareConnectionPool();
	}

	static class FetchSubscriberParam {
		final ISubscriberKey subsKey;

		FetchSubscriberParam(ISubscriberKey subsKey) {
			this.subsKey = subsKey;
		}
	}

	@Override
	public Subscriber fetchSubscriber(ISubscriberKey subscriberKey) {
		FetchSubscriberParam param = new FetchSubscriberParam(subscriberKey);
		return performOperation(FETCH_SUBSCRIBER_BY_KEY, param);
	}

	@Override
	public Post fetchPost(IPostKey postKey) {
		return null;
	}

	private void handleEx(Exception e) {
		// TODO logging and so on
	}

	@Override
	public synchronized void start() {
		doStart();
		started = true;
	}

	// Any error happens in initialization should be considered as a
	// runtime-none-recoverable fatal.
	private void doStart() {
		prepareConnectionPool();
	}

	private void prepareConnectionPool() {
		pool = new ConnectionPool(_MYSQL_DB_URL, _MYSQL_DB_USERNAME, _MYSQL_DB_PASSWORD, _DB_DRIVER_CLASS,
				_MYSQL_DB_CONNECTION_POOL_SIZE);
	}

	@Override
	public synchronized void stop() {
		started = false;
		doStop();
	}

	private void doStop() {
		pool.releaseAll();
	}

	@Override
	public synchronized boolean isStarted() {
		return started;
	}

	static class ConnectionPool {
		private int size;
		private Driver driver;
		// Unbounded, higher throughput, less predictable than
		// ArrayBlockingQueue
		// in concurrent runtime.
		private BlockingQueue<Connection> connections = new LinkedBlockingQueue<>();
		private IDBTimeoutPolicy timeoutPolicy = _DEFAULT_DB_TIMEOUT_POLICY;
		private final static String _MANDATORY_PROPERTY_USERNAME = "user";
		private final static String _MANDATORY_PROPERTY_PASSWORD = "password";
		private AtomicInteger connectionsOutSide = new AtomicInteger(0);

		ConnectionPool(final String URL, final String userName, final String pw, final String driverClass, int size) {
			this.size = size;
			Properties properties = new Properties();
			properties.setProperty(_MANDATORY_PROPERTY_USERNAME, userName);
			properties.setProperty(_MANDATORY_PROPERTY_PASSWORD, pw);
			try {
				driver = (Driver) Class.forName(driverClass).newInstance();
				DriverManager.registerDriver(driver);
				createConnections(URL, properties);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
				throw new RuntimeException(e);
			}
		}

		public void releaseAll() {
			doReleaseAll();
			waitForConnectionsOutside();
		}

		private void doReleaseAll() {
			for (Connection connection : connections) {
				try {
					connection.close();
				} catch (SQLException e) {
					Helper.logError(e);
				}
			}
		}

		private void waitForConnectionsOutside() {
			if (connectionsOutSide.get() > 0) {
				synchronized (connectionsOutSide) {
					try {
						connectionsOutSide.wait(TimeUnit.SECONDS.toMillis(5));
					} catch (InterruptedException e) {
						// Ignore
					}
				}
			}
			if (connectionsOutSide.get() > 0) {
				doReleaseAll();
				throw new RuntimeException(String.format("Still %s connections are outside.", connectionsOutSide.get()));
			} else {
				doReleaseAll();
			}
		}

		private void createConnections(String url, Properties properties) throws SQLException {
			for (int i = 0; i < getSize(); i++) {
				connections.add(driver.connect(url, properties));
			}
		}

		public int getSize() {
			return size;
		}

		public Connection checkout() {
			try {
				Connection connection = connections.poll(timeoutPolicy.checkoutTimeout(), timeoutPolicy.timeUnit());
				connectionsOutSide.incrementAndGet();
				return connection;
			} catch (InterruptedException e) {
				throw new RuntimeException("Should not be interrupted while checking out Connection", e);
			}
		}

		public void checkin(Connection connection) {
			try {
				connections.offer(connection, timeoutPolicy.checkinTimeout(), timeoutPolicy.timeUnit());
				connectionsOutSide.decrementAndGet();
			} catch (InterruptedException e) {
				throw new RuntimeException("Should not be interrupted while checking in Connection", e);
			}
		}
	}

	// A measurement for DB based persistence performance.
	static interface IDBTimeoutPolicy {
		int checkoutTimeout();

		TimeUnit timeUnit();

		int checkinTimeout();
	}

	static final IDBTimeoutPolicy _DEFAULT_DB_TIMEOUT_POLICY = new IDBTimeoutPolicy() {
		@Override
		public int checkoutTimeout() {
			return 1;
		}

		@Override
		public TimeUnit timeUnit() {
			return TimeUnit.SECONDS;
		}

		@Override
		public int checkinTimeout() {
			return 1;
		}
	};

	/**
	 * An IDatabaseOperation stands for what database should operate, it
	 * consists of the parameters and result of the operation.<br>
	 * It de-couples database transaction staff and the business level
	 * operation.
	 */
	interface IDatabaseOperation<P, R> {
		R perform(Connection connection, P p) throws Exception;
	}

	private <P, R> R performOperation(IDatabaseOperation<P, R> op, P parameter) {
		Connection connection = null;
		try {
			connection = pool.checkout();
			return op.perform(connection, parameter);
		} catch (Exception e) {
			handleEx(e);
			// After exception handling in persistence layer, rethrow to upper
			// layer?
			// Runtime or not runtime exception?
			throw new RuntimeException(e);
		} finally {
			if (connection != null) {
				pool.checkin(connection);
			}
		}
	}

	private static final IDatabaseOperation<FetchSubscriberParam, Subscriber> FETCH_SUBSCRIBER_BY_KEY = (connection,
			subscriberParam) -> {
		String sql = "SELECT NAME,GENDER,AVATAR_ID FROM T_SUBSCRIBER WHERE ID = ?";
		Subscriber subscriber = null;
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, subscriberParam.subsKey.get());
			ResultSet rs = statement.executeQuery();
			// Should be one item selected.
			assertTrue(rs.next());
			subscriber = new Subscriber(subscriberParam.subsKey);
			subscriber.setName(rs.getString(1));
			subscriber.setGender(rs.getString(2).equals(MALE.toString()) ? MALE : FEMALE);
			subscriber.setAvatarID(rs.getString(3));
			// Should no more item selected.
			assertTrue(!rs.next());
			return subscriber;
		}
	};

	static class UpdateSubscriberParam {
		final ISubscriberKey key;
		final String name;

		final Gender gender;
		final String avatarID;

		UpdateSubscriberParam(ISubscriberKey key, String name, Gender gender, String avatarID) {
			this.key = key;
			this.name = name;
			this.gender = gender;
			this.avatarID = avatarID;
		}
	}

	@Override
	public void updateSubscriber(Subscriber subscriber) {
		UpdateSubscriberParam param = new UpdateSubscriberParam(subscriber.getKey(), subscriber.getName(),
				subscriber.getGender(), subscriber.getAvatarID());
		performOperation(UPDATE_SUBSCRIBER, param);
	}

	static class StoreSubscriebrParam extends UpdateSubscriberParam {
		final String password;

		StoreSubscriebrParam(ISubscriberKey key, String name, String password, Gender gender, String avatarID) {
			super(key, name, gender, avatarID);
			this.password = password;
		}
	}

	@Override
	public void storeSubscriber(Subscriber subscriber, String password) {
		StoreSubscriebrParam param = new StoreSubscriebrParam(subscriber.getKey(), subscriber.getName(), password,
				subscriber.getGender(), subscriber.getAvatarID());
		performOperation(INSERT_SUBSCRIBER, param);
	}

	private static final IDatabaseOperation<Object, Post> FETCH_POST_BY_KEY = (connection, param) -> {
		return null;
	};

	private static final IDatabaseOperation<StoreSubscriebrParam, Void> INSERT_SUBSCRIBER = (connection, param) -> {
		String sql = "INSERT INTO T_SUBSCRIBER (ID,NAME,PASSWORD,GENDER,AVATAR_ID) VALUE (?,?,?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, param.key.get());
			statement.setString(2, param.name);
			statement.setString(3, param.password);
			statement.setString(4, param.gender.toString());
			statement.setString(5, param.avatarID);
			statement.execute();
			return null;
		}
	};

	private static final IDatabaseOperation<UpdateSubscriberParam, Void> UPDATE_SUBSCRIBER = (connection, param) -> {
		String sql = "UPDATE T_SUBSCRIBER(ID,NAME,GENDER,AVATAR_ID) VALUES (?,?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, param.key.get());
			statement.setString(2, param.name);
			statement.setString(3, param.gender.toString());
			statement.setString(4, param.avatarID);
			statement.execute();
			return null;
		}
	};

	private static final IDatabaseOperation<Object, Post> INSERT_POST = (connection, param) -> {
		return null;
	};

	private static final IDatabaseOperation<Object, Post> UPDATE_POST = (connection, param) -> {
		return null;
	};
}
