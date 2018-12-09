package kate.cinema.backend.database;

import kate.cinema.backend.exception.ApplicationException;
import kate.cinema.backend.exception.BackendException;
import kate.cinema.entity.enums.ResponseStatus;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class ConnectionPool {
    private static final Logger logger = LogManager.getLogger(ConnectionPool.class);

    private static AtomicBoolean poolInstanceCreated = new AtomicBoolean(false);
    private static AtomicBoolean poolClosed = new AtomicBoolean(false);
    private static Lock connectionPoolLock = new ReentrantLock(true);
    private static Lock closePoolLock = new ReentrantLock(true);
    private static ConnectionPool poolInstance;
    private static int connectionAmount;

    private BlockingQueue<ProxyConnection> freeConnections;
    private BlockingQueue<ProxyConnection> takenConnections;

    private ConnectionPool() {
        DataBaseInitializer dbInitializer = new DataBaseInitializer();

        freeConnections = new ArrayBlockingQueue<>(dbInitializer.POOL_SIZE);
        takenConnections = new ArrayBlockingQueue<>(dbInitializer.POOL_SIZE);

        try {
            DriverManager.registerDriver((Driver) Class.forName(dbInitializer.DRIVER).newInstance());
        } catch (SQLException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            logger.fatal("Registration database driver error" + e);
            throw new BackendException("Error while initializing Mysql Driver " + e);
        }
        for (int i = 0; i < dbInitializer.POOL_SIZE; i++) {
            try {
                Connection connection = DriverManager.getConnection(dbInitializer.URL,
                        dbInitializer.LOGIN,
                        dbInitializer.PASSWORD);
                freeConnections.put(new ProxyConnection(connection));
                ++connectionAmount;
            } catch (SQLException | InterruptedException e) {
                logger.error("Can't get connection " + e);
            }

        }

        if (connectionAmount < 1) {
            logger.fatal("Can't initialize connections. No connections are available.");
            throw new BackendException("Error while initializing connections. No connections are available.");
        }
        logger.info("init " + connectionAmount + " connections where taken.");

        /* check if all connections were initialized */
        if (connectionAmount < dbInitializer.POOL_SIZE) {
            logger.error("Can't initialize expected amount of connections.");
            try {
                while (connectionAmount < dbInitializer.POOL_SIZE) {
                    Connection connection = DriverManager.getConnection(dbInitializer.URL, dbInitializer.LOGIN, dbInitializer.PASSWORD);
                    freeConnections.put(new ProxyConnection(connection));
                    ++connectionAmount;
                }
            } catch (SQLException | InterruptedException e) {
                logger.error("Can't get connection " + e);
            }
            logger.info("init " + connectionAmount + " connections where taken additionally.");
        }
    }

    public static ConnectionPool getInstance() {
        if (!poolInstanceCreated.get()) {
            connectionPoolLock.lock();
            try {
                if (poolInstance == null) {
                    poolInstance = new ConnectionPool();
                    poolInstanceCreated.set(true);
                }
            } finally {
                connectionPoolLock.unlock();
            }
        }
        return poolInstance;
    }

    public Connection getConnection() {
        ProxyConnection connection = null;
        if (!poolClosed.get()) {
            try {
                connection = freeConnections.take();
                takenConnections.put(connection);
            } catch (InterruptedException e) {
                logger.error("Can't take connection from available connections." + e);
            }
        }
        return connection;
    }

    void freeConnection(ProxyConnection connection) {
        if (!poolClosed.get()) {
            takenConnections.remove(connection);
            try {
                freeConnections.put(connection);
            } catch (InterruptedException e) {
                logger.error("Can't free connection " + e);
            }
        }
    }

    public void closePool() throws ApplicationException {
        if (!poolClosed.get()) {
            closePoolLock.lock();
            try {
                poolClosed.set(true);
                for (int i = 0; i < connectionAmount; ++i) {
                    freeConnections.take().finalClose();
                }
            } catch (SQLException | InterruptedException e) {
                logger.error("Can't close connection pool." + e);
            } finally {
                closePoolLock.unlock();
            }
        } else {
            throw new ApplicationException("Other thread has already called closePool()", ResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
