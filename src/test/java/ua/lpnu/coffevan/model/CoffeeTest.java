package ua.lpnu.coffevan.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoffeeTest {

    @Test
    void beanCoffee_getCoffeeType_returnsZernova() {
        BeanCoffee bc = new BeanCoffee("Arabica", 300, 1.0, 1.5, PackagingType.PACKAGE, 90, "Ethiopia", "Medium");
        assertEquals("Зернова", bc.getCoffeeType());
    }

    @Test
    void groundCoffee_getCoffeeType_returnsMelena() {
        GroundCoffee gc = new GroundCoffee("Espresso", 250, 0.5, 0.6, PackagingType.PACKAGE, 80, "Fine");
        assertEquals("Мелена", gc.getCoffeeType());
    }

    @Test
    void instantJar_getCoffeeType_returnsJar() {
        InstantCoffeeJar jar = new InstantCoffeeJar("Nescafe", 400, 0.2, 0.4, 70, 1);
        assertEquals("Розчинна (банка)", jar.getCoffeeType());
        assertEquals(PackagingType.JAR, jar.getPackagingType());
    }

    @Test
    void instantSachet_getCoffeeType_returnsSachet() {
        InstantCoffeeSachet sac = new InstantCoffeeSachet("Jacobs 3in1", 500, 0.1, 0.2, 60, 10);
        assertEquals("Розчинна (пакетик)", sac.getCoffeeType());
        assertEquals(PackagingType.BAG, sac.getPackagingType());
    }

    @Test
    void getTotalPrice_isCorrect() {
        BeanCoffee bc = new BeanCoffee("Test", 200, 2.0, 2.5, PackagingType.BOX, 85, "Brazil", "Dark");
        assertEquals(400.0, bc.getTotalPrice(), 0.001);
    }

    @Test
    void getPriceToWeightRatio_isCorrect() {
        GroundCoffee gc = new GroundCoffee("Test", 300, 3.0, 3.5, PackagingType.PACKAGE, 75, "Medium");
        assertEquals(100.0, gc.getPriceToWeightRatio(), 0.001);
    }

    @Test
    void getPriceToWeightRatio_zeroWeight_returnsZero() {
        GroundCoffee gc = new GroundCoffee("Test", 300, 0.0, 0.0, PackagingType.PACKAGE, 75, "Fine");
        assertEquals(0.0, gc.getPriceToWeightRatio(), 0.001);
    }

    @Test
    void settersAndGetters_workCorrectly() {
        BeanCoffee bc = new BeanCoffee();
        bc.setId(5);
        bc.setName("Sidamo");
        bc.setPricePerKg(450.0);
        bc.setWeightKg(1.5);
        bc.setVolumeLiters(2.0);
        bc.setPackagingType(PackagingType.BOX);
        bc.setQualityScore(95);
        bc.setOrigin("Ethiopia");
        bc.setRoastLevel("Light");

        assertEquals(5, bc.getId());
        assertEquals("Sidamo", bc.getName());
        assertEquals(450.0, bc.getPricePerKg(), 0.001);
        assertEquals(1.5, bc.getWeightKg(), 0.001);
        assertEquals(2.0, bc.getVolumeLiters(), 0.001);
        assertEquals(PackagingType.BOX, bc.getPackagingType());
        assertEquals(95, bc.getQualityScore());
        assertEquals("Ethiopia", bc.getOrigin());
        assertEquals("Light", bc.getRoastLevel());
    }

    @Test
    void groundCoffee_grindSize_setGet() {
        GroundCoffee gc = new GroundCoffee();
        gc.setGrindSize("Coarse");
        assertEquals("Coarse", gc.getGrindSize());
    }

    @Test
    void instantJar_granules_setGet() {
        InstantCoffeeJar jar = new InstantCoffeeJar();
        jar.setGranules(5);
        assertEquals(5, jar.getGranules());
    }

    @Test
    void instantSachet_sachetsCount_setGet() {
        InstantCoffeeSachet sac = new InstantCoffeeSachet();
        sac.setSachetsCount(20);
        assertEquals(20, sac.getSachetsCount());
    }

    @Test
    void coffee_equals_sameId() {
        BeanCoffee a = new BeanCoffee("A", 100, 1, 1.2, PackagingType.PACKAGE, 80, "X", "Y");
        a.setId(1);
        BeanCoffee b = new BeanCoffee("A", 100, 1, 1.2, PackagingType.PACKAGE, 80, "X", "Y");
        b.setId(1);
        assertEquals(a, b);
    }

    @Test
    void coffee_toString_containsName() {
        BeanCoffee bc = new BeanCoffee("MyBean", 300, 1, 1.5, PackagingType.PACKAGE, 88, "Col", "Med");
        assertTrue(bc.toString().contains("MyBean"));
    }
}
