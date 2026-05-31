package ua.lpnu.coffevan.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for all concrete Coffee subclasses:
 * getters/setters, toString, equals, hashCode, and type-specific attributes.
 */
class CoffeeSubclassesTest {

    // ────────────────────────────────────────────────────────────────────
    // BeanCoffee
    // ────────────────────────────────────────────────────────────────────

    @Test
    void beanCoffee_getCoffeeType_returnsЗернова() {
        BeanCoffee bc = new BeanCoffee();
        // via subtype-specific constructor
        BeanCoffee bc2 = new BeanCoffee("Arabica", 300, 1.0, 1.5,
                PackagingType.PACKAGE, 90, "Ethiopia", "Light");
        assertEquals("Зернова", bc2.getCoffeeType());
    }

    @Test
    void beanCoffee_settersAndGetters_workCorrectly() {
        BeanCoffee bc = new BeanCoffee();
        bc.setOrigin("Brazil");
        bc.setRoastLevel("Dark");
        assertEquals("Brazil", bc.getOrigin());
        assertEquals("Dark",   bc.getRoastLevel());
    }

    @Test
    void beanCoffee_toString_containsOriginAndRoast() {
        BeanCoffee bc = new BeanCoffee("Arabica", 300, 1.0, 1.5,
                PackagingType.PACKAGE, 90, "Ethiopia", "Light");
        String str = bc.toString();
        assertTrue(str.contains("Ethiopia"));
        assertTrue(str.contains("Light"));
        assertTrue(str.contains("Зернова"));
    }

    @Test
    void beanCoffee_noArgsConstructor_createsInstance() {
        BeanCoffee bc = new BeanCoffee();
        assertNotNull(bc);
    }

    // ────────────────────────────────────────────────────────────────────
    // GroundCoffee
    // ────────────────────────────────────────────────────────────────────

    @Test
    void groundCoffee_getCoffeeType_returnsМелена() {
        GroundCoffee gc = new GroundCoffee("Lavazza", 320, 1.0, 1.5,
                PackagingType.PACKAGE, 75, "Fine");
        assertEquals("Мелена", gc.getCoffeeType());
    }

    @Test
    void groundCoffee_setterAndGetter_grindSize() {
        GroundCoffee gc = new GroundCoffee();
        gc.setGrindSize("Coarse");
        assertEquals("Coarse", gc.getGrindSize());
    }

    @Test
    void groundCoffee_toString_containsGrindSize() {
        GroundCoffee gc = new GroundCoffee("Lavazza", 320, 1.0, 1.5,
                PackagingType.PACKAGE, 75, "Fine");
        assertTrue(gc.toString().contains("Fine"));
    }

    @Test
    void groundCoffee_noArgsConstructor_createsInstance() {
        assertNotNull(new GroundCoffee());
    }

    // ────────────────────────────────────────────────────────────────────
    // InstantCoffeeJar
    // ────────────────────────────────────────────────────────────────────

    @Test
    void instantCoffeeJar_getCoffeeType_returnsCorrectLabel() {
        InstantCoffeeJar jar = new InstantCoffeeJar("Nescafe", 400, 0.2, 0.4, 70, 1);
        assertEquals("Розчинна (банка)", jar.getCoffeeType());
    }

    @Test
    void instantCoffeeJar_setterAndGetter_granules() {
        InstantCoffeeJar jar = new InstantCoffeeJar();
        jar.setGranules(5);
        assertEquals(5, jar.getGranules());
    }

    @Test
    void instantCoffeeJar_toString_granules_positive() {
        InstantCoffeeJar jar = new InstantCoffeeJar("Nescafe", 400, 0.2, 0.4, 70, 1);
        assertTrue(jar.toString().contains("так"));
    }

    @Test
    void instantCoffeeJar_toString_granules_zero() {
        InstantCoffeeJar jar = new InstantCoffeeJar("Jacobs", 350, 0.1, 0.25, 50, 0);
        assertTrue(jar.toString().contains("ні"));
    }

    @Test
    void instantCoffeeJar_packagingType_isJar() {
        InstantCoffeeJar jar = new InstantCoffeeJar("Nescafe", 400, 0.2, 0.4, 70, 1);
        assertEquals(PackagingType.JAR, jar.getPackagingType());
    }

    @Test
    void instantCoffeeJar_noArgsConstructor_createsInstance() {
        assertNotNull(new InstantCoffeeJar());
    }

    // ────────────────────────────────────────────────────────────────────
    // InstantCoffeeSachet
    // ────────────────────────────────────────────────────────────────────

