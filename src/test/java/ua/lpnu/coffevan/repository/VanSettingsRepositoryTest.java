package ua.lpnu.coffevan.repository;

import org.junit.jupiter.api.*;
import ua.lpnu.coffevan.model.Van;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class VanSettingsRepositoryTest {

    private static Connection connection;
    private static VanSettingsRepository dao;

    @BeforeAll
    static void setUpDb() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        String createTable = """
                CREATE TABLE IF NOT EXISTS van_settings (
                    id         INTEGER PRIMARY KEY,
                    max_volume REAL    NOT NULL DEFAULT 1000.0,
                    max_budget REAL    NOT NULL DEFAULT 50000.0
                );
                """;
        connection.createStatement().execute(createTable);
        connection.createStatement().execute(
                "INSERT INTO van_settings (id, max_volume, max_budget) VALUES (1, 1000.0, 50000.0)");
        dao = new VanSettingsRepository(connection);
    }

    @AfterAll
    static void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void load_returnsDefaultSettings() {
        Van van = dao.load();
        assertEquals(1000.0, van.getMaxVolumeLiters(), 0.001);
        assertEquals(50000.0, van.getMaxBudget(), 0.001);
    }

    @Test
    void save_updatesSettings() {
        Van van = new Van(2000.0, 100000.0);
        dao.save(van);

        Van loaded = dao.load();
        assertEquals(2000.0, loaded.getMaxVolumeLiters(), 0.001);
        assertEquals(100000.0, loaded.getMaxBudget(), 0.001);

        dao.save(new Van(1000.0, 50000.0));
    }

    @Test
    void load_whenSqlException_returnsFallbackSettings() throws SQLException {
        Connection mockedConn = org.mockito.Mockito.mock(Connection.class);
        org.mockito.Mockito.when(mockedConn.prepareStatement(org.mockito.Mockito.anyString()))
                .thenThrow(new SQLException("Mocked DB error"));
        VanSettingsRepository exceptionDao = new VanSettingsRepository(mockedConn);

        Van van = exceptionDao.load();
        assertEquals(1000.0, van.getMaxVolumeLiters(), 0.001);
        assertEquals(50000.0, van.getMaxBudget(), 0.001);
    }

    @Test
    void save_whenSqlException_handlesGracefully() throws SQLException {
        Connection mockedConn = org.mockito.Mockito.mock(Connection.class);
        org.mockito.Mockito.when(mockedConn.prepareStatement(org.mockito.Mockito.anyString()))
                .thenThrow(new SQLException("Mocked DB error"));
        VanSettingsRepository exceptionDao = new VanSettingsRepository(mockedConn);

        assertDoesNotThrow(() -> exceptionDao.save(new Van(2000.0, 100000.0)));
    }
}
