package ua.lpnu.coffevan.model;

/**
 * Represents instant coffee sold in sachets (single-serve bags).
 */
public class InstantCoffeeSachet extends Coffee {

    private int sachetsCount;

    public InstantCoffeeSachet() {
    }

    public InstantCoffeeSachet(String name, double pricePerKg, double weightKg,
                               double volumeLiters, int qualityScore, int sachetsCount) {
        super(name, pricePerKg, weightKg, volumeLiters, PackagingType.BAG, qualityScore);
        this.sachetsCount = sachetsCount;
    }

    @Override
    public String getCoffeeType() {
        return "Розчинна (пакетик)";
    }

    public int getSachetsCount() {
        return sachetsCount;
    }

    public void setSachetsCount(int sachetsCount) {
        this.sachetsCount = sachetsCount;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Кількість пакетиків: %d", sachetsCount);
    }
}
