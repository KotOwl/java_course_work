package ua.lpnu.coffevan.repository;

import ua.lpnu.coffevan.model.Van;

/**
 * Contract for loading and persisting Van settings.
 */
public interface VanSettingsRepositoryInterface {

    Van load();

    void save(Van van);
}
