package ua.lpnu.coffevan.dao;

import ua.lpnu.coffevan.model.*;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Coffee entities.
 */
public interface CoffeeDao {

    int save(Coffee coffee);

    Optional<Coffee> findById(int id);

    List<Coffee> findAll();

    void update(Coffee coffee);

    void delete(int id);

    void deleteAll();
}
