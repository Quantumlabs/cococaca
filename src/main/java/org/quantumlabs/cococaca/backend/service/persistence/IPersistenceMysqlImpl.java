package org.quantumlabs.cococaca.backend.service.persistence;

import static org.quantumlabs.cococaca.backend.Helper.assertNotEmtryString;
import static org.quantumlabs.cococaca.backend.Helper.assertTrue;
import static org.quantumlabs.cococaca.backend.service.persistence.model.Gender.FEMALE;
import static org.quantumlabs.cococaca.backend.service.persistence.model.Gender.MALE;
import static org.quantumlabs.cococaca.backend.service.preference.Parameters.CONFIG_PERSISTENCE_DB_DRIVER;
import static org.quantumlabs.cococaca.backend.service.preference.Parameters.CONFIG_PERSISTENCE_DB_PASSWORD;
import static org.quantumlabs.cococaca.backend.service.preference.Parameters.CONFIG_PERSISTENCE_DB_POOL_SIZE;
import static org.quantumlabs.cococaca.backend.service.preference.Parameters.CONFIG_PERSISTENCE_DB_URL;
import static org.quantumlabs.cococaca.backend.service.preference.Parameters.CONFIG_PERSISTENCE_DB_USERNAME;

import java.io.InputStream;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.ArrayList;

import org.quantumlabs.cococaca.backend.Helper;
import org.quantumlabs.cococaca.backend.service.persistence.IPersistenceMysqlImpl.IDatabaseOperation;
import org.quantumlabs.cococaca.backend.service.persistence.model.Gender;
import org.quantumlabs.cococaca.backend.service.persistence.model.IContentKeyImpl;
import org.quantumlabs.cococaca.backend.service.persistence.model.IPostKey;
import org.quantumlabs.cococaca.backend.service.persistence.model.IPostKeyImpl;
import org.quantumlabs.cococaca.backend.service.persistence.model.ISubscriberKey;
import org.quantumlabs.cococaca.backend.service.persistence.model.ISubscriberKeyImpl;
import org.quantumlabs.cococaca.backend.service.persistence.model.Post;
import org.quantumlabs.cococaca.backend.service.persistence.model.Subscriber;
import org.quantumlabs.cococaca.backend.service.preference.Config;
import org.quantumlabs.cococaca.backend.transaction.authorization.Credential;

