package net.skycade.kitpvp.stat;

import net.skycade.SkycadeCore.CoreSettings;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.stat.leaderboards.stats.StatGangsPoints;
import net.skycade.skycadeleaderboards.SkycadeLeaderboards;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GangPointsManager {
    private Map<String, Long> points = new HashMap<>();

    public GangPointsManager() {
        loadPoints();
    }

    private void loadPoints() {
        Bukkit.getScheduler().runTaskAsynchronously(KitPvP.getInstance(), () -> {
            try (Connection connection = CoreSettings.getInstance().getConnection()) {
                PreparedStatement statement = connection.prepareStatement("SELECT `name`, `points` FROM skycade_kitpvp_gangs_points WHERE instance = ? AND season = ?");
                statement.setString(1, CoreSettings.getInstance().getThisInstance());
                statement.setString(2, CoreSettings.getInstance().getSeason());
                ResultSet set = statement.executeQuery();

                while (set.next()) {
                    String name = set.getString("name");
                    Long amount = set.getLong("points");

                    points.put(name, amount);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // load gang points stat here so it doesn't register 0 all the time
            SkycadeLeaderboards.getAPI().register(KitPvP.getInstance().getName(), StatGangsPoints.getInstance());

        });
    }

    public void save() {
        points.forEach((gangName, amount) -> {
            try (Connection connection = CoreSettings.getInstance().getConnection()) {
                String sql = "INSERT INTO skycade_kitpvp_gangs_points (`name`, `points`, `instance`, `season`) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE points = VALUES(points)";
                PreparedStatement statement = connection.prepareStatement(sql);
                try {
                    statement.setString(1, gangName);
                    statement.setLong(2, amount);
                    statement.setString(3, CoreSettings.getInstance().getThisInstance());
                    statement.setString(4, CoreSettings.getInstance().getSeason());
                    statement.addBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                statement.executeBatch();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public Map<String, Long> getAllPoints() {
        return points;
    }

    public Set<String> getAllGangNames() {
        return points.keySet();
    }

    public Long getPoints(String gangName) {
        if (!points.containsKey(gangName))
            return 0L;
        else
            return (points.get(gangName) / 100);
    }

    public void setPoints(String gangName, Long amount) {
        points.replace(gangName, amount);
    }

    public void addPoints(String gangName, Long amount) {
        if (points.containsKey(gangName)) {
            points.replace(gangName, points.get(gangName) + amount);
        } else {
            points.put(gangName, amount);
        }
    }
    public void removePoints(String gangName, Long amount) {
        if (points.containsKey(gangName)) {
            points.replace(gangName, points.get(gangName) - amount);
        } else {
            points.put(gangName, amount);
        }
    }

    public void resetAllPoints() {
        Map<String, Long> newPoints = new HashMap<>();

        points.forEach((gang, points) -> {
            newPoints.put(gang, 0L);
        });

        points = newPoints;
    }
}
