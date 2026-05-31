package ua.lpnu.coffevan.model;

/**
 * Represents ground coffee.
 */
public class GroundCoffee extends Coffee {

    private String grindSize;

    public GroundCoffee() {
    }

    public GroundCoffee(String name, double pricePerKg, double weightKg,
                        double volumeLiters, PackagingType packagingType,
                        int qualityScore, String grindSize) {
        super(name, pricePerKg, weightKg, volumeLiters, packagingType, qualityScore);
        this.grindSize = grindSize;
    }

    @Override
    public String getCoffeeType() {
        return "Мелена";
    }

    public String getGrindSize() {
        return grindSize;
    }

    public void setGrindSize(String grindSize) {
        this.grindSize = grindSize;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Помол: %s", grindSize);
    }
}
