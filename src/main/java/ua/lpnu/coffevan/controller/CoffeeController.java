package ua.lpnu.coffevan.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.lpnu.coffevan.dto.CoffeeRequest;
import ua.lpnu.coffevan.dto.CoffeeResponse;
import ua.lpnu.coffevan.model.*;
import ua.lpnu.coffevan.service.VanService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Presentation layer controller for coffee-related operations.
 * Mediates between the UI layer and {@link VanService}, translating
 * domain objects to/from DTOs.
 */
public class CoffeeController {

    private static final Logger logger = LogManager.getLogger(CoffeeController.class);

    private final VanService vanService;

    public CoffeeController(VanService vanService) {
        this.vanService = vanService;
    }

    /**
     * Adds a coffee item to the van.
     *
     * @param request the coffee data
     * @return {@code true} if the item was added, {@code false} if capacity/budget exceeded
     */
    public boolean addCoffee(CoffeeRequest request) {
        Coffee coffee = toDomain(request);
        boolean result = vanService.addCoffee(coffee);
        logger.info("addCoffee '{}' -> {}", request.getName(), result ? "ok" : "rejected");
        return result;
    }

    /**
     * Returns all coffee items currently loaded in the van.
     */
    public List<CoffeeResponse> getAllCoffee() {
        return vanService.getAllCoffee().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Removes a coffee item by its id.
     */
    public void removeCoffee(int id) {
        vanService.getAllCoffee().stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .ifPresent(coffee -> {
                    vanService.removeCoffee(coffee);
                    logger.info("removeCoffee id={}", id);
                });
    }

    /**
     * Updates an existing coffee item.
     *
     * @param original the current domain object to replace
     * @param request  the new data
     */
    public void updateCoffee(Coffee original, CoffeeRequest request) {
        Coffee updated = toDomain(request);
        updated.setId(original.getId());
        vanService.updateCoffee(original, updated);
        logger.info("updateCoffee id={}", original.getId());
    }

    /**
     * Returns all coffee items sorted by price-to-weight ratio (ascending).
     */
    public List<CoffeeResponse> sortByPriceToWeightRatio() {
        return vanService.sortByPriceToWeightRatio().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Filters coffee by type, packaging, minimum quality, and maximum price.
     * Pass {@code null} / {@code 0} / {@code <=0} to skip a filter.
     */
    public List<CoffeeResponse> filterCoffee(String type, String packaging,
                                             int minQuality, double maxPrice) {
        return vanService.filterBy(type, packaging, minQuality, maxPrice).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Returns coffee items whose quality score is within the given range.
     */
    public List<CoffeeResponse> findByQualityRange(int minQuality, int maxQuality) {
        return vanService.findByQualityRange(minQuality, maxQuality).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // -----------------------------------------------------------------------
    // Mapping helpers
    // -----------------------------------------------------------------------

    private Coffee toDomain(CoffeeRequest req) {
        PackagingType packaging = PackagingType.valueOf(req.getPackagingType());
        return switch (req.getCoffeeType()) {
            case "Зернова" -> new BeanCoffee(
                    req.getName(), req.getPricePerKg(), req.getWeightKg(),
                    req.getVolumeLiters(), packaging, req.getQualityScore(),
                    req.getExtra1() != null ? req.getExtra1() : "",
                    req.getExtra2() != null ? req.getExtra2() : "");
            case "Мелена" -> new GroundCoffee(
                    req.getName(), req.getPricePerKg(), req.getWeightKg(),
                    req.getVolumeLiters(), packaging, req.getQualityScore(),
                    req.getExtra1() != null ? req.getExtra1() : "");
            case "Розчинна (банка)" -> new InstantCoffeeJar(
                    req.getName(), req.getPricePerKg(), req.getWeightKg(),
                    req.getVolumeLiters(), req.getQualityScore(),
                    req.getExtra1() != null ? Integer.parseInt(req.getExtra1()) : 0);
            case "Розчинна (пакетик)" -> new InstantCoffeeSachet(
                    req.getName(), req.getPricePerKg(), req.getWeightKg(),
                    req.getVolumeLiters(), req.getQualityScore(),
                    req.getExtra1() != null ? Integer.parseInt(req.getExtra1()) : 0);
            default -> throw new IllegalArgumentException("Unknown coffee type: " + req.getCoffeeType());
        };
    }

    private CoffeeResponse toResponse(Coffee coffee) {
        return new CoffeeResponse(
                coffee.getId(),
                coffee.getCoffeeType(),
                coffee.getName(),
                coffee.getPricePerKg(),
                coffee.getWeightKg(),
                coffee.getVolumeLiters(),
                coffee.getPackagingType().name(),
                coffee.getQualityScore(),
                coffee.getTotalPrice(),
                coffee.getPriceToWeightRatio()
        );
    }
}
