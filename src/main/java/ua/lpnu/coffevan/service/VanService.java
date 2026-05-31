package ua.lpnu.coffevan.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.lpnu.coffevan.dao.CoffeeDao;
import ua.lpnu.coffevan.dao.VanSettingsDaoInterface;
import ua.lpnu.coffevan.model.Coffee;
import ua.lpnu.coffevan.model.Van;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Business logic layer for the Coffee Van application.
 * Handles loading, sorting, searching and CRUD operations.
 */
public class VanService {

    private static final Logger logger = LogManager.getLogger(VanService.class);

    private final CoffeeDao coffeeDao;
    private final VanSettingsDaoInterface vanSettingsDao;
    private Van van;

    public VanService(CoffeeDao coffeeDao, VanSettingsDaoInterface vanSettingsDao) {
        this.coffeeDao = coffeeDao;
        this.vanSettingsDao = vanSettingsDao;
        this.van = vanSettingsDao.load();
        recalculateVanUsage();
    }

    private void recalculateVanUsage() {
        double totalVolume = 0;
        double totalBudget = 0;
        for (Coffee c : coffeeDao.findAll()) {
            totalVolume += c.getVolumeLiters();
            totalBudget += c.getTotalPrice();
        }
        van.setUsedVolumeLiters(totalVolume);
        van.setSpentBudget(totalBudget);
    }

    public boolean addCoffee(Coffee coffee) {
        if (!van.canLoad(coffee)) {
            logger.warn("Cannot load coffee '{}': van capacity or budget exceeded", coffee.getName());
            return false;
        }
        coffeeDao.save(coffee);
        van.load(coffee);
        logger.info("Added coffee '{}' to van", coffee.getName());
        return true;
    }

    public void removeCoffee(Coffee coffee) {
        coffeeDao.delete(coffee.getId());
        van.unload(coffee);
        logger.info("Removed coffee id={} from van", coffee.getId());
    }

    public void updateCoffee(Coffee original, Coffee updated) {
        van.unload(original);
        coffeeDao.update(updated);
        van.load(updated);
        logger.info("Updated coffee id={}", updated.getId());
    }

    public List<Coffee> getAllCoffee() {
        return coffeeDao.findAll();
    }

    public List<Coffee> sortByPriceToWeightRatio() {
        List<Coffee> sorted = coffeeDao.findAll().stream()
                .sorted(Comparator.comparingDouble(Coffee::getPriceToWeightRatio))
                .collect(Collectors.toList());
        logger.info("Sorted {} items by price/weight ratio", sorted.size());
        return sorted;
    }

    public List<Coffee> findByQualityRange(int minQuality, int maxQuality) {
        List<Coffee> result = coffeeDao.findAll().stream()
                .filter(c -> c.getQualityScore() >= minQuality && c.getQualityScore() <= maxQuality)
                .collect(Collectors.toList());
        logger.info("Found {} items in quality range [{}, {}]", result.size(), minQuality, maxQuality);
        return result;
    }

    public List<Coffee> filterBy(String type, String packaging, int minQuality, double maxPrice) {
        return coffeeDao.findAll().stream()
                .filter(c -> type == null       || c.getCoffeeType().equals(type))
                .filter(c -> packaging == null  || c.getPackagingType().getDisplayName().equals(packaging))
                .filter(c -> c.getQualityScore() >= minQuality)
                .filter(c -> maxPrice <= 0      || c.getPricePerKg() <= maxPrice)
                .collect(Collectors.toList());
    }

    public void clearVan() {
        coffeeDao.deleteAll();
        van.setUsedVolumeLiters(0);
        van.setSpentBudget(0);
        logger.info("Van cleared");
    }

    public Van getVan() {
        return van;
    }

    public void updateVanSettings(double maxVolume, double maxBudget) {
        van.setMaxVolumeLiters(maxVolume);
        van.setMaxBudget(maxBudget);
        vanSettingsDao.save(van);
        logger.info("Van settings updated: volume={}, budget={}", maxVolume, maxBudget);
    }

    public double getTotalVolume() {
        return van.getUsedVolumeLiters();
    }

    public double getTotalBudget() {
        return van.getSpentBudget();
    }
}
