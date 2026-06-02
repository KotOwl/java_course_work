package ua.lpnu.coffevan.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.lpnu.coffevan.dto.VanResponse;
import ua.lpnu.coffevan.model.Van;
import ua.lpnu.coffevan.service.VanService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VanControllerTest {

    @Mock
    private VanService vanService;

    @InjectMocks
    private VanController vanController;

    @Test
    void getVanStatus_returnsMappedVanResponse() {
        Van van = new Van(2000.0, 80000.0);
        // Let's reflection-set or simulate values (usedVolume and spentBudget) by stubbing or checking
        // But since Van is a simple POJO and used volume/spent budget are computed from loaded items in Service,
        // we can stub vanService.getVan() returning a mock/real Van.
        van.setMaxVolumeLiters(2000.0);
        van.setMaxBudget(80000.0);

        when(vanService.getVan()).thenReturn(van);

        VanResponse status = vanController.getVanStatus();

        assertEquals(2000.0, status.getMaxVolumeLiters());
        assertEquals(80000.0, status.getMaxBudget());
        assertEquals(0.0, status.getUsedVolumeLiters());
        assertEquals(0.0, status.getSpentBudget());
    }

    @Test
    void updateVanSettings_delegatesToService() {
        vanController.updateVanSettings(1500.0, 50000.0);
        verify(vanService).updateVanSettings(1500.0, 50000.0);
    }

    @Test
    void clearVan_delegatesToService() {
        vanController.clearVan();
        verify(vanService).clearVan();
    }

    @Test
    void getTotalVolume_delegatesToService() {
        when(vanService.getTotalVolume()).thenReturn(150.5);
        assertEquals(150.5, vanController.getTotalVolume());
    }

    @Test
    void getTotalBudget_delegatesToService() {
        when(vanService.getTotalBudget()).thenReturn(5400.25);
        assertEquals(5400.25, vanController.getTotalBudget());
    }
}