;

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
		assertNotEmtryString(_MYSQL_DB_URL);
		assertNotEmtryString(_MYSQL_DB_USERNAME);
		assertNotEmtryString(_MYSQL_DB_PASSWORD);
		assertNotEmtryString(_DB_DRIVER_CLASS);
		assertNotEmtryString(poolSizeString);
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
		return performOperation(FETCH_POST_BY_KEY, postKey);
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

	// Nullable
	private static final IDatabaseOperation<IPostKey, Post> FETCH_POST_BY_KEY = (connection, param) -> {
		String sql = "SELECT AUTHOR_ID, CONTENT_ID,DESCRIPTION, DATESTAMP FROM T_POST WHERE ID = ?";
		Post post = null;
		try (PreparedStatement statement = connection.prepareStatement(sql);) {
			statement.setString(1, param.get());
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				post = new Post(param);
				post.setAuthorKey(new ISubscriberKeyImpl(rs.getString(1)));
				post.setContentKey(new IContentKeyImpl(rs.getString(2)));
				post.setDescription(rs.getString(3));
				post.setTimeStamp(Long.valueOf(rs.getString(4)));
			}
			Helper.assertTrue(!rs.next());
		}
		return post;
	};

	private static final IDatabaseOperation<StoreSubscriebrParam, Void> INSERT_SUBSCRIBER = (connection, param) -> {
		String sql = "INSERT INTO T_SUBSCRIBER (NAME,PASSWORD,GENDER,AVATAR_ID) VALUE (?,?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, param.name);
			statement.setString(2, param.password);
			statement.setString(3, param.gender.toString());
			statement.setString(4, param.avatarID);
			statement.execute();
			return null;
		}
	};

	private static final IDatabaseOperation<UpdateSubscriberParam, Void> UPDATE_SUBSCRIBER = (connection, param) -> {
		String sql = "UPDATE T_SUBSCRIBER SET NAME=?, GENDER=?, AVATAR_ID=?  WHERE ID=?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, param.name);
			statement.setString(2, param.gender.toString());
			statement.setString(3, param.avatarID);
			statement.setString(4, param.key.get());
			statement.execute();
			return null;
		}
	};

	private static final IDatabaseOperation<Post, Post> INSERT_POST = (connection, param) -> {
		String sql = "INSERT INTO T_POST (ID, author_id, content_id,description,datestamp) values (?,?,?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, param.getKey().get());
			statement.setString(2, param.getAuthorKey().get());
			statement.setString(3, param.getContentKey().get());
			statement.setString(4, param.getDescription());
			statement.setString(5, Long.valueOf(param.getTimeStamp()).toString());
			statement.execute();
			return null;
		}
	};

	private static final IDatabaseOperation<Object, Post> UPDATE_POST = (connection, param) -> {
		return null;
	};

	@Override
	public void storePost(Post post) {
		performOperation(INSERT_POST, post);
	}

	@Override
	public String write(InputStream inputStream) {
		return performOperation(WRITE_STREAM, inputStream);
	}

	private static final IDatabaseOperation<InputStream, String> WRITE_STREAM = (connection, inputStream) -> {
		String sql = "INSERT INTO T_FILE_STORE ( BIN_DATA) values (?)";
		try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			statement.setBinaryStream(1, inputStream);
			// Auto generated key
			statement.executeUpdate();
			ResultSet keyRs = statement.getGeneratedKeys();
			assertTrue(keyRs.next());
			int generatedKey = keyRs.getInt(1);
			String key = BigInteger.valueOf(generatedKey).toString();
			return key;
		}

	};

	private static class PostFetchingCondition {
		ISubscriberKey subscriberKey;
	}

	private static final IDatabaseOperation<PostFetchingCondition, Post[]> FETCH_LAST_X_SUBSCRIBED_POSTS = (connection,
			condition) -> {
		/*
		 * ID AUTHOR_ID CONTENT_ID DESCRIPTION DATESTAMP
		 */
		String sql = "SELECT F.ID, F.AUTHOR_ID, F.CONTENT_ID, F.DESCRIPTION, F.DATESTAMP FROM (SELECT FOLLOWER_ID, FOLLOWEE_ID FROM T_SUBSCRIBER C JOIN TC_FOLLOWSHIP D  ON C.ID = D.FOLLOWER_ID WHERE ID=?) E JOIN T_POST F ON E.FOLLOWEE_ID = F.AUTHOR_ID ORDER BY DATESTAMP";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, condition.subscriberKey.get());
			ResultSet rs = statement.executeQuery();
			// Probably no result at all.
			List<Post> posts = new ArrayList<>();
			while (rs.next()) {
				Post post = new Post(new IPostKeyImpl(rs.getString(1)));
				post.setAuthorKey(new ISubscriberKeyImpl(rs.getString(2)));
				post.setContentKey(new IContentKeyImpl(rs.getString(3)));
				post.setDescription(rs.getString(4));
				// post.setTimeStamp(Long.valueOf(rs.getString(5)));
				posts.add(post);
			}
			return posts.toArray(new Post[posts.size()]);
		}
	};

	@Override
	public Post[] fetchPostsForSubscriber(ISubscriberKey subscriberKey) {
		PostFetchingCondition condition = new PostFetchingCondition();
		condition.subscriberKey = subscriberKey;
		return performOperation(FETCH_LAST_X_SUBSCRIBED_POSTS, condition);
	}

	static final IDatabaseOperation<Credential, ISubscriberKey> AUTHORIZE_USER = (connection, credential) -> {
		String sql = "SELECT ID FROM T_SUBSCRIBER WHERE NAME=? AND PASSWORD=?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, credential.getUserName());
			statement.setString(2, credential.getPassword());
			ResultSet rs = statement.executeQuery();
			ISubscriberKey key = null;
			if (rs.next()) {
				String ID = rs.getString(1);
				key = new ISubscriberKeyImpl(ID);
			}
			Helper.assertTrue(!rs.next());
			return key;
		}
	};

	@Override
	public ISubscriberKey authorize(Credential credential) {
		return performOperation(AUTHORIZE_USER, credential);
	}

	private static IDatabaseOperation<String, Boolean> IS_SUBSCRIBER_EXISTING = (connection, userName) -> {
		String sql = "SELECT ID FROM T_SUBSCRIBER WHERE NAME=?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, userName);
			ResultSet rs = statement.executeQuery();
			boolean existing = rs.next();
			Helper.assertTrue(String.format("User name %s should not be duplicated in DB", userName), !rs.next());
			return existing;
		}
	};

	@Override
	public boolean isSubscriberExisting(String userName) {
		return performOperation(IS_SUBSCRIBER_EXISTING, userName);
	}
}