    @Test
    void instantCoffeeSachet_getCoffeeType_returnsCorrectLabel() {
        InstantCoffeeSachet sac = new InstantCoffeeSachet("MacCoffee", 180, 0.02, 0.05, 32, 24);
        assertEquals("Розчинна (пакетик)", sac.getCoffeeType());
    }

    @Test
    void instantCoffeeSachet_setterAndGetter_sachetsCount() {
        InstantCoffeeSachet sac = new InstantCoffeeSachet();
        sac.setSachetsCount(12);
        assertEquals(12, sac.getSachetsCount());
    }

    @Test
    void instantCoffeeSachet_toString_containsSachetsCount() {
        InstantCoffeeSachet sac = new InstantCoffeeSachet("MacCoffee", 180, 0.02, 0.05, 32, 24);
        assertTrue(sac.toString().contains("24"));
    }

    @Test
    void instantCoffeeSachet_packagingType_isBag() {
        InstantCoffeeSachet sac = new InstantCoffeeSachet("MacCoffee", 180, 0.02, 0.05, 32, 24);
        assertEquals(PackagingType.BAG, sac.getPackagingType());
    }

    @Test
    void instantCoffeeSachet_noArgsConstructor_createsInstance() {
        assertNotNull(new InstantCoffeeSachet());
    }

    // ────────────────────────────────────────────────────────────────────
    // PackagingType enum
    // ────────────────────────────────────────────────────────────────────

    @Test
    void packagingType_allValues_haveDisplayNames() {
        for (PackagingType pt : PackagingType.values()) {
            assertNotNull(pt.getDisplayName());
            assertFalse(pt.getDisplayName().isBlank());
        }
    }

    // ────────────────────────────────────────────────────────────────────
    // Coffee abstract — equals / hashCode
    // ────────────────────────────────────────────────────────────────────

    @Test
    void coffee_equals_sameFields_returnsTrue() {
        BeanCoffee a = new BeanCoffee("Arabica", 300, 1.0, 1.5, PackagingType.PACKAGE, 90, "Eth", "Light");
        BeanCoffee b = new BeanCoffee("Arabica", 300, 1.0, 1.5, PackagingType.PACKAGE, 90, "Eth", "Light");
        assertEquals(a, b);
    }

    @Test
    void coffee_equals_differentName_returnsFalse() {
        BeanCoffee a = new BeanCoffee("Arabica",  300, 1.0, 1.5, PackagingType.PACKAGE, 90, "Eth", "Light");
        BeanCoffee b = new BeanCoffee("Robusta",  300, 1.0, 1.5, PackagingType.PACKAGE, 90, "Eth", "Light");
        assertNotEquals(a, b);
    }

    @Test
    void coffee_hashCode_equalObjects_sameHashCode() {
        BeanCoffee a = new BeanCoffee("Arabica", 300, 1.0, 1.5, PackagingType.PACKAGE, 90, "Eth", "Light");
        BeanCoffee b = new BeanCoffee("Arabica", 300, 1.0, 1.5, PackagingType.PACKAGE, 90, "Eth", "Light");
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void coffee_equals_sameReference_returnsTrue() {
        BeanCoffee a = new BeanCoffee("Arabica", 300, 1.0, 1.5, PackagingType.PACKAGE, 90, "Eth", "Light");
        assertEquals(a, a);
    }

    @Test
    void coffee_equals_null_returnsFalse() {
        BeanCoffee a = new BeanCoffee("Arabica", 300, 1.0, 1.5, PackagingType.PACKAGE, 90, "Eth", "Light");
        assertNotEquals(null, a);
    }

    // ────────────────────────────────────────────────────────────────────
    // Coffee abstract — computed methods
    // ────────────────────────────────────────────────────────────────────

    @Test
    void coffee_getTotalPrice_calculatedCorrectly() {
        BeanCoffee bc = new BeanCoffee("A", 300, 2.0, 2.0, PackagingType.PACKAGE, 80, "X", "Y");
        assertEquals(600.0, bc.getTotalPrice(), 0.001);
    }

    @Test
    void coffee_getPriceToWeightRatio_calculatedCorrectly() {
        BeanCoffee bc = new BeanCoffee("A", 300, 2.0, 2.0, PackagingType.PACKAGE, 80, "X", "Y");
        assertEquals(150.0, bc.getPriceToWeightRatio(), 0.001);
    }

    @Test
    void coffee_getPriceToWeightRatio_whenWeightZero_returnsZero() {
        BeanCoffee bc = new BeanCoffee("A", 300, 0.0, 0.0, PackagingType.PACKAGE, 80, "X", "Y");
        assertEquals(0.0, bc.getPriceToWeightRatio(), 0.001);
    }
}
