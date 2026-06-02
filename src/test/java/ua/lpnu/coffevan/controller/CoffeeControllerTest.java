package ua.lpnu.coffevan.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.lpnu.coffevan.dto.CoffeeRequest;
import ua.lpnu.coffevan.dto.CoffeeResponse;
import ua.lpnu.coffevan.model.*;
import ua.lpnu.coffevan.service.VanService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoffeeControllerTest {

    @Mock
    private VanService vanService;

    @InjectMocks
    private CoffeeController coffeeController;

    private CoffeeRequest beanRequest;
    private CoffeeRequest groundRequest;
    private CoffeeRequest jarRequest;
    private CoffeeRequest sachetRequest;

    @BeforeEach
    void setUp() {
        beanRequest = new CoffeeRequest("Зернова", "Ethiopia Arabica", 500.0, 1.5, 2.5, "PACKAGE", 95, "Ethiopia", "Medium");
        groundRequest = new CoffeeRequest("Мелена", "Espresso Blend", 350.0, 0.5, 0.8, "BOX", 80, "Fine", null);
        jarRequest = new CoffeeRequest("Розчинна (банка)", "Instant Gold", 450.0, 0.2, 0.4, "JAR", 70, "1", null);
        sachetRequest = new CoffeeRequest("Розчинна (пакетик)", "Instant 3in1", 200.0, 0.02, 0.05, "PACKAGE", 50, "20", null);
    }

    @Test
    void addCoffee_beanCoffee_mapsAndDelegatesToService() {
        ArgumentCaptor<Coffee> captor = ArgumentCaptor.forClass(Coffee.class);
        when(vanService.addCoffee(captor.capture())).thenReturn(true);

        boolean result = coffeeController.addCoffee(beanRequest);

        assertTrue(result);
        Coffee coffee = captor.getValue();
        assertInstanceOf(BeanCoffee.class, coffee);
        assertEquals("Ethiopia Arabica", coffee.getName());
        assertEquals(500.0, coffee.getPricePerKg());
        assertEquals(1.5, coffee.getWeightKg());
        assertEquals(2.5, coffee.getVolumeLiters());
        assertEquals(PackagingType.PACKAGE, coffee.getPackagingType());
        assertEquals(95, coffee.getQualityScore());
        assertEquals("Ethiopia", ((BeanCoffee) coffee).getOrigin());
        assertEquals("Medium", ((BeanCoffee) coffee).getRoastLevel());
    }

    @Test
    void addCoffee_groundCoffee_mapsAndDelegatesToService() {
        ArgumentCaptor<Coffee> captor = ArgumentCaptor.forClass(Coffee.class);
        when(vanService.addCoffee(captor.capture())).thenReturn(false);

        boolean result = coffeeController.addCoffee(groundRequest);

        assertFalse(result);
        Coffee coffee = captor.getValue();
        assertInstanceOf(GroundCoffee.class, coffee);
        assertEquals("Espresso Blend", coffee.getName());
        assertEquals(PackagingType.BOX, coffee.getPackagingType());
        assertEquals("Fine", ((GroundCoffee) coffee).getGrindSize());
    }

    @Test
    void addCoffee_jarCoffee_mapsAndDelegatesToService() {
        ArgumentCaptor<Coffee> captor = ArgumentCaptor.forClass(Coffee.class);
        when(vanService.addCoffee(captor.capture())).thenReturn(true);

        boolean result = coffeeController.addCoffee(jarRequest);

        assertTrue(result);
        Coffee coffee = captor.getValue();
        assertInstanceOf(InstantCoffeeJar.class, coffee);
        assertEquals(1, ((InstantCoffeeJar) coffee).getGranules());
    }

    @Test
    void addCoffee_sachetCoffee_mapsAndDelegatesToService() {
        ArgumentCaptor<Coffee> captor = ArgumentCaptor.forClass(Coffee.class);
        when(vanService.addCoffee(captor.capture())).thenReturn(true);

        boolean result = coffeeController.addCoffee(sachetRequest);

        assertTrue(result);
        Coffee coffee = captor.getValue();
        assertInstanceOf(InstantCoffeeSachet.class, coffee);
        assertEquals(20, ((InstantCoffeeSachet) coffee).getSachetsCount());
    }

    @Test
    void addCoffee_unknownType_throwsIllegalArgumentException() {
        CoffeeRequest badRequest = new CoffeeRequest("UnknownType", "Bad", 100, 1, 1, "PACKAGE", 50, null, null);
        assertThrows(IllegalArgumentException.class, () -> coffeeController.addCoffee(badRequest));
    }

    @Test
    void getAllCoffee_returnsMappedResponses() {
        BeanCoffee bc = new BeanCoffee("Ethiopia Arabica", 500.0, 1.5, 2.5, PackagingType.PACKAGE, 95, "Ethiopia", "Medium");
        bc.setId(10);
        when(vanService.getAllCoffee()).thenReturn(List.of(bc));

        List<CoffeeResponse> responses = coffeeController.getAllCoffee();

        assertEquals(1, responses.size());
        CoffeeResponse res = responses.get(0);
        assertEquals(10, res.getId());
        assertEquals("Зернова", res.getCoffeeType());
        assertEquals("Ethiopia Arabica", res.getName());
        assertEquals(500.0, res.getPricePerKg());
        assertEquals(1.5, res.getWeightKg());
        assertEquals(2.5, res.getVolumeLiters());
        assertEquals("PACKAGE", res.getPackagingType());
        assertEquals(95, res.getQualityScore());
        assertEquals(750.0, res.getTotalPrice());
    }

    @Test
    void removeCoffee_existingId_callsServiceRemove() {
        BeanCoffee bc = new BeanCoffee("Arabica", 500.0, 1.5, 2.5, PackagingType.PACKAGE, 95, "Ethiopia", "Medium");
        bc.setId(5);
        when(vanService.getAllCoffee()).thenReturn(List.of(bc));

        coffeeController.removeCoffee(5);

        verify(vanService).removeCoffee(bc);
    }

    @Test
    void removeCoffee_nonExistentId_doesNotCallServiceRemove() {
        BeanCoffee bc = new BeanCoffee("Arabica", 500.0, 1.5, 2.5, PackagingType.PACKAGE, 95, "Ethiopia", "Medium");
        bc.setId(5);
        when(vanService.getAllCoffee()).thenReturn(List.of(bc));

        coffeeController.removeCoffee(10);

        verify(vanService, never()).removeCoffee(any());
    }

    @Test
    void updateCoffee_callsServiceUpdate() {
        BeanCoffee original = new BeanCoffee("Old", 400, 1, 1, PackagingType.PACKAGE, 80, "", "");
        original.setId(42);

        ArgumentCaptor<Coffee> captor = ArgumentCaptor.forClass(Coffee.class);

        coffeeController.updateCoffee(original, beanRequest);

        verify(vanService).updateCoffee(eq(original), captor.capture());
        Coffee updated = captor.getValue();
        assertEquals(42, updated.getId());
        assertEquals("Ethiopia Arabica", updated.getName());
    }

    @Test
    void sortByPriceToWeightRatio_returnsSortedResponses() {
        BeanCoffee bc = new BeanCoffee("Arabica", 500.0, 1.5, 2.5, PackagingType.PACKAGE, 95, "Ethiopia", "Medium");
        when(vanService.sortByPriceToWeightRatio()).thenReturn(List.of(bc));

        List<CoffeeResponse> sorted = coffeeController.sortByPriceToWeightRatio();

        assertEquals(1, sorted.size());
        assertEquals("Arabica", sorted.get(0).getName());
    }

    @Test
    void filterCoffee_returnsFilteredResponses() {
        BeanCoffee bc = new BeanCoffee("Arabica", 500.0, 1.5, 2.5, PackagingType.PACKAGE, 95, "Ethiopia", "Medium");
        when(vanService.filterBy("Зернова", "PACKAGE", 90, 600.0)).thenReturn(List.of(bc));

        List<CoffeeResponse> filtered = coffeeController.filterCoffee("Зернова", "PACKAGE", 90, 600.0);

        assertEquals(1, filtered.size());
        assertEquals("Arabica", filtered.get(0).getName());
    }

    @Test
    void findByQualityRange_returnsRangeResponses() {
        BeanCoffee bc = new BeanCoffee("Arabica", 500.0, 1.5, 2.5, PackagingType.PACKAGE, 95, "Ethiopia", "Medium");
        when(vanService.findByQualityRange(90, 100)).thenReturn(List.of(bc));

        List<CoffeeResponse> results = coffeeController.findByQualityRange(90, 100);

        assertEquals(1, results.size());
        assertEquals("Arabica", results.get(0).getName());
    }
}
