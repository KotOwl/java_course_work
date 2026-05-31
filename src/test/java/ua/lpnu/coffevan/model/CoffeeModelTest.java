package ua.lpnu.coffevan.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Coffee} abstract class and all its concrete subtypes.
 *
 * <p>These tests cover branches not yet exercised by higher-level service/DAO tests,
 * including {@code getPriceToWeightRatio()} when weight is zero, {@code equals()},
 * {@code hashCode()}, and {@code toString()} formatting.
 */
class CoffeeModelTest {

    // ── getPriceToWeightRatio ─────────────────────────────────────────────

    @Test
    void getPriceToWeightRatio_whenWeightIsZero_returnsZero() {
        BeanCoffee c = new BeanCoffee("Test", 500, 0.0, 1.0, PackagingType.PACKAGE, 80, "Ethiopia", "Medium");
        assertEquals(0.0, c.getPriceToWeightRatio(), 1e-9,
                "Ratio should be 0 when weight is 0 to avoid division by zero");
    }

    @Test
    void getPriceToWeightRatio_normalCase_returnsCorrectRatio() {
        BeanCoffee c = new BeanCoffee("Test", 600, 2.0, 3.0, PackagingType.PACKAGE, 90, "Colombia", "Dark");
        assertEquals(300.0, c.getPriceToWeightRatio(), 1e-6);
    }

    // ── getTotalPrice ─────────────────────────────────────────────────────

    @Test
    void getTotalPrice_returnsProductOfPriceAndWeight() {
        GroundCoffee c = new GroundCoffee("Espresso", 300, 2.5, 3.0, PackagingType.PACKAGE, 75, "Fine");
        assertEquals(750.0, c.getTotalPrice(), 1e-6);
    }

    // ── equals ───────────────────────────────────────────────────────────

    @Test
    void equals_sameInstance_returnsTrue() {
        BeanCoffee c = new BeanCoffee("A", 100, 1.0, 1.5, PackagingType.PACKAGE, 80, "X", "Y");
        assertEquals(c, c);
    }

    @Test
    void equals_differentType_returnsFalse() {
        BeanCoffee c = new BeanCoffee("A", 100, 1.0, 1.5, PackagingType.PACKAGE, 80, "X", "Y");
        assertNotEquals(c, "some string");
    }

    @Test
    void equals_nullObject_returnsFalse() {
        BeanCoffee c = new BeanCoffee("A", 100, 1.0, 1.5, PackagingType.PACKAGE, 80, "X", "Y");
        assertNotEquals(null, c);
    }

    @Test
    void equals_sameFieldValues_returnsTrue() {
        BeanCoffee c1 = new BeanCoffee("Arabica", 300, 1.0, 1.5, PackagingType.PACKAGE, 90, "Ethiopia", "Light");
        BeanCoffee c2 = new BeanCoffee("Arabica", 300, 1.0, 1.5, PackagingType.PACKAGE, 90, "Ethiopia", "Light");
        assertEquals(c1, c2);
    }

    @Test
    void equals_differentName_returnsFalse() {
        BeanCoffee c1 = new BeanCoffee("Arabica", 300, 1.0, 1.5, PackagingType.PACKAGE, 90, "X", "Y");
        BeanCoffee c2 = new BeanCoffee("Robusta", 300, 1.0, 1.5, PackagingType.PACKAGE, 90, "X", "Y");
        assertNotEquals(c1, c2);
    }

    @Test
    void equals_differentPrice_returnsFalse() {
        BeanCoffee c1 = new BeanCoffee("A", 300, 1.0, 1.5, PackagingType.PACKAGE, 90, "X", "Y");
        BeanCoffee c2 = new BeanCoffee("A", 400, 1.0, 1.5, PackagingType.PACKAGE, 90, "X", "Y");
        assertNotEquals(c1, c2);
    }

    @Test
    void equals_differentWeight_returnsFalse() {
        BeanCoffee c1 = new BeanCoffee("A", 300, 1.0, 1.5, PackagingType.PACKAGE, 90, "X", "Y");
        BeanCoffee c2 = new BeanCoffee("A", 300, 2.0, 1.5, PackagingType.PACKAGE, 90, "X", "Y");
        assertNotEquals(c1, c2);
    }

    @Test
    void equals_differentQuality_returnsFalse() {
        BeanCoffee c1 = new BeanCoffee("A", 300, 1.0, 1.5, PackagingType.PACKAGE, 90, "X", "Y");
        BeanCoffee c2 = new BeanCoffee("A", 300, 1.0, 1.5, PackagingType.PACKAGE, 70, "X", "Y");
        assertNotEquals(c1, c2);
    }

    // ── hashCode ──────────────────────────────────────────────────────────

