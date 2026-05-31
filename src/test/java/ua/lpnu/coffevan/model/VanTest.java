package ua.lpnu.coffevan.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VanTest {

    private Van van;
    private BeanCoffee coffee;

    @BeforeEach
    void setUp() {
        van = new Van(100.0, 5000.0);
        coffee = new BeanCoffee("Test", 200, 2.0, 10.0, PackagingType.PACKAGE, 80, "Brazil", "Dark");
    }

    @Test
    void initialState_usedVolumeAndBudgetAreZero() {
        assertEquals(0, van.getUsedVolumeLiters(), 0.001);
        assertEquals(0, van.getSpentBudget(), 0.001);
    }

    @Test
    void canLoad_whenFitsVolume_returnsTrue() {
        assertTrue(van.canLoad(coffee));
    }

    @Test
    void canLoad_whenExceedsVolume_returnsFalse() {
        Van smallVan = new Van(5.0, 50000.0);
        assertFalse(smallVan.canLoad(coffee));
    }

    @Test
    void canLoad_whenExceedsBudget_returnsFalse() {
        Van poorVan = new Van(1000.0, 100.0);
        assertFalse(poorVan.canLoad(coffee));
    }

    @Test
    void load_updatesUsedVolumeAndBudget() {
        van.load(coffee);
        assertEquals(10.0, van.getUsedVolumeLiters(), 0.001);
        assertEquals(400.0, van.getSpentBudget(), 0.001);
    }

    @Test
    void unload_decreasesUsedVolumeAndBudget() {
        van.load(coffee);
        van.unload(coffee);
        assertEquals(0, van.getUsedVolumeLiters(), 0.001);
        assertEquals(0, van.getSpentBudget(), 0.001);
    }

    @Test
    void getRemainingVolume_isCorrect() {
        van.load(coffee);
        assertEquals(90.0, van.getRemainingVolume(), 0.001);
    }

    @Test
    void getRemainingBudget_isCorrect() {
        van.load(coffee);
        assertEquals(4600.0, van.getRemainingBudget(), 0.001);
    }

    @Test
    void setters_workCorrectly() {
        van.setMaxVolumeLiters(200.0);
        van.setMaxBudget(10000.0);
        van.setUsedVolumeLiters(50.0);
        van.setSpentBudget(2000.0);

        assertEquals(200.0, van.getMaxVolumeLiters(), 0.001);
        assertEquals(10000.0, van.getMaxBudget(), 0.001);
        assertEquals(50.0, van.getUsedVolumeLiters(), 0.001);
        assertEquals(2000.0, van.getSpentBudget(), 0.001);
    }
}
