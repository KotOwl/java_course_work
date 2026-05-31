package ua.lpnu.coffevan.dao;

import ua.lpnu.coffevan.model.Van;

/**
 * Contract for loading and persisting Van settings.
 */
public interface VanSettingsDaoInterface {

    Van load();

    void save(Van van);
}
