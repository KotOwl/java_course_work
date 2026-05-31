package ua.lpnu.coffevan.dao;

import org.junit.jupiter.api.*;
import ua.lpnu.coffevan.model.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CoffeeDaoImplTest {

    private static Connection connection;
    private static CoffeeDaoImpl dao;

    @BeforeAll
    static void setUpDb() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        String createTable = """
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
        connection.createStatement().execute(createTable);
        dao = new CoffeeDaoImpl(connection);
    }

    @AfterAll
    static void tearDown() throws SQLException {
        connection.close();
    }

    @BeforeEach
    void clearTable() {
        dao.deleteAll();
    }

    @Test
    @Order(1)
    void save_beanCoffee_returnsValidId() {
        BeanCoffee bc = new BeanCoffee("Arabica", 300, 1.0, 1.5, PackagingType.PACKAGE, 90, "Ethiopia", "Medium");
        int id = dao.save(bc);
        assertTrue(id > 0);
        assertEquals(id, bc.getId());
    }

    @Test
    @Order(2)
    void save_groundCoffee_canBeFoundById() {
        GroundCoffee gc = new GroundCoffee("Espresso", 250, 0.5, 0.6, PackagingType.PACKAGE, 80, "Fine");
        int id = dao.save(gc);
        Optional<Coffee> found = dao.findById(id);
        assertTrue(found.isPresent());
        assertEquals("Espresso", found.get().getName());
        assertInstanceOf(GroundCoffee.class, found.get());
    }

    @Test
    @Order(3)
    void save_instantJar_canBeFoundById() {
        InstantCoffeeJar jar = new InstantCoffeeJar("Nescafe", 400, 0.2, 0.4, 70, 1);
        int id = dao.save(jar);
        Optional<Coffee> found = dao.findById(id);
        assertTrue(found.isPresent());
        assertInstanceOf(InstantCoffeeJar.class, found.get());
        assertEquals(1, ((InstantCoffeeJar) found.get()).getGranules());
    }

    @Test
    @Order(4)
    void save_instantSachet_canBeFoundById() {
        InstantCoffeeSachet sac = new InstantCoffeeSachet("Jacobs", 500, 0.1, 0.2, 60, 10);
        int id = dao.save(sac);
        Optional<Coffee> found = dao.findById(id);
        assertTrue(found.isPresent());
        assertInstanceOf(InstantCoffeeSachet.class, found.get());
        assertEquals(10, ((InstantCoffeeSachet) found.get()).getSachetsCount());
    }

    @Test
    @Order(5)
    void findAll_returnsAllSaved() {
        dao.save(new BeanCoffee("A", 100, 1, 1.5, PackagingType.PACKAGE, 80, "X", "Y"));
        dao.save(new GroundCoffee("B", 200, 0.5, 0.7, PackagingType.PACKAGE, 75, "Med"));
        List<Coffee> all = dao.findAll();
        assertEquals(2, all.size());
    }

    @Test
    @Order(6)
    void update_changesName() {
        BeanCoffee bc = new BeanCoffee("OldName", 200, 1, 1.5, PackagingType.PACKAGE, 80, "Brazil", "Dark");
        int id = dao.save(bc);
        bc.setName("NewName");
        dao.update(bc);

        Optional<Coffee> updated = dao.findById(id);
        assertTrue(updated.isPresent());
        assertEquals("NewName", updated.get().getName());
    }

    @Test
    @Order(7)
    void delete_removesRecord() {
        BeanCoffee bc = new BeanCoffee("ToDelete", 300, 1, 1.5, PackagingType.BOX, 85, "Col", "Light");
        int id = dao.save(bc);
        dao.delete(id);
        assertTrue(dao.findById(id).isEmpty());
    }

    @Test
    @Order(8)
    void findById_nonExistent_returnsEmpty() {
        Optional<Coffee> found = dao.findById(99999);
        assertTrue(found.isEmpty());
    }

    @Test
    @Order(9)
    void deleteAll_removesEverything() {
        dao.save(new BeanCoffee("A", 100, 1, 1.5, PackagingType.PACKAGE, 80, "X", "Y"));
        dao.save(new BeanCoffee("B", 200, 1, 1.5, PackagingType.PACKAGE, 85, "X", "Y"));
        dao.deleteAll();
        assertEquals(0, dao.findAll().size());
    }
    @Test
    @Order(10)
    void mapRow_withUnknownType_throwsIllegalStateException() throws SQLException {
        // Manually insert a row with an invalid coffee type
        String sql = "INSERT INTO coffee (coffee_type, name, price_per_kg, weight_kg, volume_liters, packaging, quality_score) " +
                     "VALUES ('Unknown', 'BadCoffee', 100, 1.0, 1.0, 'PACKAGE', 50)";
        connection.createStatement().execute(sql);
        
        // Retrieve it via findAll to trigger mapRow
        assertThrows(IllegalStateException.class, () -> dao.findAll());
    }

    @Test
    @Order(11)
    void save_withGenericCoffeeSubclass_hitsElseInFillCommonFields() {
        // Create an anonymous subclass of Coffee
        Coffee genericCoffee = new Coffee("Generic", 150.0, 0.5, 0.5, PackagingType.BOX, 70) {
            @Override
            public String getCoffeeType() {
                return "GenericType";
            }
        };
        
        // This should run successfully and hit the else block in fillCommonFields
        int id = dao.save(genericCoffee);
        assertTrue(id > 0);
    }

    @Test
    @Order(12)
    void daoMethods_whenSqlException_handleGracefullyAndReturnDefaults() throws SQLException {
        Connection mockedConn = org.mockito.Mockito.mock(Connection.class);
        org.mockito.Mockito.when(mockedConn.prepareStatement(org.mockito.Mockito.anyString()))
                .thenThrow(new SQLException("Mocked DB error"));
        org.mockito.Mockito.when(mockedConn.prepareStatement(org.mockito.Mockito.anyString(), org.mockito.Mockito.anyInt()))
                .thenThrow(new SQLException("Mocked DB error"));
        org.mockito.Mockito.when(mockedConn.createStatement())
                .thenThrow(new SQLException("Mocked DB error"));
                
        CoffeeDaoImpl exceptionDao = new CoffeeDaoImpl(mockedConn);
        
        // Test save
        BeanCoffee bc = new BeanCoffee("Arabica", 300, 1.0, 1.5, PackagingType.PACKAGE, 90, "Ethiopia", "Medium");
        assertEquals(-1, exceptionDao.save(bc));
        
        // Test findById
        assertTrue(exceptionDao.findById(1).isEmpty());
        
        // Test findAll
        assertTrue(exceptionDao.findAll().isEmpty());
        
        // Test update
        assertDoesNotThrow(() -> exceptionDao.update(bc));
        
        // Test delete
        assertDoesNotThrow(() -> exceptionDao.delete(1));
        
        // Test deleteAll
        assertDoesNotThrow(() -> exceptionDao.deleteAll());
    }
}
