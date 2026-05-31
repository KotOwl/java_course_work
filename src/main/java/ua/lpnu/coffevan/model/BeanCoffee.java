package ua.lpnu.coffevan.model;

/**
 * Represents whole-bean coffee.
 */
public class BeanCoffee extends Coffee {

    private String origin;
    private String roastLevel;

    public BeanCoffee() {
    }

    public BeanCoffee(String name, double pricePerKg, double weightKg,
                      double volumeLiters, PackagingType packagingType,
                      int qualityScore, String origin, String roastLevel) {
        super(name, pricePerKg, weightKg, volumeLiters, packagingType, qualityScore);
        this.origin = origin;
        this.roastLevel = roastLevel;
    }

    @Override
    public String getCoffeeType() {
        return "Зернова";
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getRoastLevel() {
        return roastLevel;
    }

    public void setRoastLevel(String roastLevel) {
        this.roastLevel = roastLevel;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Походження: %s | Обсмажування: %s", origin, roastLevel);
    }
}
