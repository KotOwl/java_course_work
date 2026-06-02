package ua.lpnu.coffevan.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.lpnu.coffevan.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * SQLite implementation of {@link CoffeeRepository}.
 *
 * <p>Column mapping for {@code extra1}, {@code extra2}, {@code extra3}:
 * <ul>
 *   <li>BeanCoffee      – extra1=origin, extra2=roastLevel</li>
 *   <li>GroundCoffee    – extra1=grindSize</li>
 *   <li>InstantCoffeeJar    – extra1=granules (int as text)</li>
 *   <li>InstantCoffeeSachet – extra1=sachetsCount (int as text)</li>
 * </ul>
 */
public class CoffeeRepositoryImpl implements CoffeeRepository {

    private static final Logger logger = LogManager.getLogger(CoffeeRepositoryImpl.class);

    private final Connection connection;

    public CoffeeRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public int save(Coffee coffee) {
        String sql = """
                INSERT INTO coffee
                    (coffee_type, name, price_per_kg, weight_kg, volume_liters,
                     packaging, quality_score, extra1, extra2, extra3)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fillCommonFields(ps, coffee);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int id = keys.getInt(1);
                coffee.setId(id);
                logger.info("Saved coffee with id={}: {}", id, coffee.getName());
                return id;
            }
        } catch (SQLException e) {
            logger.error("Error saving coffee: {}", coffee.getName(), e);
        }
        return -1;
    }

    @Override
    public Optional<Coffee> findById(int id) {
        String sql = "SELECT * FROM coffee WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding coffee by id={}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Coffee> findAll() {
        List<Coffee> list = new ArrayList<>();
        String sql = "SELECT * FROM coffee";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            logger.debug("Loaded {} coffee items from DB", list.size());
        } catch (SQLException e) {
            logger.error("Error loading all coffee items", e);
        }
        return list;
    }

    @Override
    public void update(Coffee coffee) {
        String sql = """
                UPDATE coffee SET
                    coffee_type=?, name=?, price_per_kg=?, weight_kg=?, volume_liters=?,
                    packaging=?, quality_score=?, extra1=?, extra2=?, extra3=?
                WHERE id=?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            fillCommonFields(ps, coffee);
            ps.setInt(11, coffee.getId());
            ps.executeUpdate();
            logger.info("Updated coffee id={}: {}", coffee.getId(), coffee.getName());
        } catch (SQLException e) {
            logger.error("Error updating coffee id={}", coffee.getId(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM coffee WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            logger.info("Deleted coffee id={}", id);
        } catch (SQLException e) {
            logger.error("Error deleting coffee id={}", id, e);
        }
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM coffee";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
            logger.info("All coffee items deleted");
        } catch (SQLException e) {
            logger.error("Error deleting all coffee items", e);
        }
    }

    private void fillCommonFields(PreparedStatement ps, Coffee coffee) throws SQLException {
        ps.setString(1, coffee.getCoffeeType());
        ps.setString(2, coffee.getName());
        ps.setDouble(3, coffee.getPricePerKg());
        ps.setDouble(4, coffee.getWeightKg());
        ps.setDouble(5, coffee.getVolumeLiters());
        ps.setString(6, coffee.getPackagingType().name());
        ps.setInt(7, coffee.getQualityScore());

        if (coffee instanceof BeanCoffee bc) {
            ps.setString(8, bc.getOrigin());
            ps.setString(9, bc.getRoastLevel());
            ps.setNull(10, Types.VARCHAR);
        } else if (coffee instanceof GroundCoffee gc) {
            ps.setString(8, gc.getGrindSize());
            ps.setNull(9, Types.VARCHAR);
            ps.setNull(10, Types.VARCHAR);
        } else if (coffee instanceof InstantCoffeeJar ij) {
            ps.setString(8, String.valueOf(ij.getGranules()));
            ps.setNull(9, Types.VARCHAR);
            ps.setNull(10, Types.VARCHAR);
        } else if (coffee instanceof InstantCoffeeSachet is) {
            ps.setString(8, String.valueOf(is.getSachetsCount()));
            ps.setNull(9, Types.VARCHAR);
            ps.setNull(10, Types.VARCHAR);
        } else {
            ps.setNull(8, Types.VARCHAR);
            ps.setNull(9, Types.VARCHAR);
            ps.setNull(10, Types.VARCHAR);
        }
    }

    private Coffee mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String type = rs.getString("coffee_type");
        String name = rs.getString("name");
        double pricePerKg = rs.getDouble("price_per_kg");
        double weightKg = rs.getDouble("weight_kg");
        double volumeLiters = rs.getDouble("volume_liters");
        PackagingType packaging = PackagingType.valueOf(rs.getString("packaging"));
        int quality = rs.getInt("quality_score");
        String extra1 = rs.getString("extra1");
        String extra2 = rs.getString("extra2");

        Coffee coffee = switch (type) {
            case "Зернова" -> new BeanCoffee(name, pricePerKg, weightKg, volumeLiters,
                    packaging, quality,
                    extra1 != null ? extra1 : "",
                    extra2 != null ? extra2 : "");
            case "Мелена" -> new GroundCoffee(name, pricePerKg, weightKg, volumeLiters,
                    packaging, quality,
                    extra1 != null ? extra1 : "");
            case "Розчинна (банка)" -> new InstantCoffeeJar(name, pricePerKg, weightKg, volumeLiters,
                    quality, extra1 != null ? Integer.parseInt(extra1) : 0);
            case "Розчинна (пакетик)" -> new InstantCoffeeSachet(name, pricePerKg, weightKg, volumeLiters,
                    quality, extra1 != null ? Integer.parseInt(extra1) : 0);
            default -> throw new IllegalStateException("Unknown coffee type: " + type);
        };

        coffee.setId(id);
        return coffee;
    }
}
