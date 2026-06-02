package ua.lpnu.coffevan.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.lpnu.coffevan.model.Van;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Repository for persisting Van capacity settings.
 */
public class VanSettingsRepository implements VanSettingsRepositoryInterface {

    private static final Logger logger = LogManager.getLogger(VanSettingsRepository.class);

    private final Connection connection;

    public VanSettingsRepository(Connection connection) {
        this.connection = connection;
    }

    public Van load() {
        String sql = "SELECT max_volume, max_budget FROM van_settings WHERE id=1";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new Van(rs.getDouble("max_volume"), rs.getDouble("max_budget"));
            }
        } catch (SQLException e) {
            logger.error("Error loading van settings", e);
        }
        return new Van(1000.0, 50000.0);
    }

    public void save(Van van) {
        String sql = "UPDATE van_settings SET max_volume=?, max_budget=? WHERE id=1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, van.getMaxVolumeLiters());
            ps.setDouble(2, van.getMaxBudget());
            ps.executeUpdate();
            logger.info("Van settings saved: volume={}, budget={}", van.getMaxVolumeLiters(), van.getMaxBudget());
        } catch (SQLException e) {
            logger.error("Error saving van settings", e);
        }
    }
}
