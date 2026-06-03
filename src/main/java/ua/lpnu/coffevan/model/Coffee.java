package ua.lpnu.coffevan.model;

import java.util.Objects;

/**
 * Abstract base class representing a coffee product.
 * Stores common attributes shared by all coffee types.
 */
public abstract class Coffee {

    private int id;
    private int vanId = 1;
    private String name;
    private double pricePerKg;
    private double weightKg;
    private double volumeLiters;
    private PackagingType packagingType;
    private int qualityScore;

    protected Coffee() {
    }

    protected Coffee(String name, double pricePerKg, double weightKg,
                     double volumeLiters, PackagingType packagingType, int qualityScore) {
        this.name = name;
        this.pricePerKg = pricePerKg;
        this.weightKg = weightKg;
        this.volumeLiters = volumeLiters;
        this.packagingType = packagingType;
        this.qualityScore = qualityScore;
    }

    public abstract String getCoffeeType();

    public double getTotalPrice() {
        return pricePerKg * weightKg;
    }

    public double getPriceToWeightRatio() {
        if (weightKg == 0) {
            return 0;
        }
        return pricePerKg / weightKg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVanId() {
        return vanId;
    }

    public void setVanId(int vanId) {
        this.vanId = vanId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPricePerKg() {
        return pricePerKg;
    }

    public void setPricePerKg(double pricePerKg) {
        this.pricePerKg = pricePerKg;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    public double getVolumeLiters() {
        return volumeLiters;
    }

    public void setVolumeLiters(double volumeLiters) {
        this.volumeLiters = volumeLiters;
    }

    public PackagingType getPackagingType() {
        return packagingType;
    }

    public void setPackagingType(PackagingType packagingType) {
        this.packagingType = packagingType;
    }

    public int getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(int qualityScore) {
        this.qualityScore = qualityScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coffee coffee)) return false;
        return id == coffee.id
                && Double.compare(coffee.pricePerKg, pricePerKg) == 0
                && Double.compare(coffee.weightKg, weightKg) == 0
                && qualityScore == coffee.qualityScore
                && Objects.equals(name, coffee.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, pricePerKg, weightKg, qualityScore);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %.2f грн/кг | %.3f кг | %.3f л | якість: %d",
                getCoffeeType(), name, pricePerKg, weightKg, volumeLiters, qualityScore);
    }
}
