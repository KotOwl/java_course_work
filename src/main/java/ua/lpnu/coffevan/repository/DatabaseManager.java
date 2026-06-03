package ua.lpnu.coffevan.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.lpnu.coffevan.util.SmtpEmailNotifier;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages SQLite database connection and schema initialisation.
 */
public class DatabaseManager {

    private static final Logger logger = LogManager.getLogger(DatabaseManager.class);
    private static String dbUrl = "jdbc:sqlite:coffee_van.db";

    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        try {
            connection = DriverManager.getConnection(dbUrl);
            logger.info("Connected to SQLite database: {}", dbUrl);
            initSchema();
        } catch (SQLException e) {
            logger.fatal("Cannot connect to database", e);
            SmtpEmailNotifier.sendCriticalAlert(
                    "Coffee Van – Fatal DB Error",
                    "Cannot connect to SQLite database '" + dbUrl + "'.\n" + e.getMessage());
            throw new RuntimeException("Cannot connect to database", e);
        }
    }


    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Sets the database URL and resets the singleton instance.
     * Useful for isolating tests with an in-memory database.
     */
    public static synchronized void setDbUrl(String url) {
        dbUrl = url;
        if (instance != null) {
            instance.close();
            instance = null;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    private void initSchema() throws SQLException {
        String createCoffee = """
                CREATE TABLE IF NOT EXISTS coffee (
                    id            INTEGER PRIMARY KEY AUTOINCREMENT,
                    coffee_type   TEXT    NOT NULL,
                    name          TEXT    NOT NULL,
                    price_per_kg  REAL    NOT NULL,
                    weight_kg     REAL    NOT NULL,
                    volume_liters REAL    NOT NULL,
                    packaging     TEXT    NOT NULL,
                    quality_score INTEGER NOT NULL,
                    extra1        TEXT,
                    extra2        TEXT,
                    extra3        TEXT
                );
                """;

        String createVan = """
                CREATE TABLE IF NOT EXISTS van_settings (
                    id            INTEGER PRIMARY KEY,
                    max_volume    REAL    NOT NULL DEFAULT 1000.0,
                    max_budget    REAL    NOT NULL DEFAULT 50000.0
                );
                """;

        String insertVan = """
                INSERT OR IGNORE INTO van_settings (id, max_volume, max_budget)
                VALUES (1, 1000.0, 50000.0);
                """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createCoffee);
            stmt.execute(createVan);
            stmt.execute(insertVan);
            logger.info("Database schema initialised");
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Database connection closed");
            }
        } catch (SQLException e) {
            logger.error("Error closing database connection", e);
        }
    }
}
