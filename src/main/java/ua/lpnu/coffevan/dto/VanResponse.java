package ua.lpnu.coffevan.dto;

/**
 * Response payload representing the current state of the coffee van.
 */
public class VanResponse {

    private double maxVolumeLiters;
    private double maxBudget;
    private double usedVolumeLiters;
    private double spentBudget;
    private double freeVolumeLiters;
    private double remainingBudget;

    public VanResponse() {
    }

    public VanResponse(double maxVolumeLiters, double maxBudget,
                       double usedVolumeLiters, double spentBudget) {
        this.maxVolumeLiters = maxVolumeLiters;
        this.maxBudget = maxBudget;
        this.usedVolumeLiters = usedVolumeLiters;
        this.spentBudget = spentBudget;
        this.freeVolumeLiters = maxVolumeLiters - usedVolumeLiters;
        this.remainingBudget = maxBudget - spentBudget;
    }

    public double getMaxVolumeLiters() { return maxVolumeLiters; }
    public void setMaxVolumeLiters(double maxVolumeLiters) {
        this.maxVolumeLiters = maxVolumeLiters;
        this.freeVolumeLiters = maxVolumeLiters - usedVolumeLiters;
    }

    public double getMaxBudget() { return maxBudget; }
    public void setMaxBudget(double maxBudget) {
        this.maxBudget = maxBudget;
        this.remainingBudget = maxBudget - spentBudget;
    }

    public double getUsedVolumeLiters() { return usedVolumeLiters; }
    public void setUsedVolumeLiters(double usedVolumeLiters) {
        this.usedVolumeLiters = usedVolumeLiters;
        this.freeVolumeLiters = maxVolumeLiters - usedVolumeLiters;
    }

    public double getSpentBudget() { return spentBudget; }
    public void setSpentBudget(double spentBudget) {
        this.spentBudget = spentBudget;
        this.remainingBudget = maxBudget - spentBudget;
    }

    public double getFreeVolumeLiters() { return freeVolumeLiters; }

    public double getRemainingBudget() { return remainingBudget; }

    @Override
    public String toString() {
        return String.format("Van[volume=%.1f/%.1f л, budget=%.2f/%.2f грн]",
                usedVolumeLiters, maxVolumeLiters, spentBudget, maxBudget);
    }
}
