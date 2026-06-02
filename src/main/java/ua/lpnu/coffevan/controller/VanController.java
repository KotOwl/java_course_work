package ua.lpnu.coffevan.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.lpnu.coffevan.dto.VanResponse;
import ua.lpnu.coffevan.model.Van;
import ua.lpnu.coffevan.service.VanService;

/**
 * Presentation layer controller for van-related operations.
 * Mediates between the UI layer and {@link VanService}.
 */
public class VanController {

    private static final Logger logger = LogManager.getLogger(VanController.class);

    private final VanService vanService;

    public VanController(VanService vanService) {
        this.vanService = vanService;
    }

    /**
     * Returns the current state of the van as a DTO.
     */
    public VanResponse getVanStatus() {
        Van van = vanService.getVan();
        return new VanResponse(
                van.getMaxVolumeLiters(),
                van.getMaxBudget(),
                van.getUsedVolumeLiters(),
                van.getSpentBudget()
        );
    }

    /**
     * Updates the van's capacity and budget settings.
     *
     * @param maxVolume new maximum volume in litres
     * @param maxBudget new maximum budget in UAH
     */
    public void updateVanSettings(double maxVolume, double maxBudget) {
        vanService.updateVanSettings(maxVolume, maxBudget);
        logger.info("Van settings updated via controller: volume={}, budget={}", maxVolume, maxBudget);
    }

    /**
     * Removes all coffee items from the van.
     */
    public void clearVan() {
        vanService.clearVan();
        logger.info("Van cleared via controller");
    }

    /**
     * Returns total used volume (litres).
     */
    public double getTotalVolume() {
        return vanService.getTotalVolume();
    }

    /**
     * Returns total spent budget (UAH).
     */
    public double getTotalBudget() {
        return vanService.getTotalBudget();
    }
}