    @Test
    void hashCode_equalObjects_haveSameHashCode() {
        BeanCoffee c1 = new BeanCoffee("Arabica", 300, 1.0, 1.5, PackagingType.PACKAGE, 90, "X", "Y");
        BeanCoffee c2 = new BeanCoffee("Arabica", 300, 1.0, 1.5, PackagingType.PACKAGE, 90, "X", "Y");
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    // ── toString ──────────────────────────────────────────────────────────

    @Test
    void toString_containsNameAndType() {
        BeanCoffee c = new BeanCoffee("Arabica Premium", 400, 1.0, 1.5, PackagingType.PACKAGE, 90, "X", "Y");
        String s = c.toString();
        assertTrue(s.contains("Arabica Premium"), "toString should contain name");
        assertTrue(s.contains("Зернова"),         "toString should contain coffee type");
    }

    @Test
    void toString_groundCoffee_containsCorrectType() {
        GroundCoffee c = new GroundCoffee("Espresso", 350, 0.5, 0.7, PackagingType.BAG, 80, "Fine");
        assertTrue(c.toString().contains("Мелена"));
    }

    @Test
    void toString_instantCoffeeJar_containsCorrectType() {
        InstantCoffeeJar c = new InstantCoffeeJar("Nescafe", 400, 0.2, 0.4, 70, 1);
        assertTrue(c.toString().contains("Розчинна (банка)"));
    }

    @Test
    void toString_instantCoffeeSachet_containsCorrectType() {
        InstantCoffeeSachet c = new InstantCoffeeSachet("MacCoffee", 180, 0.02, 0.05, 32, 24);
        assertTrue(c.toString().contains("Розчинна (пакетик)"));
    }

    // ── setters ───────────────────────────────────────────────────────────

    @Test
    void setters_updateFieldsCorrectly() {
        BeanCoffee c = new BeanCoffee("Old", 100, 1.0, 1.5, PackagingType.PACKAGE, 80, "X", "Y");
        c.setName("New");
        c.setPricePerKg(500);
        c.setWeightKg(2.0);
        c.setVolumeLiters(3.0);
        c.setPackagingType(PackagingType.BOX);
        c.setQualityScore(95);

        assertEquals("New", c.getName());
        assertEquals(500, c.getPricePerKg(), 1e-6);
        assertEquals(2.0, c.getWeightKg(), 1e-6);
        assertEquals(3.0, c.getVolumeLiters(), 1e-6);
        assertEquals(PackagingType.BOX, c.getPackagingType());
        assertEquals(95, c.getQualityScore());
    }

    // ── BeanCoffee specific ───────────────────────────────────────────────

    @Test
    void beanCoffee_getOriginAndRoastLevel() {
        BeanCoffee c = new BeanCoffee("Test", 300, 1.0, 1.5, PackagingType.PACKAGE, 80, "Ethiopia", "Light");
        assertEquals("Ethiopia", c.getOrigin());
        assertEquals("Light", c.getRoastLevel());
    }

    @Test
    void beanCoffee_setOriginAndRoastLevel() {
        BeanCoffee c = new BeanCoffee("Test", 300, 1.0, 1.5, PackagingType.PACKAGE, 80, "X", "Y");
        c.setOrigin("Brazil");
        c.setRoastLevel("Dark");
        assertEquals("Brazil", c.getOrigin());
        assertEquals("Dark", c.getRoastLevel());
    }

    @Test
    void beanCoffee_coffeeTypeIsCorrect() {
        BeanCoffee c = new BeanCoffee("Test", 300, 1.0, 1.5, PackagingType.PACKAGE, 80, "X", "Y");
        assertEquals("Зернова", c.getCoffeeType());
    }

    // ── GroundCoffee specific ─────────────────────────────────────────────

    @Test
    void groundCoffee_getGrindSize() {
        GroundCoffee c = new GroundCoffee("Test", 200, 1.0, 1.5, PackagingType.PACKAGE, 75, "Coarse");
        assertEquals("Coarse", c.getGrindSize());
    }

    @Test
    void groundCoffee_setGrindSize() {
        GroundCoffee c = new GroundCoffee("Test", 200, 1.0, 1.5, PackagingType.PACKAGE, 75, "Fine");
        c.setGrindSize("Medium");
        assertEquals("Medium", c.getGrindSize());
    }

    @Test
    void groundCoffee_coffeeTypeIsCorrect() {
        GroundCoffee c = new GroundCoffee("Test", 200, 1.0, 1.5, PackagingType.PACKAGE, 75, "Fine");
        assertEquals("Мелена", c.getCoffeeType());
    }

    // ── InstantCoffeeJar specific ─────────────────────────────────────────

    @Test
    void instantCoffeeJar_getGranules() {
        InstantCoffeeJar c = new InstantCoffeeJar("Nescafe", 400, 0.2, 0.4, 70, 1);
        assertEquals(1, c.getGranules());
    }

    @Test
    void instantCoffeeJar_setGranules() {
        InstantCoffeeJar c = new InstantCoffeeJar("Nescafe", 400, 0.2, 0.4, 70, 0);
        c.setGranules(1);
        assertEquals(1, c.getGranules());
    }

    @Test
    void instantCoffeeJar_coffeeTypeIsCorrect() {
        InstantCoffeeJar c = new InstantCoffeeJar("Nescafe", 400, 0.2, 0.4, 70, 1);
        assertEquals("Розчинна (банка)", c.getCoffeeType());
    }

    // ── InstantCoffeeSachet specific ──────────────────────────────────────

    @Test
    void instantCoffeeSachet_getSachetsCount() {
        InstantCoffeeSachet c = new InstantCoffeeSachet("MacCoffee", 180, 0.02, 0.05, 32, 24);
        assertEquals(24, c.getSachetsCount());
    }

    @Test
    void instantCoffeeSachet_setSachetsCount() {
        InstantCoffeeSachet c = new InstantCoffeeSachet("MacCoffee", 180, 0.02, 0.05, 32, 10);
        c.setSachetsCount(30);
        assertEquals(30, c.getSachetsCount());
    }

    @Test
    void instantCoffeeSachet_coffeeTypeIsCorrect() {
        InstantCoffeeSachet c = new InstantCoffeeSachet("MacCoffee", 180, 0.02, 0.05, 32, 24);
        assertEquals("Розчинна (пакетик)", c.getCoffeeType());
    }

    // ── PackagingType ────────────────────────────────────────────────────

    @Test
    void packagingType_displayNames_areCorrect() {
        assertEquals("Пакет",  PackagingType.PACKAGE.getDisplayName());
        assertEquals("Пакетик", PackagingType.BAG.getDisplayName());
        assertEquals("Банка",  PackagingType.JAR.getDisplayName());
        assertEquals("Коробка",PackagingType.BOX.getDisplayName());
    }

    @Test
    void packagingType_values_containsAllFour() {
        PackagingType[] types = PackagingType.values();
        assertEquals(4, types.length);
    }

    // ── Van ───────────────────────────────────────────────────────────────

    @Test
    void van_canLoad_returnsTrueWhenFits() {
        Van van = new Van(100.0, 10000.0);
        BeanCoffee c = new BeanCoffee("Test", 300, 1.0, 5.0, PackagingType.PACKAGE, 80, "X", "Y");
        assertTrue(van.canLoad(c));
    }

    @Test
    void van_canLoad_returnsFalseWhenVolumeExceeded() {
        Van van = new Van(1.0, 10000.0);
        BeanCoffee c = new BeanCoffee("Big", 300, 5.0, 50.0, PackagingType.BOX, 80, "X", "Y");
        assertFalse(van.canLoad(c));
    }

    @Test
    void van_canLoad_returnsFalseWhenBudgetExceeded() {
        Van van = new Van(1000.0, 1.0);
        BeanCoffee c = new BeanCoffee("Expensive", 500, 1.0, 1.0, PackagingType.PACKAGE, 90, "X", "Y");
        assertFalse(van.canLoad(c));
    }

    @Test
    void van_unload_decreasesVolumeAndBudget() {
        Van van = new Van(100.0, 10000.0);
        BeanCoffee c = new BeanCoffee("Test", 300, 1.0, 5.0, PackagingType.PACKAGE, 80, "X", "Y");
        van.load(c);
        van.unload(c);
        assertEquals(0.0, van.getUsedVolumeLiters(), 1e-6);
        assertEquals(0.0, van.getSpentBudget(), 1e-6);
    }

    @Test
    void van_getRemainingVolume_isCorrect() {
        Van van = new Van(100.0, 10000.0);
        BeanCoffee c = new BeanCoffee("Test", 100, 1.0, 20.0, PackagingType.PACKAGE, 80, "X", "Y");
        van.load(c);
        assertEquals(80.0, van.getRemainingVolume(), 1e-6);
    }

    @Test
    void van_getRemainingBudget_isCorrect() {
        Van van = new Van(100.0, 10000.0);
        BeanCoffee c = new BeanCoffee("Test", 300, 2.0, 5.0, PackagingType.PACKAGE, 80, "X", "Y");
        van.load(c); // total price = 300 * 2.0 = 600
        assertEquals(9400.0, van.getRemainingBudget(), 1e-6);
    }

    @Test
    void van_setters_updateCorrectly() {
        Van van = new Van(100.0, 5000.0);
        van.setMaxVolumeLiters(200.0);
        van.setMaxBudget(10000.0);
        van.setUsedVolumeLiters(50.0);
        van.setSpentBudget(2500.0);

        assertEquals(200.0, van.getMaxVolumeLiters(), 1e-6);
        assertEquals(10000.0, van.getMaxBudget(), 1e-6);
        assertEquals(50.0, van.getUsedVolumeLiters(), 1e-6);
        assertEquals(2500.0, van.getSpentBudget(), 1e-6);
    }
}
