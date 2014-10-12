package org.quantumlabs.cococaca.backend.service.persistence;

import org.quantumlabs.cococaca.backend.service.persistence.model.IPostKey;
import org.quantumlabs.cococaca.backend.service.persistence.model.ISubscriberKey;
import org.quantumlabs.cococaca.backend.service.persistence.model.Post;
import org.quantumlabs.cococaca.backend.service.persistence.model.Subscriber;
import org.quantumlabs.cococaca.backend.service.preference.Config;

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

import static org.quantumlabs.cococaca.backend.service.preference.Parameters.CONFIG_PERSISTENCE_DB_DRIVER;
import static org.quantumlabs.cococaca.backend.service.preference.Parameters.CONFIG_PERSISTENCE_DB_PASSWORD;
import static org.quantumlabs.cococaca.backend.service.preference.Parameters.CONFIG_PERSISTENCE_DB_POOL_SIZE;
import static org.quantumlabs.cococaca.backend.service.preference.Parameters.CONFIG_PERSISTENCE_DB_URL;
import static org.quantumlabs.cococaca.backend.service.preference.Parameters.CONFIG_PERSISTENCE_DB_USERNAME;

public class IPersistenceMysqlImpl implements IPersistence {
    private String _MYSQL_DB_URL;
    private String _MYSQL_DB_USERNAME;
    private String _MYSQL_DB_PASSWORD;
    private String _DB_DRIVER_CLASS;
    private int _MYSQL_DB_CONNECTION_POOL_SIZE;
    private boolean started;
    private ConnectionPool pool;

    public IPersistenceMysqlImpl(final Config config) {
        _MYSQL_DB_CONNECTION_POOL_SIZE = Integer.valueOf(config.get(CONFIG_PERSISTENCE_DB_POOL_SIZE));
        _MYSQL_DB_URL = config.get(CONFIG_PERSISTENCE_DB_URL);
        _MYSQL_DB_USERNAME = config.get(CONFIG_PERSISTENCE_DB_USERNAME);
        _MYSQL_DB_PASSWORD = config.get(CONFIG_PERSISTENCE_DB_PASSWORD);
        _DB_DRIVER_CLASS = config.get(CONFIG_PERSISTENCE_DB_DRIVER);
        prepareConnectionPool();
    }

    static class SubscriberParam {
        private final String dbKey;

        SubscriberParam(String dbKey) {
            this.dbKey = dbKey;
        }

        String getDBRowKey() {
            return dbKey;
        }
    }

    @Override
    public Subscriber fetchSubscriber(ISubscriberKey subscriberKey) {
        SubscriberParam param = new SubscriberParam(subscriberKey.get());
        return performOperation(FETCH_SUBSCRIBER_BY_KEY, param);
    }

    @Override
    public Post fetchPost(IPostKey postKey) {
        return null;
    }

    private void handleEx(Exception e) {
        //TODO logging and so on
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
        pool = new ConnectionPool(_MYSQL_DB_URL, _MYSQL_DB_USERNAME, _MYSQL_DB_PASSWORD, _DB_DRIVER_CLASS, _MYSQL_DB_CONNECTION_POOL_SIZE);
    }

    @Override
    public synchronized void stop() {
        started = false;
        doStop();
    }

    private void doStop() {
        //TODO db stop staffs
    }

    @Override
    public synchronized boolean isStarted() {
        return started;
    }

    static class ConnectionPool {
        private int size;
        private Driver driver;
        //Unbounded, higher throughput, less predicable than ArrayBlockingQueue  in concurrent runtime.
        private BlockingQueue<Connection> connections = new LinkedBlockingQueue<>();
        private IDBTimeoutPolicy timeoutPolicy = _DEFAULT_DB_TIMEOUT_POLICY;
        private final static String _MANDATORY_PROPERTY_USERNAME = "username";
        private final static String _MANDATORY_PROPERTY_PASSWORD = "password";

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
                return connections.poll(timeoutPolicy.checkoutTimeout(), timeoutPolicy.timeUnit());
            } catch (InterruptedException e) {
                throw new RuntimeException("Should not be interrupted while checking out Connection", e);
            }
        }

        public void checkin(Connection connection) {
            try {
                connections.offer(connection, timeoutPolicy.checkinTimeout(), timeoutPolicy.timeUnit());
            } catch (InterruptedException e) {
                throw new RuntimeException("Should not be interrupted while checking in Connection", e);
            }
        }
    }

    //A measurement for DB based persistence performance.
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
     * An IDatabaseOperation stands for what database should operate, it consists of the parameters and
     * result of the operation.<br>
     * It de-couples database transaction staff and the business level operation.
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
            //After exception handling in persistence layer, rethrow to upper layer?
            //Runtime or not runtime exception?
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                pool.checkin(connection);
            }
        }
    }

    private static final IDatabaseOperation<SubscriberParam, Subscriber> FETCH_SUBSCRIBER_BY_KEY = (connection, subscriberParam) -> {
        Subscriber subscriber = null;
        PreparedStatement statement = connection.prepareStatement("");
        //set parameters to statement
        ResultSet rs = statement.executeQuery();
        //extract rs to subscriber;
        return subscriber;
    };

    private static final IDatabaseOperation<Object, Post> FETCH_POST_BY_KEY = (connection, param) -> {
        return null;
    };

    private static final IDatabaseOperation<Object, Post> INSERT_SUBSCRIBER = (connection, param) -> {
        return null;
    };

    private static final IDatabaseOperation<Object, Post> UPDATE_SUBSCRIBER = (connection, param) -> {
        return null;
    };

    private static final IDatabaseOperation<Object, Post> INSERT_POST = (connection, param) -> {
        return null;
    };

    private static final IDatabaseOperation<Object, Post> UPDATE_POST = (connection, param) -> {
        return null;
    };
}
