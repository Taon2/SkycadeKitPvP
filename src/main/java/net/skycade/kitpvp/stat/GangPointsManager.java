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

public class GangPointsManager {
    private Map<String, Integer> points = new HashMap<>();

    public GangPointsManager() {
        loadPoints();
    }

    private void loadPoints() {
        Bukkit.getScheduler().runTaskAsynchronously(KitPvP.getInstance(), () -> {
            try (Connection connection = CoreSettings.getInstance().getConnection()) {
                PreparedStatement statement = connection.prepareStatement("SELECT `name`, `points` FROM skycade_kitpvp_gangs_points WHERE season = ?");
                statement.setString(1, CoreSettings.getInstance().getSeason());
                ResultSet set = statement.executeQuery();

                while (set.next()) {
                    String name = set.getString("name");
                    int amount = set.getInt("points");

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
                String sql = "INSERT INTO skycade_kitpvp_gangs_points (`name`, `points`, `season`) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE points = VALUES(points)";
                PreparedStatement statement = connection.prepareStatement(sql);
                try {
                    statement.setString(1, gangName);
                    statement.setInt(2, amount);
                    statement.setString(3, CoreSettings.getInstance().getSeason());
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

    public Map<String, Integer> getAllPoints() {
        return points;
    }

    public Integer getPoints(String gangName) {
        return points.get(gangName);
    }

    public void setPoints(String gangName, int amount) {
        points.replace(gangName, amount);
    }

    public void addPoints(String gangName, int amount) {
        if (points.containsKey(gangName)) {
            points.replace(gangName, points.get(gangName) + amount);
        } else {
            points.put(gangName, amount);
        }
    }

    public void resetAllPoints() {
        points.clear();
    }
}
