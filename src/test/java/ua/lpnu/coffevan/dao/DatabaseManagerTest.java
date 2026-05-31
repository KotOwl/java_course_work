package ua.lpnu.coffevan.dao;

import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DatabaseManager.
 *
 * <p>Because DatabaseManager is a JVM-scoped singleton backed by a real
 * SQLite file, we test it in isolation by verifying that the shared
 * instance can provide a live, valid connection with the expected schema
 * already initialised.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseManagerTest {

    private static DatabaseManager manager;

    @BeforeAll
    static void acquireInstance() {
        // Always returns the same singleton
        manager = DatabaseManager.getInstance();
    }

    @Test
    @Order(1)
    void getInstance_returnsNonNull() {
        assertNotNull(manager);
    }

    @Test
    @Order(2)
    void getInstance_returnsSameInstance() {
        DatabaseManager second = DatabaseManager.getInstance();
        assertSame(manager, second, "DatabaseManager must be a singleton");
    }

    @Test
    @Order(3)
    void getConnection_isNotNull() throws SQLException {
        Connection conn = manager.getConnection();
        assertNotNull(conn);
    }

    @Test
    @Order(4)
    void getConnection_isOpen() throws SQLException {
        Connection conn = manager.getConnection();
        assertFalse(conn.isClosed(), "Connection should be open after getInstance()");
    }

    @Test
    @Order(5)
    void schema_coffeeTableExists() throws SQLException {
        Connection conn = manager.getConnection();
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, "coffee", new String[]{"TABLE"})) {
            assertTrue(rs.next(), "'coffee' table should exist after schema init");
        }
    }

    @Test
    @Order(6)
    void schema_vanSettingsTableExists() throws SQLException {
        Connection conn = manager.getConnection();
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, "van_settings", new String[]{"TABLE"})) {
            assertTrue(rs.next(), "'van_settings' table should exist after schema init");
        }
    }

    @Test
    @Order(7)
    void schema_vanSettingsHasDefaultRow() throws SQLException {
        Connection conn = manager.getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM van_settings WHERE id = 1")) {
            assertTrue(rs.next());
            assertEquals(1, rs.getInt(1), "Default van_settings row must be pre-seeded");
        }
    }

    @Test
    @Order(8)
    void schema_coffeeTableHasExpectedColumns() throws SQLException {
        Connection conn = manager.getConnection();
        DatabaseMetaData meta = conn.getMetaData();
        // Collect column names
        java.util.Set<String> cols = new java.util.HashSet<>();
        try (ResultSet rs = meta.getColumns(null, null, "coffee", null)) {
            while (rs.next()) {
                cols.add(rs.getString("COLUMN_NAME").toLowerCase());
            }
        }
        assertTrue(cols.contains("id"),            "Column 'id' must exist");
        assertTrue(cols.contains("coffee_type"),   "Column 'coffee_type' must exist");
        assertTrue(cols.contains("name"),           "Column 'name' must exist");
        assertTrue(cols.contains("price_per_kg"),  "Column 'price_per_kg' must exist");
        assertTrue(cols.contains("weight_kg"),     "Column 'weight_kg' must exist");
        assertTrue(cols.contains("volume_liters"), "Column 'volume_liters' must exist");
        assertTrue(cols.contains("packaging"),     "Column 'packaging' must exist");
        assertTrue(cols.contains("quality_score"), "Column 'quality_score' must exist");
        assertTrue(cols.contains("extra1"),        "Column 'extra1' must exist");
    }
}
