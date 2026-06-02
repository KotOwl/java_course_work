package ua.lpnu.coffevan.dto;

/**
 * Response payload representing a coffee item.
 */
public class CoffeeResponse {

    private int id;
    private String coffeeType;
    private String name;
    private double pricePerKg;
    private double weightKg;
    private double volumeLiters;
    private String packagingType;
    private int qualityScore;
    private double totalPrice;
    private double priceToWeightRatio;

    public CoffeeResponse() {
    }

    public CoffeeResponse(int id, String coffeeType, String name,
                          double pricePerKg, double weightKg, double volumeLiters,
                          String packagingType, int qualityScore,
                          double totalPrice, double priceToWeightRatio) {
        this.id = id;
        this.coffeeType = coffeeType;
        this.name = name;
        this.pricePerKg = pricePerKg;
        this.weightKg = weightKg;
        this.volumeLiters = volumeLiters;
        this.packagingType = packagingType;
        this.qualityScore = qualityScore;
        this.totalPrice = totalPrice;
        this.priceToWeightRatio = priceToWeightRatio;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

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

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public double getPriceToWeightRatio() { return priceToWeightRatio; }
    public void setPriceToWeightRatio(double priceToWeightRatio) { this.priceToWeightRatio = priceToWeightRatio; }

    @Override
    public String toString() {
        return String.format("[%s] %s | %.2f грн/кг | %.3f кг | %.3f л | якість: %d",
                coffeeType, name, pricePerKg, weightKg, volumeLiters, qualityScore);
    }
}
