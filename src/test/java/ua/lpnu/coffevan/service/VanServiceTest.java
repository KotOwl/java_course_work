package ua.lpnu.coffevan.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.lpnu.coffevan.dao.CoffeeDao;
import ua.lpnu.coffevan.dao.VanSettingsDaoInterface;
import ua.lpnu.coffevan.model.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class VanServiceTest {

    private StubCoffeeDao coffeeDao;
    private StubVanSettingsDao vanSettingsDao;
    private VanService vanService;
    private Van van;

    @BeforeEach
    void setUp() {
        van = new Van(1000.0, 50000.0);
        coffeeDao = new StubCoffeeDao();
        vanSettingsDao = new StubVanSettingsDao(van);
        vanService = new VanService(coffeeDao, vanSettingsDao);
    }

    @Test
    void addCoffee_whenFits_returnsTrueAndSaves() {
        BeanCoffee coffee = new BeanCoffee("Arabica", 300, 1.0, 5.0,
                PackagingType.PACKAGE, 90, "Ethiopia", "Medium");

        boolean result = vanService.addCoffee(coffee);

        assertTrue(result);
        assertEquals(1, coffeeDao.saved.size());
    }

    @Test
    void addCoffee_whenVolumeExceeded_returnsFalse() {
        Van tinyVan = new Van(1.0, 50000.0);
        StubVanSettingsDao smallDao = new StubVanSettingsDao(tinyVan);
        VanService service = new VanService(coffeeDao, smallDao);

        BeanCoffee coffee = new BeanCoffee("Big", 300, 5.0, 50.0,
                PackagingType.BOX, 80, "Brazil", "Dark");

        boolean result = service.addCoffee(coffee);

        assertFalse(result);
        assertEquals(0, coffeeDao.saved.size());
    }

    @Test
    void addCoffee_whenBudgetExceeded_returnsFalse() {
        Van poorVan = new Van(1000.0, 10.0);
        StubVanSettingsDao poorDao = new StubVanSettingsDao(poorVan);
        VanService service = new VanService(coffeeDao, poorDao);

        BeanCoffee coffee = new BeanCoffee("Expensive", 500, 1.0, 2.0,
                PackagingType.PACKAGE, 90, "Ethiopia", "Dark");

        assertFalse(service.addCoffee(coffee));
    }

    @Test
    void removeCoffee_deletesFromDaoAndUnloads() {
        BeanCoffee coffee = new BeanCoffee("Arabica", 300, 1.0, 5.0,
                PackagingType.PACKAGE, 90, "Ethiopia", "Medium");
        coffee.setId(1);
        van.load(coffee);
        coffeeDao.store.put(1, coffee);

        vanService.removeCoffee(coffee);

        assertFalse(coffeeDao.store.containsKey(1));
        assertEquals(0, van.getUsedVolumeLiters(), 0.001);
    }

    @Test
    void updateCoffee_updatesInDaoAndVan() {
        BeanCoffee original = new BeanCoffee("Old", 200, 1.0, 5.0,
                PackagingType.PACKAGE, 80, "Brazil", "Light");
        original.setId(1);
        van.load(original);
        coffeeDao.store.put(1, original);

        BeanCoffee updated = new BeanCoffee("New", 300, 1.0, 5.0,
                PackagingType.PACKAGE, 90, "Colombia", "Dark");
        updated.setId(1);

        vanService.updateCoffee(original, updated);

        assertEquals("New", coffeeDao.store.get(1).getName());
    }

    @Test
    void sortByPriceToWeightRatio_returnsSortedList() {
        BeanCoffee cheap = new BeanCoffee("Cheap", 100, 2.0, 3.0,
                PackagingType.PACKAGE, 60, "Brazil", "Light");
        BeanCoffee expensive = new BeanCoffee("Expensive", 400, 1.0, 2.0,
                PackagingType.BOX, 90, "Ethiopia", "Dark");
        coffeeDao.store.put(1, expensive);
        coffeeDao.store.put(2, cheap);

        List<Coffee> sorted = vanService.sortByPriceToWeightRatio();

        assertEquals("Cheap", sorted.get(0).getName());
        assertEquals("Expensive", sorted.get(1).getName());
    }

    @Test
    void findByQualityRange_filtersCorrectly() {
        BeanCoffee low = new BeanCoffee("Low", 100, 1.0, 1.5, PackagingType.PACKAGE, 40, "X", "Y");
        BeanCoffee mid = new BeanCoffee("Mid", 200, 1.0, 1.5, PackagingType.PACKAGE, 70, "X", "Y");
        BeanCoffee high = new BeanCoffee("High", 300, 1.0, 1.5, PackagingType.PACKAGE, 95, "X", "Y");
        coffeeDao.store.put(1, low);
        coffeeDao.store.put(2, mid);
        coffeeDao.store.put(3, high);

        List<Coffee> result = vanService.findByQualityRange(60, 80);

        assertEquals(1, result.size());
        assertEquals("Mid", result.get(0).getName());
    }

    @Test
    void clearVan_clearsAllAndResetsCounters() {
        BeanCoffee coffee = new BeanCoffee("Test", 200, 1.0, 5.0,
                PackagingType.PACKAGE, 80, "Brazil", "Med");
        van.load(coffee);
        coffeeDao.store.put(1, coffee);

        vanService.clearVan();

        assertEquals(0, coffeeDao.store.size());
        assertEquals(0, van.getUsedVolumeLiters(), 0.001);
        assertEquals(0, van.getSpentBudget(), 0.001);
    }

    @Test
    void updateVanSettings_updatesVanAndSaves() {
        vanService.updateVanSettings(2000.0, 100000.0);
        assertEquals(2000.0, van.getMaxVolumeLiters(), 0.001);
        assertEquals(100000.0, van.getMaxBudget(), 0.001);
        assertEquals(2000.0, vanSettingsDao.saved.getMaxVolumeLiters(), 0.001);
    }

    @Test
    void getTotalVolume_reflectsUsedVolume() {
        BeanCoffee coffee = new BeanCoffee("Test", 200, 1.0, 8.0,
                PackagingType.PACKAGE, 80, "Brazil", "Med");
        vanService.addCoffee(coffee);
        assertEquals(8.0, vanService.getTotalVolume(), 0.001);
    }

    @Test
    void getTotalBudget_reflectsSpentBudget() {
        BeanCoffee coffee = new BeanCoffee("Test", 500, 2.0, 3.0,
                PackagingType.PACKAGE, 80, "Col", "Dark");
        vanService.addCoffee(coffee);
        assertEquals(1000.0, vanService.getTotalBudget(), 0.001);
    }

    @Test
    void getAllCoffee_returnsAllFromDao() {
        BeanCoffee coffee = new BeanCoffee("Test", 200, 1.0, 2.0,
                PackagingType.PACKAGE, 80, "Brazil", "Med");
        coffeeDao.store.put(1, coffee);

        List<Coffee> result = vanService.getAllCoffee();
        assertEquals(1, result.size());
    }

    @Test
    void getVan_returnsVanInstance() {
        assertNotNull(vanService.getVan());
        assertEquals(1000.0, vanService.getVan().getMaxVolumeLiters(), 0.001);
    }

    // ── filterBy tests ───────────────────────────────────────────────────

    @Test
    void filterBy_byType_returnsOnlyMatchingType() {
        coffeeDao.store.put(1, new BeanCoffee("Bean", 300, 1.0, 1.5, PackagingType.PACKAGE, 80, "X", "Y"));
        coffeeDao.store.put(2, new GroundCoffee("Ground", 200, 1.0, 1.5, PackagingType.PACKAGE, 80, "Fine"));

        List<Coffee> result = vanService.filterBy("Зернова", null, 1, 0);
        assertEquals(1, result.size());
        assertEquals("Bean", result.get(0).getName());
    }

    @Test
    void filterBy_byPackaging_returnsOnlyMatchingPackaging() {
        coffeeDao.store.put(1, new BeanCoffee("Bean", 300, 1.0, 1.5, PackagingType.PACKAGE, 80, "X", "Y"));
        coffeeDao.store.put(2, new InstantCoffeeJar("Jar", 400, 0.2, 0.4, 70, 1));

        List<Coffee> result = vanService.filterBy(null, "Банка", 1, 0);
        assertEquals(1, result.size());
        assertEquals("Jar", result.get(0).getName());
    }

    @Test
    void filterBy_byMinQuality_returnsOnlyAboveThreshold() {
        coffeeDao.store.put(1, new BeanCoffee("Low", 100, 1.0, 1.5, PackagingType.PACKAGE, 30, "X", "Y"));
        coffeeDao.store.put(2, new BeanCoffee("High", 300, 1.0, 1.5, PackagingType.PACKAGE, 90, "X", "Y"));

        List<Coffee> result = vanService.filterBy(null, null, 80, 0);
        assertEquals(1, result.size());
        assertEquals("High", result.get(0).getName());
    }

    @Test
    void filterBy_byMaxPrice_returnsOnlyBelowOrEqual() {
        coffeeDao.store.put(1, new BeanCoffee("Cheap", 150, 1.0, 1.5, PackagingType.PACKAGE, 70, "X", "Y"));
        coffeeDao.store.put(2, new BeanCoffee("Expensive", 600, 1.0, 1.5, PackagingType.PACKAGE, 70, "X", "Y"));

        List<Coffee> result = vanService.filterBy(null, null, 1, 200.0);
        assertEquals(1, result.size());
        assertEquals("Cheap", result.get(0).getName());
    }

    @Test
    void filterBy_allNullsAndZeroPrice_returnsEverything() {
        coffeeDao.store.put(1, new BeanCoffee("A", 300, 1.0, 1.5, PackagingType.PACKAGE, 80, "X", "Y"));
        coffeeDao.store.put(2, new GroundCoffee("B", 200, 1.0, 1.5, PackagingType.PACKAGE, 60, "Med"));

        List<Coffee> result = vanService.filterBy(null, null, 1, 0);
        assertEquals(2, result.size());
    }

    @Test
    void filterBy_combinedTypeAndQuality_returnsCorrectSubset() {
        coffeeDao.store.put(1, new BeanCoffee("Bean_Low", 300, 1.0, 1.5, PackagingType.PACKAGE, 40, "X", "Y"));
        coffeeDao.store.put(2, new BeanCoffee("Bean_High", 300, 1.0, 1.5, PackagingType.PACKAGE, 90, "X", "Y"));
        coffeeDao.store.put(3, new GroundCoffee("Ground_High", 200, 1.0, 1.5, PackagingType.PACKAGE, 90, "Fine"));

        List<Coffee> result = vanService.filterBy("Зернова", null, 80, 0);
        assertEquals(1, result.size());
        assertEquals("Bean_High", result.get(0).getName());
    }

    @Test
    void filterBy_maxPriceZeroOrNegative_doesNotFilterByPrice() {
        coffeeDao.store.put(1, new BeanCoffee("A", 1000, 1.0, 1.5, PackagingType.PACKAGE, 80, "X", "Y"));

        List<Coffee> result = vanService.filterBy(null, null, 1, -1);
        assertEquals(1, result.size());
    }

    // ---- Stub implementations ----

    static class StubCoffeeDao implements CoffeeDao {
        final Map<Integer, Coffee> store = new LinkedHashMap<>();
        final List<Coffee> saved = new ArrayList<>();
        private int nextId = 1;

        @Override
        public int save(Coffee coffee) {
            coffee.setId(nextId);
            store.put(nextId, coffee);
            saved.add(coffee);
            return nextId++;
        }

        @Override
        public Optional<Coffee> findById(int id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public List<Coffee> findAll() {
            return new ArrayList<>(store.values());
        }

        @Override
        public void update(Coffee coffee) {
            store.put(coffee.getId(), coffee);
        }

        @Override
        public void delete(int id) {
            store.remove(id);
        }

        @Override
        public void deleteAll() {
            store.clear();
        }
    }

    static class StubVanSettingsDao implements VanSettingsDaoInterface {
        private final Van van;
        Van saved;

        StubVanSettingsDao(Van van) {
            this.van = van;
        }

        @Override
        public Van load() {
            return van;
        }

        @Override
        public void save(Van v) {
            this.saved = v;
        }
    }
}
