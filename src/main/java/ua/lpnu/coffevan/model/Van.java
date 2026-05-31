package ua.lpnu.coffevan.model;

/**
 * Represents the coffee van with a fixed volume capacity and a budget limit.
 */
public class Van {

    private double maxVolumeLiters;
    private double maxBudget;
    private double usedVolumeLiters;
    private double spentBudget;

    public Van(double maxVolumeLiters, double maxBudget) {
        this.maxVolumeLiters = maxVolumeLiters;
        this.maxBudget = maxBudget;
        this.usedVolumeLiters = 0;
        this.spentBudget = 0;
    }

    public boolean canLoad(Coffee coffee) {
        double newVolume = usedVolumeLiters + coffee.getVolumeLiters();
        double newBudget = spentBudget + coffee.getTotalPrice();
        return newVolume <= maxVolumeLiters && newBudget <= maxBudget;
    }

    public void load(Coffee coffee) {
        usedVolumeLiters += coffee.getVolumeLiters();
        spentBudget += coffee.getTotalPrice();
    }

    public void unload(Coffee coffee) {
        usedVolumeLiters -= coffee.getVolumeLiters();
        spentBudget -= coffee.getTotalPrice();
    }

    public double getRemainingVolume() {
        return maxVolumeLiters - usedVolumeLiters;
    }

    public double getRemainingBudget() {
        return maxBudget - spentBudget;
    }

    public double getMaxVolumeLiters() {
        return maxVolumeLiters;
    }

    public void setMaxVolumeLiters(double maxVolumeLiters) {
        this.maxVolumeLiters = maxVolumeLiters;
    }

    public double getMaxBudget() {
        return maxBudget;
    }

    public void setMaxBudget(double maxBudget) {
        this.maxBudget = maxBudget;
    }

    public double getUsedVolumeLiters() {
        return usedVolumeLiters;
    }

    public void setUsedVolumeLiters(double usedVolumeLiters) {
        this.usedVolumeLiters = usedVolumeLiters;
    }

    public double getSpentBudget() {
        return spentBudget;
    }

    public void setSpentBudget(double spentBudget) {
        this.spentBudget = spentBudget;
    }
}
