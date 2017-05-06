package net.skycade.kitpvp.stat;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class KitPvPDB {

    private static KitPvPDB instance;
    private final String kitPvPTable;
    private final String previousNamesTable;
    private final String propertiesTable;
    private Connection connection;

    private KitPvPDB() {
        String host = KitPvP.getInstance().getConfig().getString("database.host");
        System.out.printf(host + "\n");
        int port = KitPvP.getInstance().getConfig().getInt("database.port");
        String databaseName = KitPvP.getInstance().getConfig().getString("database.name");
        String username = KitPvP.getInstance().getConfig().getString("database.username");
        String password = KitPvP.getInstance().getConfig().getString("database.password");
        kitPvPTable = KitPvP.getInstance().getConfig().getString("database.kitpvp-table");
        previousNamesTable = KitPvP.getInstance().getConfig().getString("database.previous-names-table");
        propertiesTable = KitPvP.getInstance().getConfig().getString("database.properties-table");

        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + databaseName + "?useSSL=true", username, password);
        } catch (SQLException e) {
            KitPvP.getInstance().getLogger().log(Level.SEVERE, "Coulnd't connect to mysql.", e);
        }
    }

    public Member getMemberData(UUID uuid) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + kitPvPTable + " WHERE UUID = ?");
            statement.setString(1, uuid.toString());
            statement.executeQuery();
            ResultSet result = statement.getResultSet();
            if (!result.next()) return null;

            Member member = new Member(uuid, result.getString("PlayerName"));
            member.setDeaths(result.getInt("Deaths"));
            member.setHighestStreak(result.getInt("HighestStreak"));
            JSONArray permissionsJson = (JSONArray) new JSONParser().parse(result.getString("Permissions"));
            member.setPermissions(permissionsJson);

            PreparedStatement previousNamesStatement = connection.prepareStatement("SELECT * FROM " + previousNamesTable + " WHERE UUID = ?");
            previousNamesStatement.setString(1, uuid.toString());
            ResultSet previousNamesResults = previousNamesStatement.executeQuery();
            while (previousNamesResults.next()) {
                member.addPreviousName(previousNamesResults.getString("PreviousName"));
            }

            PreparedStatement propertiesStatement = connection.prepareStatement("SELECT * FROM " + propertiesTable + " WHERE UUID = ?");
            propertiesStatement.setString(1, uuid.toString());
            ResultSet propertiesResults = propertiesStatement.executeQuery();
            while (propertiesResults.next()) {
                String propertyKey = propertiesResults.getString("PropertyKey");
                String propertyValue = propertiesResults.getString("PropertyValue");
                try {
                    member.getProperties().put(propertyKey, Integer.parseInt(propertyValue));
                } catch (NumberFormatException e1) {
                    try {
                        JSONObject jsonObject = ((JSONObject) new JSONParser().parse(propertyValue));
                        member.getProperties().put(propertyKey, new Document(jsonObject));
                    } catch (ParseException | ClassCastException e) {
                        member.getProperties().put(propertyKey, String.valueOf(propertyValue));
                    }
                }
            }

            return member;
        } catch (SQLException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet getAllMembers() {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery("SELECT * FROM " + kitPvPTable);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UUID getUUIDForName(String playerName) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT UUID FROM " + previousNamesTable + " WHERE PlayerName = ?");
            statement.setString(1, playerName);
            ResultSet results = statement.executeQuery();
            if (results.next())
                return UUID.fromString(results.getString(1));
            else return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public void setMemberData(UUID uuid, String name, List<String> previousNames, List<String> permissions, Integer kills, Integer highestStreak, Integer death, Map<String, Object> properties) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO " + kitPvPTable + " (UUID, PlayerName, Permissions, Kills, HighestStreak, Deaths) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE PlayerName = VALUES(PlayerName), Permissions = VALUES(Permissions), Kills = VALUES(Kills), HighestStreak = VALUES(HighestStreak), Deaths = VALUES(Deaths)");
            statement.setString(1, uuid.toString());
            statement.setString(2, name);

            JSONArray permissionsJson = new JSONArray();
            permissionsJson.addAll(permissions);
            statement.setString(3, permissionsJson.toJSONString());

            statement.setInt(4, kills);
            statement.setInt(5, highestStreak);
            statement.setInt(6, death);

            statement.executeUpdate();

            for (String previousName : previousNames) {
                PreparedStatement namesStatement = connection.prepareStatement("INSERT INTO " + previousNamesTable + " (PreviousName, UUID) VALUES (?, ?) ON DUPLICATE KEY UPDATE UUID = VALUES(UUID)");
                namesStatement.setString(1, previousName);
                namesStatement.setString(2, uuid.toString());
                namesStatement.executeUpdate();
            }

            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                PreparedStatement propertiesQuery = connection.prepareStatement("SELECT * FROM " + propertiesTable + " WHERE UUID = ? AND PropertyKey = ?");
                propertiesQuery.setString(1, uuid.toString());
                propertiesQuery.setString(2, entry.getKey());
                ResultSet results = propertiesQuery.executeQuery();
                if (results.next()) {
                    PreparedStatement propertiesStatement = connection.prepareStatement("UPDATE " + propertiesTable + " SET PropertyValue = ? WHERE UUID = ? AND PropertyKey = ?");
                    if (entry.getValue() instanceof Document) {
                        propertiesStatement.setString(1, ((Document) entry.getValue()).toJson());
                    } else {
                        propertiesStatement.setString(1, entry.getValue().toString());
                    }
                    propertiesStatement.setString(2, uuid.toString());
                    propertiesStatement.setString(3, entry.getKey());
                    propertiesStatement.executeUpdate();
                } else {
                    PreparedStatement propertiesStatement = connection.prepareStatement("INSERT INTO " + propertiesTable + " (UUID, PropertyKey, PropertyValue) VALUES (?, ?, ?)");
                    propertiesStatement.setString(1, uuid.toString());
                    propertiesStatement.setString(2, entry.getKey());
                    if (entry.getValue() instanceof Document) {
                        propertiesStatement.setString(3, ((Document) entry.getValue()).toJson());
                    } else {
                        propertiesStatement.setString(3, entry.getValue().toString());
                    }
                    propertiesStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static KitPvPDB getInstance() {
        if (instance == null)
            instance = new KitPvPDB();
        return instance;
    }
}
