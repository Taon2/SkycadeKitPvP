package net.skycade.kitpvp.listeners.player;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.kit.kits.*;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDamageListener implements Listener {

    private final KitPvP plugin;
    private final Map<Entity, UUID> lastProjLaunch = new HashMap<>();
    private final Map<UUID, Entity> lastDamagerMap = new HashMap<>();
    private final Map<UUID, HashMap<UUID, Double>> killAssist = new HashMap<>();
    private final Map<UUID, HashMap<UUID, Integer>> samePlayerKill = new HashMap<>();

    public PlayerDamageListener(KitPvP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void on(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.FALL)
            e.setCancelled(true);
    }

    @EventHandler
    public void on(EntityDamageByEntityEvent e) {
        if (plugin.getSpawnRegion().contains(e.getEntity().getLocation())) {
            e.setCancelled(true);
            return;
        }
        if (!(e.getEntity() instanceof Player))
            return;
        Player damagee = (Player) e.getEntity();
        if (lastProjLaunch.containsKey(damagee)) {
            addAssist(damagee, Bukkit.getPlayer(lastProjLaunch.get(damagee)), e.getDamage());
            lastProjLaunch.remove(damagee);
            return;
        }
        if (!(e.getDamager() instanceof Player))
            return;
        Player damager = (Player) e.getDamager();

        plugin.getStats(damager).getActiveKit().getKit().onDamageDealHit(e, damager, damagee);
        plugin.getStats(damagee).getActiveKit().getKit().onDamageGetHit(e, damager, damagee);

        if (!damager.equals(damagee))
            addAssist(damagee, damager, e.getDamage());
    }

    @EventHandler
    public void on(EntityTargetLivingEntityEvent event) {
        if (!(event.getEntity() instanceof Golem)) return;
        if (event.getEntity().getName().equalsIgnoreCase(event.getTarget().getName() + " golem")) event.setCancelled(true);
    }

    @EventHandler
    public void on(PlayerDeathEvent e) {
        e.getDrops().clear();
        e.setDeathMessage("");
        if (e.getEntity().isOnline()) plugin.respawn(e.getEntity());

        Player died = e.getEntity();
        Member diedMem = MemberManager.getInstance().getMember(died);
        KitPvPStats diedStats = plugin.getStats(diedMem);

        // Increase highest killstreak
        int diedStreak = diedStats.getStreak();
        if (diedStreak > diedStats.getHighestStreak())
            diedStats.setHighestStreak(diedStats.getStreak());

        // Give extra xp for a high ks.
        if (diedStreak > 5) {
            int rewardXp = 1;
            int streak = diedStreak;
            while (streak > 0) {
                rewardXp++;
                streak -= 5;
            }
            diedStats.getActiveKit().getKit().increaseXp(died, rewardXp);
        }

        // Increase deaths and reset ks
        diedStats.setDeaths(plugin.getStats(diedMem).getDeaths() + 1);
        diedStats.setStreak(0);

        died.getLocation().getWorld().playEffect(died.getLocation(), Effect.SMOKE, 1);
        plugin.getKitManager().getSignMap().remove(died.getUniqueId());

        // Try to get the killer
        Player killer = died.getKiller();
        if (killer == null && lastDamagerMap.get(died.getUniqueId()) != null) {
            String customName;
            customName = lastDamagerMap.get(died.getUniqueId()).getCustomName();
            if (customName == null)
                return;

            killer = Bukkit.getPlayer(customName.substring(0, customName.length()));
            lastDamagerMap.remove(died.getUniqueId());
        }
        if (killer == null || killer.equals(died))
            return;


        Member killerMem = MemberManager.getInstance().getMember(killer);
        diedMem.message("You got killed by " + killerMem.getName() + "§7.");
        killerMem.message("You killed " + diedMem.getName() + "§7.");

        // The same player got killed multiple times
        if (noKillRewards(killer, died))
            return;
        // On the same ip (anti alt boosting)
//        if (killer.getAddress().getAddress().getHostAddress()
//                .equals(diedMem.getPlayer().getAddress().getAddress().getHostAddress())) {
//            killerMem.message("§7The player you killed is on the §asame ip address§7, you got no rewards.");
//            return;
//        }

        //Extra credits when a high ks gets broken
        int killstreakCredits = diedStats.getStreak() >= 10 ? diedStats.getStreak() : 0;
        if (diedStats.getStreak() >= 10)
            killerMem.message("Killstreak broken, you got §a" + diedStats.getStreak() + " extra credits§7.");

        KitPvPStats stats = plugin.getStats(killerMem);

        // Give rewards to the killer
        stats.setKills(stats.getKills() + 1);
        stats.setStreak(plugin.getStats(killer).getStreak() + 1);

        int base = KitPvP.getInstance().getConfig().getInt("kill-credits");
        int extra = (int) Math.ceil(base * Math.pow((100 + KitPvP.getInstance().getConfig().getInt("kill-bonus-percentage"))/((double) 100), stats.getStreak() - 1));

        double modifier = KitPvP.getInstance().getConfig().getDouble("credits-modifier");
        int finalReward = (int) Math.ceil(modifier / (double) 100 * (extra + killstreakCredits));

        stats.setCoins(stats.getCoins() + finalReward);

        // Increase kit xp depending on the kit and level of the player who died.
        int rewardXp = (diedStats.getActiveKit().getKit().getPrice() / 2000) * diedStats.getKits().get(diedStats.getActiveKit()).getLevel();
        if (rewardXp < 1)
            rewardXp = 1;
        stats.getActiveKit().getKit().increaseXp(killer, rewardXp);

        checkAssist(died, killer);
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        Projectile proj = e.getEntity();
        if (!(proj.getShooter() instanceof Player))
            return;
        Player shooter = (Player) proj.getShooter();
        if (plugin.isInSpawnArea(shooter)) {
            e.setCancelled(true);
            return;
        }
        lastProjLaunch.put(proj, shooter.getUniqueId());

        Kit kit = plugin.getStats(shooter).getActiveKit().getKit();

        if (proj.getType() == EntityType.FISHING_HOOK) {
            if (kit.getKitType() == KitType.FISHERMAN)
                ((KitFisherman) kit).onRodUse(shooter, e);
        } else if (proj.getType() == EntityType.SNOWBALL) {
            if (kit.getKitType() == KitType.SHACO)
                ((KitShaco) kit).onSnowballUse(shooter, e);
            else if (kit.getKitType() == KitType.FROSTY)
                ((KitFrosty) kit).onSnowballUse(shooter, e);
        } else if (proj.getType() == EntityType.ARROW) {
            if (kit.getKitType() == KitType.ARCHER)
                ((KitArcher) kit).onArrowLaunch(shooter, e);
            else if (kit.getKitType() == KitType.FIREARCHER)
                ((KitFireArcher) kit).onArrowLaunch(shooter, e);
            else if (kit.getKitType() == KitType.SNIPER)
                ((KitSniper) kit).onArrowLaunch(shooter, e);
        }
    }

    @EventHandler
    public void onProjectileHit(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player))
            return;
        Player damagee = (Player) e.getEntity();

        Entity damager = e.getDamager();
        if (damager.getCustomName() != null)
            lastDamagerMap.put(damagee.getUniqueId(), e.getDamager());
        if (!(e.getDamager() instanceof Projectile))
            return;
        Projectile proj = (Projectile) damager;
        if (!(proj.getShooter() instanceof Player))
            return;
        Player shooter = (Player) proj.getShooter();
        if (plugin.isInSpawnArea(shooter) || plugin.isInSpawnArea(damagee))
            return;

        Kit damageeKit = plugin.getStats(damagee).getActiveKit().getKit();
        if (damageeKit.getKitType() == KitType.ENDERMAN) {
            ((KitEnderman) damageeKit).onArrowHit(shooter, damagee, e);
        }

        Kit kit = plugin.getStats(shooter).getActiveKit().getKit();
        if (kit.getKitType() == KitType.SHACO)
            ((KitShaco) kit).onSnowballHit(shooter, damagee);
        else if (kit.getKitType() == KitType.FROSTY)
            ((KitFrosty) kit).onSnowballHit(shooter, damagee);
        else if (kit.getKitType() == KitType.ARCHER)
            ((KitArcher) kit).onArrowHit(shooter, damagee, e);
        else if (kit.getKitType() == KitType.FIREARCHER)
            ((KitFireArcher) kit).onArrowHit(shooter, damagee, e);
        else if (kit.getKitType() == KitType.SNIPER)
            ((KitSniper) kit).onArrowHit(shooter, damagee, e);
    }

    private void addAssist(Player damaged, Player damager, double damage) {
        if (!killAssist.containsKey(damaged.getUniqueId())) {
            HashMap<UUID, Double> inner = new HashMap<>();
            inner.put(damager.getUniqueId(), damage);
            killAssist.put(damaged.getUniqueId(), inner);
            return;
        }
        if (!(killAssist.get(damaged.getUniqueId()).containsKey(damager.getUniqueId()))) {
            killAssist.get(damaged.getUniqueId()).put(damager.getUniqueId(), damage);
            return;
        }
        final HashMap<UUID, Double> inner = killAssist.get(damaged.getUniqueId());
        for (Map.Entry<UUID, Double> entry : inner.entrySet()) {
            double value = entry.getValue();
            value += damage;
            killAssist.get(damaged.getUniqueId()).put(entry.getKey(), value);
        }
    }

    private boolean noKillRewards(Player killer, Player killed) {
        HashMap<UUID, Integer> playerKilledMap = samePlayerKill.containsKey(killer.getUniqueId())
                ? samePlayerKill.get(killer.getUniqueId()) : new HashMap<>();
        if (!(samePlayerKill.containsKey(killer.getUniqueId()))) {
            playerKilledMap.put(killed.getUniqueId(), 1);
            samePlayerKill.put(killer.getUniqueId(), playerKilledMap);
            return false;
        }

        if (playerKilledMap.containsKey(killed.getUniqueId())) {
            int amount = playerKilledMap.get(killed.getUniqueId());
            amount++;
            playerKilledMap.put(killed.getUniqueId(), amount);
            samePlayerKill.put(killer.getUniqueId(), playerKilledMap);
            if (amount > 3) {
                killer.sendMessage("§7You killed the same player more than 3 times, §cno rewards §7rewarded.");
                return true;
            }
        } else {
            playerKilledMap.clear();
            playerKilledMap.put(killed.getUniqueId(), 1);
            samePlayerKill.put(killer.getUniqueId(), playerKilledMap);
        }
        return false;

    }

    private void checkAssist(Player killed, Player killer) {
        if (killAssist.containsKey(killed.getUniqueId())) {
            HashMap<UUID, Double> inner = killAssist.get(killed.getUniqueId());
            double total = 0.0;

            for (Map.Entry<UUID, Double> entry : inner.entrySet())
                total += entry.getValue();

            for (Map.Entry<UUID, Double> entry : inner.entrySet()) {
                Player keyPlayer = Bukkit.getPlayer(entry.getKey());
                if (keyPlayer != null) {
                    int credits = KitPvP.getInstance().getConfig().getInt("kill-credits");
                    double percentage = ((entry.getValue() * 100) / total) / 100;

                    if (keyPlayer != killed && keyPlayer != killer) {
                        int assist = (int) (credits * percentage);
                        if (assist > 0) {
                            keyPlayer.sendMessage("§7You got §6" + assist +
                                    " §7credits for assisting to kill " + killed.getName() + "§7!");

                            // Give xp reward
                            KitPvPStats diedStats = plugin.getStats(killed);
                            int rewardXp = ((diedStats.getActiveKit().getKit().getPrice() / 15000) * diedStats.getKits().get(diedStats.getActiveKit()).getLevel()) * (int) percentage;
                            if (rewardXp > 0)
                                plugin.getStats(keyPlayer).getActiveKit().getKit().increaseXp(keyPlayer, rewardXp);
                        }

                        plugin.getStats(keyPlayer).setCoins(plugin.getStats(keyPlayer).getCoins() + assist);
                        plugin.getStats(keyPlayer).setAssists(plugin.getStats(keyPlayer).getAssists() + 1);
                    }
                }
            }
            killAssist.remove(killed.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        lastDamagerMap.remove(uuid);
        killAssist.remove(uuid);
        samePlayerKill.remove(uuid);
        //plugin.getStats().remove(uuid); // todo: remove
    }

}
