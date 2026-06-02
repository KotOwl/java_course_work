package ua.lpnu.coffevan.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DtoTest {

    @Test
    void testCoffeeRequest() {
        CoffeeRequest req = new CoffeeRequest();
        req.setCoffeeType("Зернова");
        req.setName("Ethiopia");
        req.setPricePerKg(300.0);
        req.setWeightKg(1.2);
        req.setVolumeLiters(2.0);
        req.setPackagingType("PACKAGE");
        req.setQualityScore(90);
        req.setExtra1("Origin");
        req.setExtra2("Roast");

        assertEquals("Зернова", req.getCoffeeType());
        assertEquals("Ethiopia", req.getName());
        assertEquals(300.0, req.getPricePerKg());
        assertEquals(1.2, req.getWeightKg());
        assertEquals(2.0, req.getVolumeLiters());
        assertEquals("PACKAGE", req.getPackagingType());
        assertEquals(90, req.getQualityScore());
        assertEquals("Origin", req.getExtra1());
        assertEquals("Roast", req.getExtra2());

        CoffeeRequest reqConstructor = new CoffeeRequest(
                "Мелена", "Espresso", 250.0, 0.5, 0.6, "BOX", 85, "Fine", "N/A"
        );
        assertEquals("Мелена", reqConstructor.getCoffeeType());
        assertEquals("Espresso", reqConstructor.getName());
        assertEquals(250.0, reqConstructor.getPricePerKg());
        assertEquals(0.5, reqConstructor.getWeightKg());
        assertEquals(0.6, reqConstructor.getVolumeLiters());
        assertEquals("BOX", reqConstructor.getPackagingType());
        assertEquals(85, reqConstructor.getQualityScore());
        assertEquals("Fine", reqConstructor.getExtra1());
        assertEquals("N/A", reqConstructor.getExtra2());
    }

    @Test
    void testCoffeeResponse() {
        CoffeeResponse res = new CoffeeResponse();
        res.setId(1);
        res.setCoffeeType("Мелена");
        res.setName("Lviv");
        res.setPricePerKg(200.0);
        res.setWeightKg(0.5);
        res.setVolumeLiters(0.7);
        res.setPackagingType("BOX");
        res.setQualityScore(80);
        res.setTotalPrice(100.0);
        res.setPriceToWeightRatio(400.0);

        assertEquals(1, res.getId());
        assertEquals("Мелена", res.getCoffeeType());
        assertEquals("Lviv", res.getName());
        assertEquals(200.0, res.getPricePerKg());
        assertEquals(0.5, res.getWeightKg());
        assertEquals(0.7, res.getVolumeLiters());
        assertEquals("BOX", res.getPackagingType());
        assertEquals(80, res.getQualityScore());
        assertEquals(100.0, res.getTotalPrice());
        assertEquals(400.0, res.getPriceToWeightRatio());

        CoffeeResponse resConstructor = new CoffeeResponse(
                2, "Зернова", "Colombia", 400.0, 1.0, 1.5, "PACKAGE", 95, 400.0, 400.0
        );
        assertEquals(2, resConstructor.getId());
        assertEquals("Зернова", resConstructor.getCoffeeType());
        assertEquals("Colombia", resConstructor.getName());
        assertEquals(400.0, resConstructor.getPricePerKg());
        assertEquals(1.0, resConstructor.getWeightKg());
        assertEquals(1.5, resConstructor.getVolumeLiters());
        assertEquals("PACKAGE", resConstructor.getPackagingType());
        assertEquals(95, resConstructor.getQualityScore());
        assertEquals(400.0, resConstructor.getTotalPrice());
        assertEquals(400.0, resConstructor.getPriceToWeightRatio());

        assertNotNull(resConstructor.toString());
    }

    @Test
    void testVanResponse() {
        VanResponse res = new VanResponse();
        res.setMaxVolumeLiters(1500.0);
        res.setMaxBudget(60000.0);
        res.setUsedVolumeLiters(500.0);
        res.setSpentBudget(20000.0);

        assertEquals(1500.0, res.getMaxVolumeLiters());
        assertEquals(60000.0, res.getMaxBudget());
        assertEquals(500.0, res.getUsedVolumeLiters());
        assertEquals(20000.0, res.getSpentBudget());
        assertEquals(1000.0, res.getFreeVolumeLiters());
        assertEquals(40000.0, res.getRemainingBudget());

        VanResponse resConstructor = new VanResponse(1000.0, 50000.0, 200.0, 10000.0);
        assertEquals(1000.0, resConstructor.getMaxVolumeLiters());
        assertEquals(50000.0, resConstructor.getMaxBudget());
        assertEquals(200.0, resConstructor.getUsedVolumeLiters());
        assertEquals(10000.0, resConstructor.getSpentBudget());
        assertEquals(40000.0, resConstructor.getRemainingBudget());
        assertEquals(800.0, resConstructor.getFreeVolumeLiters());

        assertNotNull(resConstructor.toString());
    }
}
