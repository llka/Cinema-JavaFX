package kate.cinema.backend.database;


import kate.cinema.backend.exception.BackendException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.MissingResourceException;
import java.util.ResourceBundle;


class DataBaseInitializer {
    private static final Logger logger = LogManager.getLogger(DataBaseInitializer.class);

    final String DRIVER;
    final String URL;
    final String LOGIN;
    final int POOL_SIZE;
    private int poolSize;
    final static int DEFAULT_POOL_SIZE = 3;
    final String PASSWORD;

    DataBaseInitializer() {
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("properties.database");
            DRIVER = resourceBundle.getString("db.driver");
            URL = resourceBundle.getString("db.url");
            LOGIN = resourceBundle.getString("db.login");
            poolSize = Integer.valueOf(resourceBundle.getString("db.poolsize"));
            if (poolSize > 0) {
                POOL_SIZE = poolSize;
            } else {
                logger.info("Pool size is less then 1.");
                POOL_SIZE = DEFAULT_POOL_SIZE;
                logger.info("Invalid pool size parameters, now pool size is " + POOL_SIZE);
            }
            PASSWORD = resourceBundle.getString("db.password");
        } catch (NumberFormatException | MissingResourceException e) {
            logger.fatal("Cannot initialize the database connection. " + e);
            throw new BackendException("Cannot initialize the database connection. " + e);
        }
    }
}
