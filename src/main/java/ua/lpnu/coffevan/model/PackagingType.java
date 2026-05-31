package ua.lpnu.coffevan.model;

public enum PackagingType {
    BAG("Пакетик"),
    JAR("Банка"),
    PACKAGE("Пакет"),
    BOX("Коробка");

    private final String displayName;

    PackagingType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
