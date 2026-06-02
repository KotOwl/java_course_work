package ua.lpnu.coffevan.dto;

/**
 * Request payload for creating or updating a coffee item.
 */
public class CoffeeRequest {

    private String coffeeType;
    private String name;
    private double pricePerKg;
    private double weightKg;
    private double volumeLiters;
    private String packagingType;
    private int qualityScore;
    /** extra1: origin (Bean), grindSize (Ground), granules (Jar), sachetsCount (Sachet) */
    private String extra1;
    /** extra2: roastLevel (Bean only) */
    private String extra2;

    public CoffeeRequest() {
    }

    public CoffeeRequest(String coffeeType, String name, double pricePerKg,
                         double weightKg, double volumeLiters,
                         String packagingType, int qualityScore,
                         String extra1, String extra2) {
        this.coffeeType = coffeeType;
        this.name = name;
        this.pricePerKg = pricePerKg;
        this.weightKg = weightKg;
        this.volumeLiters = volumeLiters;
        this.packagingType = packagingType;
        this.qualityScore = qualityScore;
        this.extra1 = extra1;
        this.extra2 = extra2;
    }

    public String getCoffeeType() { return coffeeType; }
    public void setCoffeeType(String coffeeType) { this.coffeeType = coffeeType; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPricePerKg() { return pricePerKg; }
    public void setPricePerKg(double pricePerKg) { this.pricePerKg = pricePerKg; }

    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }

    public double getVolumeLiters() { return volumeLiters; }
    public void setVolumeLiters(double volumeLiters) { this.volumeLiters = volumeLiters; }

    public String getPackagingType() { return packagingType; }
    public void setPackagingType(String packagingType) { this.packagingType = packagingType; }

    public int getQualityScore() { return qualityScore; }
    public void setQualityScore(int qualityScore) { this.qualityScore = qualityScore; }

    public String getExtra1() { return extra1; }
    public void setExtra1(String extra1) { this.extra1 = extra1; }

    public String getExtra2() { return extra2; }
    public void setExtra2(String extra2) { this.extra2 = extra2; }
}
