package net.skycade.kitpvp.coreclasses.member;

import net.skycade.kitpvp.KitPvP;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Member {

    private UUID uuid;
    private String name;
    private UUID lastKiller = null;

    public Member(String name) {
        this.name = name;
    }

    public Member(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(getUUID());
    }

    public void update() {
        MemberManager.getInstance().update(this);
    }

    @Override
    public int hashCode() {
        return getUUID().hashCode();
    }

    @Override
    public String toString() {
        return getUUID().toString();
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Member && ((Member) object).getUUID().equals(getUUID());
    }

    public boolean isConsole() {
        return this instanceof ConsoleCommandSender;
    }

    public Integer getKills() {
        return KitPvP.getInstance().getStats(this).getKills();
    }

    public Integer getHighestStreak() {
        return KitPvP.getInstance().getStats(this).getHighestStreak();
    }

    public Integer getDeaths() {
        return KitPvP.getInstance().getStats(this).getDeaths();
    }

    public UUID getLastKiller() {
        return lastKiller;
    }

    public void setLastKiller(UUID lastKiller) {
        this.lastKiller = lastKiller;
    }

    public boolean hasSoupDiscount() {
        return KitPvP.getInstance().getEventShopManager().isActive(getPlayer(), KitPvP.getInstance().getEventShopManager().getTypeFromString("Permanently Reduce Soup Price"));
    }

    public boolean hasRefreshKitCooldownReduction() {
        return KitPvP.getInstance().getEventShopManager().isActive(getPlayer(), KitPvP.getInstance().getEventShopManager().getTypeFromString("Permanently Reduce RefreshKit Cooldown"));
    }
}
