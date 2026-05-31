package ua.lpnu.coffevan.model;

/**
 * Represents instant coffee sold in jars.
 */
public class InstantCoffeeJar extends Coffee {

    private int granules;

    public InstantCoffeeJar() {
    }

    public InstantCoffeeJar(String name, double pricePerKg, double weightKg,
                            double volumeLiters, int qualityScore, int granules) {
        super(name, pricePerKg, weightKg, volumeLiters, PackagingType.JAR, qualityScore);
        this.granules = granules;
    }

    @Override
    public String getCoffeeType() {
        return "Розчинна (банка)";
    }

    public int getGranules() {
        return granules;
    }

    public void setGranules(int granules) {
        this.granules = granules;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Гранульована: %s", granules > 0 ? "так" : "ні");
    }
}
