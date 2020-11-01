package net.skycade.kitpvp.stat.leaderboards.member;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@SuppressWarnings({"WeakerAccess", "unused"})
public class StatsMember {

    private UUID uuid;
    private String name;
    private Integer kills;
    private Long coins;
    private Integer deaths;
    private Integer highestStreak;

    public StatsMember(String name) {
        this.name = name;
    }

    public StatsMember(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(getUUID());
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Long getCoins() {
        return coins;
    }

    public Integer getDeaths() {
        return deaths;
    }

    public Integer getHighestStreak() {
        return highestStreak;
    }

    public Integer getKills() {
        return kills;
    }

    // setters

    public void setName(String name) {
        this.name = name;
    }

    public void setCoins(Long coins) {
        this.coins = coins;
    }

    public void setDeaths(Integer deaths) {
        this.deaths = deaths;
    }

    public void setHighestStreak(Integer highestStreak) {
        this.highestStreak = highestStreak;
    }

    public void setKills(Integer kills) {
        this.kills = kills;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }


    // etc

    public int hashCode() {
        return getUUID().hashCode();
    }

}
